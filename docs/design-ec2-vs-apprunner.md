# Design Doc: EC2 vs App Runner for Backend Deployment

**Date:** 2026-02-06
**Status:** Proposal
**Author:** Claude (Genevieve's intern)

## Context

The Poker High Hand Simulator backend is currently deployed on a single EC2 instance (t3.small) behind API Gateway. We're evaluating AWS App Runner as a simpler, lower-maintenance alternative.

## Current Architecture (EC2 + API Gateway)

```
Client -> API Gateway (HTTPS) -> EC2 (t3.small, port 8080) -> DynamoDB
```

**Components managed in Terraform:**
- VPC, public subnet, internet gateway, route table
- Security groups (inbound rules for API Gateway)
- IAM role + instance profile (DynamoDB access, SSM access)
- EC2 instance + Elastic IP
- API Gateway HTTP API v2 (HTTPS termination, CORS, CloudWatch logging)
- DynamoDB table

**Deploy process:** Push to GitHub, then SSM into EC2 to git pull, rebuild, restart systemd service (~60s).

### Pros

- **Full control.** Can SSH/SSM into the instance to debug, inspect logs, install tooling.
- **No CPU throttling.** EC2 provides dedicated CPU 24/7. Background simulation threads run at full speed regardless of whether HTTP requests are active.
- **Predictable cost.** Fixed ~$18/mo (EC2 $15 + API Gateway $1 + misc).
- **No cold starts.** The JVM is always warm. Responses are instant.
- **No request timeout concerns.** The app runs as a regular process; there are no platform-imposed timeouts on background work.
- **Flexible.** Can run any process, install any dependency, use any port, mount volumes, etc.

### Cons

- **Operational burden.** Must manage OS patching, monitoring, restarts, disk space. user_data bootstrap is fragile (we've already hit a `HOME: unbound variable` bug).
- **No auto-scaling.** Single instance = single point of failure. If the instance dies, the service is down until it's replaced.
- **Always-on cost.** Pays ~$15/mo whether handling 0 or 10,000 requests.
- **Complex deploy pipeline.** SSM-based deploy is custom scripting (deploy.sh). No built-in rollback.
- **Heavy Terraform footprint.** ~11 .tf files managing VPC, subnets, security groups, IAM, EIP, API Gateway, routes, stages, logging.

---

## Proposed Architecture (App Runner)

```
Client -> App Runner (HTTPS, auto-managed) -> DynamoDB
```

**Components managed in Terraform:**
- App Runner service (source: GitHub repo or ECR image)
- IAM role (DynamoDB access)
- DynamoDB table

Everything else (VPC, load balancer, TLS, scaling, health checks) is managed by App Runner.

**Deploy process:** Push to GitHub. App Runner auto-builds and deploys (~2-3 min).

### Pros

- **Zero infrastructure management.** No OS, no patching, no security groups, no VPC, no EIP, no API Gateway. App Runner handles HTTPS, load balancing, health checks, and rolling deploys.
- **Auto-deploy from GitHub.** Push to branch, App Runner builds and deploys automatically. No custom deploy scripts.
- **Built-in auto-scaling.** Scales from min to max instances based on concurrent requests. Handles traffic spikes.
- **Built-in HTTPS.** Provides a default `*.awsapprunner.com` URL with TLS. Custom domains supported.
- **Lower cost at low traffic.** With min 1 instance: ~$7/mo base (1 vCPU, 2 GB). Memory charged at $0.007/GB-hr; vCPU only charged during active request processing at $0.064/vCPU-hr. Potentially half the current EC2 cost.
- **Minimal Terraform.** ~2-3 resources instead of ~15+. Dramatically simpler IaC.
- **Built-in rolling deployments** with automatic rollback on health check failure.

### Cons

- **CPU throttling on idle — CRITICAL for this app.** App Runner throttles CPU when no inbound HTTP requests are being processed. Background simulation threads would be starved of CPU between polling requests. A simulation that takes 30 seconds on EC2 could take minutes on App Runner, or stall entirely if no one is polling. ([source](https://repost.aws/questions/QUEwbKE9jbTCyLn7OnqGhOrA/app-runner-scaling-for-background-service))
- **No SSH/debug access.** Cannot shell into the container to inspect state, tail logs, or debug. Only CloudWatch logs available.
- **Cold starts.** Java/Spring Boot apps take 10-30 seconds to start. If scaling from zero (or during deployments), first request sees significant latency.
- **Request timeout limit.** HTTP requests timeout at 120 seconds (some reports of 30s in practice). Not an issue for the POST endpoint (returns immediately) but is a platform constraint.
- **Deployment interrupts running simulations.** During rolling deploy, the old container is drained and terminated. Any in-flight background simulation threads are killed. No graceful handoff.
- **Stateless assumption.** App Runner doesn't guarantee container persistence. The in-memory simulation map could be lost at any time if the container is replaced.
- **Less networking control.** Can't easily restrict inbound traffic by IP (would need WAF). No VPC by default (can add VPC connector for outbound, but inbound is always public).

---

## Critical Issue: Background Simulation Threads

This is the most important factor in this decision.

**How the app works:**
1. `POST /simulations/start` spawns a background `Thread` that runs the simulation
2. Returns `202 Accepted` immediately with a UUID
3. The background thread runs for seconds to minutes, writing hand snapshots to DynamoDB as it goes
4. The frontend polls `GET /progress` and `GET /hands/{n}` to display results

**Why this matters for App Runner:**

App Runner is designed for request/response workloads. It [throttles CPU to near-zero](https://repost.aws/questions/QUEwbKE9jbTCyLn7OnqGhOrA/app-runner-scaling-for-background-service) when no HTTP requests are actively being processed. Background threads continue to exist but run extremely slowly.

Even with the frontend polling every 1-2 seconds, there are gaps between requests where CPU is throttled. The simulation thread would run in bursts (active during request processing) and crawl between requests. This would make simulation times unpredictable and significantly slower.

**Possible mitigations (all have tradeoffs):**
- Run the simulation synchronously in the request thread — but simulations can take minutes; the 120s request timeout would be a hard limit.
- Have the frontend send keep-alive pings every 100ms — hacky, wastes bandwidth, unreliable.
- Move simulation work to a separate ECS task or Lambda — adds complexity, defeats the simplicity argument.

---

## Cost Comparison (estimated monthly)

| Component | EC2 + API Gateway | App Runner |
|---|---|---|
| Compute | $15.00 (t3.small 24/7) | $5.04 (1 vCPU provisioned) + $0.58 (2GB memory provisioned) = **$5.62 base** |
| Active processing | included | $0.064/vCPU-hr (only during requests) |
| API Gateway | ~$1.00 | $0 (built-in) |
| EIP | $0 (attached) | N/A |
| Auto-deploy | N/A | $1.00/mo |
| Build minutes | N/A | ~$0.50/mo (estimated) |
| **Total (low traffic)** | **~$16/mo** | **~$7-8/mo** |
| **Total (moderate traffic)** | **~$16/mo** | **~$10-15/mo** |

App Runner is cheaper at low-to-moderate traffic. At sustained high traffic, costs converge or EC2 becomes cheaper.

---

## Recommendation

**Stay on EC2** for now. The CPU throttling issue is a dealbreaker for this app's background simulation workload. App Runner is designed for stateless request/response services, and this app relies on long-running background threads that need consistent CPU.

If the operational burden of EC2 becomes a real problem, better alternatives for this workload pattern would be:
- **ECS Fargate** — managed containers without CPU throttling, ~$13/mo for 0.5 vCPU / 1 GB. More Terraform than App Runner but far less than EC2+API Gateway. Supports long-running background tasks.
- **Refactor to async** — move simulation execution to a separate worker (SQS + Lambda or ECS task), keep the API on App Runner. Best architecture long-term but most implementation effort.

---

## References

- [AWS App Runner Pricing](https://aws.amazon.com/apprunner/pricing/)
- [App Runner Scaling for Background Services (AWS re:Post)](https://repost.aws/questions/QUEwbKE9jbTCyLn7OnqGhOrA/app-runner-scaling-for-background-service)
- [App Runner Request Timeout Discussion](https://repost.aws/questions/QUnozEpub5Tnq-ilmrB4l2Sg/aws-app-runner-what-exactly-does-the-timeout-limit-documentation-mean)
- [App Runner Deep Dive](https://tty.neveragain.de/2021/06/18/app-runner-deep-dive.html)
