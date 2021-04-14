## IAM
resource "aws_iam_role" "iam_for_lambda" {
  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": "sts:AssumeRole",
      "Principal": {
        "Service": "lambda.amazonaws.com"
      },
      "Effect": "Allow",
      "Sid": ""
    }
  ]
}
EOF
}

resource "aws_iam_policy" "write_cwl" {
  policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": [
        "logs:CreateLogGroup",
        "logs:CreateLogStream",
        "logs:PutLogEvents",
        "logs:DescribeLogStreams"
      ],
      "Resource": "arn:aws:logs:*:*:*",
      "Effect": "Allow"
    }
  ]
}
EOF
}

resource "aws_iam_role_policy_attachment" "attach_read_dynamodb" {
  role       = aws_iam_role.iam_for_lambda.name
  policy_arn = aws_iam_policy.read_dynamodb.arn
}

resource "aws_iam_policy" "allow_sfn" {
  policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "states:StartExecution",
        "states:StopExecution"
      ],
      "Resource": "*"
    }
  ]
}
EOF
}

resource "aws_iam_role_policy_attachment" "attach_allow_sfn" {
  role       = aws_iam_role.iam_for_lambda.name
  policy_arn = aws_iam_policy.allow_sfn.arn
}

resource "aws_iam_role_policy_attachment" "lambda_logs" {
  role = aws_iam_role.iam_for_lambda.name
  policy_arn = aws_iam_policy.write_cwl.arn
}

## Lambda
data "aws_caller_identity" "current" {}

resource "aws_lambda_function" "enqueue_lambda" {
  filename      = "../application/enqueue/build/distributions/enqueue.zip"
  function_name = "serverless-schedule-enqueue"
  role          = aws_iam_role.iam_for_lambda.arn
  handler       = "enqueue.Handler::handleRequest"

  memory_size = 256
  timeout     = 60

  source_code_hash = filebase64sha256("../application/enqueue/build/distributions/enqueue.zip")

  runtime = "java8"

  environment {
    variables = {
      STATE_MACHINE_ARN    = aws_sfn_state_machine.push_prep_state_machine.id
      EXECUTION_ARN_PREFIX = "arn:aws:states:ap-northeast-1:${data.aws_caller_identity.current.account_id}:execution:${aws_sfn_state_machine.push_prep_state_machine.name}:"
    }
  }
}

resource "aws_lambda_event_source_mapping" "push_table_trigger" {
  depends_on = [
    "aws_iam_role_policy_attachment.attach_read_dynamodb",
    "aws_iam_role_policy_attachment.lambda_logs",
    "aws_cloudwatch_log_group.enqueue_lambda_log"
  ]

  batch_size        = 100
  event_source_arn  = aws_dynamodb_table.push_table.stream_arn
  function_name     = aws_lambda_function.enqueue_lambda.arn
  enabled           = true
  starting_position = "TRIM_HORIZON"
}

resource "aws_cloudwatch_log_group" "enqueue_lambda_log" {
  name = "/aws/lambda/${aws_lambda_function.enqueue_lambda.function_name}"
}