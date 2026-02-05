# ---------- API Gateway ----------

output "api_gateway_url" {
  description = "API Gateway base URL (use this for frontend)"
  value       = aws_apigatewayv2_api.main.api_endpoint
}

output "api_gateway_endpoints" {
  description = "API endpoints for the frontend to use"
  value = {
    base_url           = aws_apigatewayv2_api.main.api_endpoint
    start_simulation   = "${aws_apigatewayv2_api.main.api_endpoint}/simulations/start"
    get_status         = "${aws_apigatewayv2_api.main.api_endpoint}/simulations/{simulationID}/status"
    get_hand           = "${aws_apigatewayv2_api.main.api_endpoint}/simulations/{simulationID}/hands/{handNum}"
    get_progress       = "${aws_apigatewayv2_api.main.api_endpoint}/simulations/{simulationID}/progress"
  }
}

output "api_gateway_id" {
  description = "API Gateway ID"
  value       = aws_apigatewayv2_api.main.id
}

# ---------- EC2 (direct access - for debugging only) ----------

output "backend_public_ip" {
  description = "Elastic IP of the backend EC2 instance (use API Gateway URL instead)"
  value       = aws_eip.backend.public_ip
}

output "api_base_url_direct" {
  description = "Direct EC2 URL (bypass API Gateway - for debugging only)"
  value       = "http://${aws_eip.backend.public_ip}:8080"
}

output "ssm_connect_command" {
  description = "Connect to the instance via SSM Session Manager (no SSH key needed)"
  value       = "aws ssm start-session --target ${aws_instance.backend.id}"
}

output "ssh_command" {
  description = "SSH command (only works when key_pair_name is set)"
  value       = var.key_pair_name != null ? "ssh -i ~/.ssh/${var.key_pair_name}.pem ec2-user@${aws_eip.backend.public_ip}" : "SSH disabled — use SSM instead"
}

# ---------- DynamoDB ----------

output "dynamodb_table_name" {
  description = "Name of the DynamoDB hands table"
  value       = aws_dynamodb_table.hands.name
}

output "dynamodb_table_arn" {
  description = "ARN of the DynamoDB hands table"
  value       = aws_dynamodb_table.hands.arn
}

# ---------- Networking ----------

output "vpc_id" {
  description = "VPC ID"
  value       = aws_vpc.main.id
}
