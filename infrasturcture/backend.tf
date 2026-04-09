# Uncomment after bootstrapping the S3 bucket and DynamoDB table.
# See README.md "Infrastructure" section for bootstrap instructions.
#
# terraform {
#   backend "s3" {
#     bucket         = "poker-sim-terraform-state"
#     key            = "terraform.tfstate"
#     region         = "us-east-1"
#     dynamodb_table = "poker-sim-terraform-locks"
#     encrypt        = true
#   }
# }
