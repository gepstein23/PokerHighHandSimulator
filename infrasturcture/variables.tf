# ---------- General ----------

variable "aws_region" {
  description = "AWS region for all resources"
  type        = string
  default     = "us-east-1"
}

variable "project_name" {
  description = "Project name used for resource naming and tagging"
  type        = string
  default     = "poker-sim"
}

variable "environment" {
  description = "Deployment environment (e.g. dev, staging, prod)"
  type        = string
  default     = "dev"
}

# ---------- Networking ----------

variable "vpc_cidr" {
  description = "CIDR block for the VPC"
  type        = string
  default     = "10.0.0.0/16"
}

variable "ssh_cidr" {
  description = "CIDR block allowed to SSH (e.g. \"1.2.3.4/32\"). Only used when key_pair_name is set."
  type        = string
  default     = null

  validation {
    condition     = var.ssh_cidr == null || var.ssh_cidr != "0.0.0.0/0"
    error_message = "ssh_cidr must not be 0.0.0.0/0. Restrict to your IP, e.g. \"1.2.3.4/32\"."
  }
}

# ---------- EC2 ----------

variable "instance_type" {
  description = "EC2 instance type"
  type        = string
  default     = "t3.small"
}

variable "key_pair_name" {
  description = "Name of an existing EC2 key pair for SSH access (leave null to disable SSH — use SSM instead)"
  type        = string
  default     = null
}

# ---------- API Gateway ----------

variable "frontend_ip" {
  description = "IP address of the frontend allowed to access the API Gateway (e.g. \"1.2.3.4\"). Leave null to allow all."
  type        = string
  default     = null
}

# ---------- Application ----------

variable "github_repo_url" {
  description = "HTTPS URL of the GitHub repository to clone"
  type        = string
  default     = "https://github.com/gepstein23/PokerHighHandSimulator.git"
}

variable "github_branch" {
  description = "Git branch to checkout on the EC2 instance"
  type        = string
  default     = "master"
}
