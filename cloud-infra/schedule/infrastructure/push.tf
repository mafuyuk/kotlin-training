resource "aws_sns_topic" "push" {
  name = "push"
}

resource "aws_lambda_function" "push" {
  filename      = "../application/push/build/distributions/push.zip"
  function_name = "serverless-schedule-push"
  role          = aws_iam_role.iam_for_lambda.arn
  handler       = "push.Handler::handleRequest"

  source_code_hash = filebase64sha256("../application/push/build/distributions/push.zip")

  runtime = "java8"
}

resource "aws_cloudwatch_log_group" "push_lambda_log" {
  name = "/aws/lambda/${aws_lambda_function.push.function_name}"
}