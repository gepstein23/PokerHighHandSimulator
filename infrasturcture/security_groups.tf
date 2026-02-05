# ---------- Backend EC2 Security Group ----------

resource "aws_security_group" "backend" {
  name_prefix = "${local.name_prefix}-backend-"
  description = "Allow API traffic from frontend and optional SSH access"
  vpc_id      = aws_vpc.main.id

  lifecycle {
    create_before_destroy = true
  }

  tags = { Name = "${local.name_prefix}-backend-sg" }
}

# API — open to the internet (frontend runs in browsers)
resource "aws_vpc_security_group_ingress_rule" "api" {
  security_group_id = aws_security_group.backend.id
  description       = "Spring Boot API (frontend access)"
  from_port         = 8080
  to_port           = 8080
  ip_protocol       = "tcp"
  cidr_ipv4         = "0.0.0.0/0"
}

# SSH — only created when a key pair is configured, locked to a specific CIDR
resource "aws_vpc_security_group_ingress_rule" "ssh" {
  count = var.key_pair_name != null ? 1 : 0

  security_group_id = aws_security_group.backend.id
  description       = "SSH (restricted to ssh_cidr)"
  from_port         = 22
  to_port           = 22
  ip_protocol       = "tcp"
  cidr_ipv4         = var.ssh_cidr

  tags = { Name = "${local.name_prefix}-ssh" }
}

# Outbound — required for package installs, git clone, AWS API calls
resource "aws_vpc_security_group_egress_rule" "all_outbound" {
  security_group_id = aws_security_group.backend.id
  description       = "All outbound"
  ip_protocol       = "-1"
  cidr_ipv4         = "0.0.0.0/0"
}
