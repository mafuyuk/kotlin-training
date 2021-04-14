resource "aws_iam_role" "iam_for_sfn" {
  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": "sts:AssumeRole",
      "Principal": {
        "Service": "states.ap-northeast-1.amazonaws.com"
      },
      "Effect": "Allow",
      "Sid": ""
    }
  ]
}
EOF
}

resource "aws_iam_policy" "allow_sns_topic" {
  policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": [
        "sns:Publish"
      ],
      "Resource": "${aws_sns_topic.push.arn}",
      "Effect": "Allow"
    }
  ]
}
EOF
}

resource "aws_iam_role_policy_attachment" "attach_sns_topic" {
  role       = aws_iam_role.iam_for_sfn.name
  policy_arn = aws_iam_policy.allow_sns_topic.arn
}

resource "aws_iam_policy" "allow_ddb_topic" {
  policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": [
        "dynamodb:UpdateItem"
      ],
      "Resource": "${aws_dynamodb_table.push_table.arn}",
      "Effect": "Allow"
    }
  ]
}
EOF
}

resource "aws_iam_role_policy_attachment" "attach_ddb_topic" {
  role       = aws_iam_role.iam_for_sfn.name
  policy_arn = aws_iam_policy.allow_ddb_topic.arn
}

resource "aws_sfn_state_machine" "push_prep_state_machine" {
  name = "push-prep-state-machine"
  role_arn = aws_iam_role.iam_for_sfn.arn

  definition = <<EOF
{
  "StartAt": "ChoiceState",
  "States": {
    "ChoiceState": {
      "Type": "Choice",
      "Choices":[
        {
          "Variable": "$.ChoiceState",
          "NumericEquals": 1,
          "Next": "PublishToSNS"
        },
        {
          "Variable": "$.ChoiceState",
          "NumericEquals": 2,
          "Next": "WaitForTimestamp"
        }
      ]
    },
    "WaitForTimestamp": {
      "Type": "Wait",
      "TimestampPath": "$.ScheduleFor",
      "Next": "PublishToSNS"
    },
    "PublishToSNS": {
     "Type": "Task",
     "Resource": "arn:aws:states:::sns:publish",
     "InputPath": "$.Message",
     "ResultPath": "$.Result.PublishToSNS",
     "OutputPath": "$",
     "Parameters": {
       "TopicArn": "${aws_sns_topic.push.arn}",
       "Message.$": "$"
     },
     "Next": "UpdateDB"
    },
    "UpdateDB": {
      "Type": "Task",
      "Resource": "arn:aws:states:::dynamodb:updateItem",
      "Parameters": {
        "TableName": "${aws_dynamodb_table.push_table.name}",
        "Key": {"PushId": {"S.$": "$.PushId"}},
        "UpdateExpression": "set #key1 = :val1",
        "ExpressionAttributeNames": {"#key1": "PublishedAt"},
        "ExpressionAttributeValues": {":val1": {"N.$": "$.PublishedAt"}}
      },
      "End": true
   }
  }
}
EOF

}