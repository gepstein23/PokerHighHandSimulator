#!/bin/bash
set -euo pipefail

# ============================================================
# deploy.sh — Build, push, and deploy Poker High Hand Simulator
#
# Deploys by SSM-ing into the live EC2 to git pull, rebuild,
# and restart the service. No instance replacement needed.
#
# Usage:
#   ./deploy.sh              # Build locally, push, deploy via SSM
#   ./deploy.sh --plan-only  # Build and push only, skip deploy
#   ./deploy.sh --infra      # Also run terraform plan/apply (for infra changes)
#
# Prerequisites:
#   - Git configured with push access to origin
#   - AWS CLI configured with SSM permissions
#   - Terraform installed (only needed with --infra)
# ============================================================

MODE="deploy"
if [[ "${1:-}" == "--plan-only" ]]; then
  MODE="plan-only"
elif [[ "${1:-}" == "--infra" ]]; then
  MODE="infra"
fi

REPO_ROOT="$(cd "$(dirname "$0")" && pwd)"
BACKEND_DIR="$REPO_ROOT/poker-high-hand-simulator-backend"
INFRA_DIR="$REPO_ROOT/infrasturcture"

# ──────────────────────────────────────────────
# 1. Build and test locally
# ──────────────────────────────────────────────
echo "==> Building and testing backend..."
cd "$BACKEND_DIR"
./mvnw clean package -q
echo "==> Build succeeded (all tests passed)."

# ──────────────────────────────────────────────
# 2. Commit and push current branch
# ──────────────────────────────────────────────
cd "$REPO_ROOT"
CURRENT_BRANCH="$(git branch --show-current)"

if [[ -n "$(git status --porcelain)" ]]; then
  echo ""
  echo "==> Uncommitted changes detected:"
  git status --short
  echo ""
  read -rp "Commit all changes before deploying? (y/n) " COMMIT_ANSWER
  if [[ "$COMMIT_ANSWER" == "y" ]]; then
    read -rp "Commit message: " COMMIT_MSG
    git add -A
    git commit -m "$COMMIT_MSG"
  else
    echo "Aborting — commit or stash changes first."
    exit 1
  fi
fi

echo "==> Pushing $CURRENT_BRANCH to origin..."
git push origin "$CURRENT_BRANCH"

if [[ "$MODE" == "plan-only" ]]; then
  echo "==> Plan-only mode. Code pushed but not deployed."
  exit 0
fi

# ──────────────────────────────────────────────
# 3. (Optional) Terraform for infra changes
# ──────────────────────────────────────────────
if [[ "$MODE" == "infra" ]]; then
  echo ""
  echo "==> Running terraform plan..."
  cd "$INFRA_DIR"
  terraform init -input=false -no-color
  terraform plan -out=tfplan
  echo ""
  read -rp "Apply this plan? (y/n) " APPLY_ANSWER
  if [[ "$APPLY_ANSWER" == "y" ]]; then
    terraform apply tfplan
  else
    echo "==> Terraform apply skipped."
  fi
fi

# ──────────────────────────────────────────────
# 4. Deploy code via SSM
# ──────────────────────────────────────────────
echo ""
echo "==> Getting EC2 instance ID from terraform..."
cd "$INFRA_DIR"
INSTANCE_ID="$(terraform output -raw instance_id)"

echo "==> Deploying to instance $INSTANCE_ID via SSM..."
COMMAND_ID=$(aws ssm send-command \
  --instance-ids "$INSTANCE_ID" \
  --document-name "AWS-RunShellScript" \
  --parameters commands='[
    "set -euxo pipefail",
    "cd /opt/app",
    "git fetch origin",
    "git checkout '"$CURRENT_BRANCH"'",
    "git pull origin '"$CURRENT_BRANCH"'",
    "cd poker-high-hand-simulator-backend",
    "./mvnw clean package -DskipTests",
    "sudo systemctl restart poker-sim"
  ]' \
  --timeout-seconds 300 \
  --comment "deploy $CURRENT_BRANCH" \
  --output text \
  --query "Command.CommandId")

echo "==> SSM command sent: $COMMAND_ID"
echo "==> Waiting for deploy to complete..."

# Poll for completion
aws ssm wait command-executed \
  --command-id "$COMMAND_ID" \
  --instance-id "$INSTANCE_ID" 2>/dev/null || true

# Get result
STATUS=$(aws ssm get-command-invocation \
  --command-id "$COMMAND_ID" \
  --instance-id "$INSTANCE_ID" \
  --query "Status" --output text)

if [[ "$STATUS" == "Success" ]]; then
  echo "==> Deploy complete! Service restarted on $INSTANCE_ID with branch $CURRENT_BRANCH."
else
  echo "==> Deploy FAILED (status: $STATUS). Fetching logs..."
  aws ssm get-command-invocation \
    --command-id "$COMMAND_ID" \
    --instance-id "$INSTANCE_ID" \
    --query "[StandardOutputContent, StandardErrorContent]" --output text
  exit 1
fi
