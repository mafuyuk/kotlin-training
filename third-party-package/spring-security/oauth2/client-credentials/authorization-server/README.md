# Authorization Server Sample
Authorization Server Sampleといいつつも
Resource ServerにJWK setURLを用意し、トークンの検証を行うようにする実装

## 環境構築
- リソースサーバー(一部認可サーバーの機能含む)構築
  リソースサーバー(一部認可サーバーの機能含む)はKotlin + Spring Boot + Spring Securityで構築
  ```bash
  user@host: ~/workspace $ ./gradlew bootRun
  ```

## OAuthトランザクションの動作確認
```
+--------+                                +---------------+
|        |----(1) Credential ------------>|      +--------|
|        |                                |<---> |Auth Mod|<-----|
|        |<---(2) Access Token -----------|      +--------|      |
|        |                                |               |      |
|        |                                |               |      |
| Client |                                |   Resource    |      | (4) Request Public Keys
|        |                                |    Server     |      |     Response JWK
|        |----(3) API Call with JWT ----->|               |------|
|        |                                |               |
|        |                                |               |------| (5) Verify Signature
|        |<---(7) API Response -----------|               |<-----| (6) Verify Permissions
+--------+                                +---------------+
```
### トークンの発行
リソースサーバの認可機能に対して、Credentialを使用してアクセストークン発行をリクエストする

- トークンエンドポイントの確認
  - クライアントはトークンエンドポイントに対してアクセストークンを要求する
  ```bash
  user@host: ~/workspace $ curl http://localhost:8088/auth/realms/demo/.well-known/openid-configuration | jq .token_endpoint
  "http://localhost:8088/auth/realms/demo/protocol/openid-connect/token"
  ```
  - クレデンシャルの暗号化
  ```bash
  user@host: ~/workspace $ export CLIENT_ID="demo-app"
  user@host: ~/workspace $ export CLIENT_SECRET="8bb69f21-6965-41a1-b0e6-7907435d2ddc"

  user@host: ~/workspace $ echo -n "$CLIENT_ID:CLIENT_SECRET" | openssl base64
  ZGVtby1hcHA6OGJiNjlmMjEtNjk2NS00MWExLWIwZTYtNzkwNzQzNWQyZGRj
  ```
  - アクセストークン取得
  ```bash
  user@host: ~/workspace $ export CLIENT_CREDENTIAL="ZGVtby1hcHA6OGJiNjlmMjEtNjk2NS00MWExLWIwZTYtNzkwNzQzNWQyZGRj"
  user@host: ~/workspace $ curl -XPOST http://localhost:8088/auth/realms/demo/protocol/openid-connect/token \
  -H "Authorization: Basic ${CLIENT_CREDENTIAL}" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d 'grant_type=client_credentials' \
  -d 'scope=read' | jq .access_token
  ```
  - アクセストークンの内容確認
  ```bash
  # アクセストークンをデコードして中身を確認できる
  user@host: ~/workspace $ open https://jwt.io/#debugger
  ```

### トークンの使用
トークンを検証するのはリソースサーバ → 認可サーバー

- トークンを付与しないパターン(401)
  ```bash
  # 認可チェックしていないパスにリクエストを送る
  user@host: ~/workspace $ curl -XGET localhost:8080/home \
    -H "Content-Type: application/json" \
    -w %{http_code}
  home200

  # 認可チェックしているパスにリクエストを送る
  user@host: ~/workspace $ curl -XGET localhost:8080/user/100 \
    -H "Content-Type: application/json" \
    -w %{http_code}
  401
  ```
- トークンを付与するけど認可されていないリソースにアクセスするパターンの確認(403)
  ```bash
  user@host: ~/workspace $ export ACCESS_TOKEN="トークンを貼る"
  user@host: ~/workspace $ curl -XGET localhost:8080/users \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer ${ACCESS_TOKEN}" \
    -w %{http_code}
  403
  ```
- トークンを使用してのAPIリクエスト(200)
  ```bash
  user@host: ~/workspace $ export ACCESS_TOKEN="トークンを貼る"
  user@host: ~/workspace $ curl -XGET localhost:8080/user/100 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${ACCESS_TOKEN}" \
  -w %{http_code}
  100200
  ```
