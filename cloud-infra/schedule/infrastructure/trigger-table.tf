resource "aws_iam_policy" "read_dynamodb" {
  policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": [
        "dynamodb:GetRecords",
        "dynamodb:GetShardIterator",
        "dynamodb:DescribeStream",
        "dynamodb:ListStreams"
      ],
      "Effect": "Allow",
      "Resource": "*"
    }
  ]
}
EOF
}

resource "aws_dynamodb_table" "push_table" {
  name         = "Push"
  billing_mode = "PROVISIONED"

  read_capacity  = 20
  write_capacity = 20

  hash_key  = "PushId"

  stream_enabled   = true
  stream_view_type = "NEW_AND_OLD_IMAGES"

  attribute {
    name = "PushId"
    type = "S"
  }
}