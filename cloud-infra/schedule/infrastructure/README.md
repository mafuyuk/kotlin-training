# 事前準備
```bash
$ brew install tfenv
```

# 環境構築
利用する際には以下の修正が必要
`main.tf`のprovider.aws.profile
`push.tf`のresource.aws_sns_platform_application.gcm_application.platform_credential

```bash
$ terraform fmt
$ terraform init
$ terraform plan
$ terraform apply
```

# 参照
- https://docs.aws.amazon.com/ja_jp/step-functions/latest/dg/connect-sns.html
- https://docs.aws.amazon.com/ja_jp/step-functions/latest/dg/connect-ddb.html