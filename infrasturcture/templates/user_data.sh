#!/bin/bash
set -euxo pipefail

# ──────────────────────────────────────────────
# 1. Install dependencies
# ──────────────────────────────────────────────
dnf install -y java-17-amazon-corretto-devel git

# ──────────────────────────────────────────────
# 2. Clone repository
# ──────────────────────────────────────────────
cd /opt
git clone --branch ${github_branch} ${github_repo} app

# ──────────────────────────────────────────────
# 3. Build the application
# ──────────────────────────────────────────────
cd /opt/app/poker-high-hand-simulator-backend
chmod +x mvnw
./mvnw clean package -DskipTests

# ──────────────────────────────────────────────
# 4. Write environment file
# ──────────────────────────────────────────────
cat > /etc/poker-sim.env <<EOF
AWS_REGION=${aws_region}
DYNAMODB_TABLE=${dynamodb_table}
EOF

# ──────────────────────────────────────────────
# 5. Create systemd service
# ──────────────────────────────────────────────
cat > /etc/systemd/system/poker-sim.service <<'UNIT'
[Unit]
Description=Poker High Hand Simulator Backend
After=network.target

[Service]
Type=simple
User=ec2-user
EnvironmentFile=/etc/poker-sim.env
ExecStart=/bin/bash -c 'exec java -jar /opt/app/poker-high-hand-simulator-backend/target/poker-high-hand-simulator-0.0.1-SNAPSHOT.jar'
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
UNIT

# ──────────────────────────────────────────────
# 6. Start service
# ──────────────────────────────────────────────
chown -R ec2-user:ec2-user /opt/app
systemctl daemon-reload
systemctl enable poker-sim
systemctl start poker-sim
