# ---------- EC2 Assume-Role Policy ----------

data "aws_iam_policy_document" "ec2_assume_role" {
  statement {
    actions = ["sts:AssumeRole"]

    principals {
      type        = "Service"
      identifiers = ["ec2.amazonaws.com"]
    }
  }
}

# ---------- IAM Role ----------

resource "aws_iam_role" "backend" {
  name               = "${local.name_prefix}-backend"
  assume_role_policy = data.aws_iam_policy_document.ec2_assume_role.json

  tags = { Name = "${local.name_prefix}-backend-role" }
}

# ---------- DynamoDB Access (least privilege) ----------

data "aws_iam_policy_document" "dynamodb_access" {
  statement {
    sid    = "HandsTableReadWrite"
    effect = "Allow"

    actions = [
      "dynamodb:PutItem",
      "dynamodb:GetItem",
      "dynamodb:Query",
      "dynamodb:UpdateItem",
      "dynamodb:DeleteItem",
      "dynamodb:BatchWriteItem",
      "dynamodb:BatchGetItem",
    ]

    resources = [
      aws_dynamodb_table.hands.arn,
    ]
  }
}

resource "aws_iam_role_policy" "dynamodb_access" {
  name   = "dynamodb-hands-access"
  role   = aws_iam_role.backend.id
  policy = data.aws_iam_policy_document.dynamodb_access.json
}

# ---------- SSM Session Manager (secure shell replacement) ----------

resource "aws_iam_role_policy_attachment" "ssm" {
  role       = aws_iam_role.backend.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonSSMManagedInstanceCore"
}

# ---------- Instance Profile ----------

resource "aws_iam_instance_profile" "backend" {
  name = "${local.name_prefix}-backend"
  role = aws_iam_role.backend.name

  tags = { Name = "${local.name_prefix}-backend-profile" }
}
