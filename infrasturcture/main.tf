# Resources are organized across dedicated files:
#
#   vpc.tf              - VPC, subnet, internet gateway, routing
#   security_groups.tf  - Inbound/outbound rules for the backend
#   iam.tf              - EC2 role, DynamoDB policy, instance profile
#   dynamodb.tf         - Hands table (simulation_id + hand_number)
#   ec2.tf              - Backend instance + elastic IP
#   api_gateway.tf      - HTTP API Gateway routing to EC2
#   data.tf             - AMI lookup, availability zones
#   locals.tf           - Shared naming prefix
