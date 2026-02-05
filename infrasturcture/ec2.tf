# ---------- Backend EC2 Instance ----------

resource "aws_instance" "backend" {
  ami                         = data.aws_ami.amazon_linux.id
  instance_type               = var.instance_type
  subnet_id                   = aws_subnet.public.id
  vpc_security_group_ids      = [aws_security_group.backend.id]
  iam_instance_profile        = aws_iam_instance_profile.backend.name
  key_name                    = var.key_pair_name
  user_data_replace_on_change = true

  user_data = templatefile("${path.module}/templates/user_data.sh", {
    aws_region     = var.aws_region
    dynamodb_table = aws_dynamodb_table.hands.name
    github_repo    = var.github_repo_url
    github_branch  = var.github_branch
  })

  # Require IMDSv2 -- prevents SSRF credential theft from the metadata service
  metadata_options {
    http_endpoint = "enabled"
    http_tokens   = "required" # IMDSv2 only
  }

  root_block_device {
    volume_size = 30
    volume_type = "gp3"
    encrypted   = true
  }

  tags = { Name = "${local.name_prefix}-backend" }
}

# ---------- Elastic IP ----------

resource "aws_eip" "backend" {
  instance = aws_instance.backend.id
  domain   = "vpc"

  tags = { Name = "${local.name_prefix}-backend-eip" }
}
