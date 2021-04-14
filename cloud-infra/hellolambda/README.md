## 事前準備
```bash
$ gradle init --type kotlin-application --dsl kotlin
```

## 実行
```bash
$ ./gradlew run
```

## コンパイル + AWSアップロード
```bash
$ ./gradlew build
$ aws lambda create-function --region ap-northeast-1 --function-name hellolambda --zip-file fileb://build/distributions/hellolambda.zip --role arn:aws:iam::***:role/lambda_basic_execution --handler hellolambda.App::handler --runtime java8 --timeout 15 --memory-size 512 --profile user

// 更新
$ aws lambda update-function-code --region ap-northeast-1 --function-name hellolambda --zip-file fileb://build/distributions/hellolambda.zip --profile user
```