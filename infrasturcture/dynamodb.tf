# ---------- Hands Table ----------
#
# Stores the output of each dealt hand.
#
# Primary key:
#   simulation_id (partition) -- groups all hands in one simulation run
#   hand_number   (sort)      -- sequential hand within the simulation
#
# Each item holds the full hand snapshot: per-table player cards,
# community cards, winning hand, qualification status, and running stats.

resource "aws_dynamodb_table" "hands" {
  name         = "${local.name_prefix}-hands"
  billing_mode = "PAY_PER_REQUEST" # On-demand: pay per read/write + $0.25/GB storage; ideal for bursty writes with frequent deletes
  hash_key     = "simulation_id"
  range_key    = "hand_number"

  attribute {
    name = "simulation_id"
    type = "S"
  }

  attribute {
    name = "hand_number"
    type = "N"
  }

  point_in_time_recovery {
    enabled = true
  }

  server_side_encryption {
    enabled = true # Uses AWS-owned KMS key (no extra cost)
  }

  ttl {
    attribute_name = "ttl"
    enabled        = true
  }

  deletion_protection_enabled = true

  tags = { Name = "${local.name_prefix}-hands" }
}
