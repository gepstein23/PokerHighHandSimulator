#!/bin/bash
set -euo pipefail

# ============================================================
# deploy.sh — Build, push, and deploy Poker High Hand Simulator
#
# Usage:
#   ./deploy.sh              # Full deploy: build, push, merge to master, terraform plan/apply
#   ./deploy.sh --plan-only  # Everything except terraform apply
#
# Prerequisites:
#   - Git configured with push access to origin
#   - AWS CLI configured (terraform needs credentials)
#   - Terraform installed
# ============================================================

PLAN_ONLY=false
if [[ "${1:-}" == "--plan-only" ]]; then
  PLAN_ONLY=true
fi

REPO_ROOT="$(cd "$(dirname "$0")" && pwd)"
BACKEND_DIR="$REPO_ROOT/poker-high-hand-simulator-backend"
INFRA_DIR="$REPO_ROOT/infrasturcture"  # Note: directory has a typo, kept intentionally
DEPLOY_BRANCH="master"

# ──────────────────────────────────────────────
# 1. Build and test
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

# ──────────────────────────────────────────────
# 3. Merge into deploy branch (master)
# ──────────────────────────────────────────────
if [[ "$CURRENT_BRANCH" != "$DEPLOY_BRANCH" ]]; then
  echo "==> Merging $CURRENT_BRANCH into $DEPLOY_BRANCH..."
  git checkout "$DEPLOY_BRANCH"
  git pull origin "$DEPLOY_BRANCH"
  git merge "$CURRENT_BRANCH" --no-edit
  git push origin "$DEPLOY_BRANCH"
  git checkout "$CURRENT_BRANCH"
  echo "==> Merged and pushed $DEPLOY_BRANCH."
else
  echo "==> Already on $DEPLOY_BRANCH, skipping merge."
fi

# ──────────────────────────────────────────────
# 4. Terraform plan
# ──────────────────────────────────────────────
echo ""
echo "==> Running terraform plan..."
cd "$INFRA_DIR"
terraform init -input=false -no-color
terraform plan -out=tfplan

# ──────────────────────────────────────────────
# 5. Terraform apply (unless --plan-only)
# ──────────────────────────────────────────────
if [[ "$PLAN_ONLY" == true ]]; then
  echo ""
  echo "==> Plan-only mode. Run 'cd $INFRA_DIR && terraform apply tfplan' to apply."
  exit 0
fi

echo ""
read -rp "Apply this plan? (y/n) " APPLY_ANSWER
if [[ "$APPLY_ANSWER" == "y" ]]; then
  terraform apply tfplan
  echo ""
  echo "==> Deploy complete. EC2 instance is being replaced with latest code from $DEPLOY_BRANCH."
  echo "    Note: The new instance needs ~2-3 minutes to clone, build, and start the service."
else
  echo "==> Apply skipped. Saved plan is at: $INFRA_DIR/tfplan"
fi
