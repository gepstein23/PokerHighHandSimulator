# ---------- HTTP API Gateway ----------
#
# Routes all requests to the EC2 backend via HTTP proxy integration.
# Uses API Gateway v2 (HTTP API) for lower latency and cost.

resource "aws_apigatewayv2_api" "main" {
  name          = "${local.name_prefix}-api"
  protocol_type = "HTTP"
  description   = "Poker High Hand Simulator API Gateway"

  cors_configuration {
    allow_origins = ["*"]
    allow_methods = ["GET", "POST", "PUT", "DELETE", "OPTIONS"]
    allow_headers = ["Content-Type", "Authorization", "X-Requested-With"]
    max_age       = 3600
  }

  tags = { Name = "${local.name_prefix}-api-gateway" }
}

# ---------- VPC Link (for private integration) ----------
#
# Note: Since the EC2 has a public IP and we want simple setup,
# we use a public HTTP integration instead of VPC Link.
# For production, consider using VPC Link with ALB/NLB.

# ---------- Integration ----------
#
# Proxy all requests to the EC2 backend

resource "aws_apigatewayv2_integration" "backend" {
  api_id             = aws_apigatewayv2_api.main.id
  integration_type   = "HTTP_PROXY"
  integration_method = "ANY"
  integration_uri    = "http://${aws_eip.backend.public_ip}:8080/{proxy}"

  # Pass all headers and query params
  payload_format_version = "1.0"
}

# ---------- Routes ----------

# Catch-all route for all paths and methods
resource "aws_apigatewayv2_route" "proxy" {
  api_id    = aws_apigatewayv2_api.main.id
  route_key = "ANY /{proxy+}"
  target    = "integrations/${aws_apigatewayv2_integration.backend.id}"
}

# Root path route
resource "aws_apigatewayv2_route" "root" {
  api_id    = aws_apigatewayv2_api.main.id
  route_key = "ANY /"
  target    = "integrations/${aws_apigatewayv2_integration.backend.id}"
}

# ---------- Stage ----------

resource "aws_apigatewayv2_stage" "default" {
  api_id      = aws_apigatewayv2_api.main.id
  name        = "$default"
  auto_deploy = true

  access_log_settings {
    destination_arn = aws_cloudwatch_log_group.api_gateway.arn
    format = jsonencode({
      requestId        = "$context.requestId"
      ip               = "$context.identity.sourceIp"
      requestTime      = "$context.requestTime"
      httpMethod       = "$context.httpMethod"
      routeKey         = "$context.routeKey"
      status           = "$context.status"
      responseLength   = "$context.responseLength"
      integrationError = "$context.integrationErrorMessage"
    })
  }

  tags = { Name = "${local.name_prefix}-api-stage" }
}

# ---------- CloudWatch Logs ----------

resource "aws_cloudwatch_log_group" "api_gateway" {
  name              = "/aws/apigateway/${local.name_prefix}-api"
  retention_in_days = 14

  tags = { Name = "${local.name_prefix}-api-logs" }
}

# ---------- WAF: IP Restriction ----------
#
# When frontend_ip is set, only that IP can reach the API Gateway.
# All other requests receive 403 Forbidden.

resource "aws_wafv2_ip_set" "frontend" {
  count              = var.frontend_ip != null ? 1 : 0
  name               = "${local.name_prefix}-frontend-ip"
  scope              = "REGIONAL"
  ip_address_version = "IPV4"
  addresses          = ["${var.frontend_ip}/32"]

  tags = { Name = "${local.name_prefix}-frontend-ip-set" }
}

resource "aws_wafv2_web_acl" "api" {
  count       = var.frontend_ip != null ? 1 : 0
  name        = "${local.name_prefix}-api-acl"
  scope       = "REGIONAL"
  description = "Allow only the frontend IP to access the API Gateway"

  default_action {
    block {}
  }

  rule {
    name     = "allow-frontend-ip"
    priority = 1

    action {
      allow {}
    }

    statement {
      ip_set_reference_statement {
        arn = aws_wafv2_ip_set.frontend[0].arn
      }
    }

    visibility_config {
      sampled_requests_enabled   = true
      cloudwatch_metrics_enabled = true
      metric_name                = "${local.name_prefix}-allow-frontend"
    }
  }

  visibility_config {
    sampled_requests_enabled   = true
    cloudwatch_metrics_enabled = true
    metric_name                = "${local.name_prefix}-api-acl"
  }

  tags = { Name = "${local.name_prefix}-api-acl" }
}

resource "aws_wafv2_web_acl_association" "api" {
  count        = var.frontend_ip != null ? 1 : 0
  resource_arn = aws_apigatewayv2_stage.default.arn
  web_acl_arn  = aws_wafv2_web_acl.api[0].arn
}
