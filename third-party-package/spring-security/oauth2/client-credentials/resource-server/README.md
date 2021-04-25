# Resource Server Sample
## 環境構築
- 認可サーバー構築
  認可サーバーはOSSの[Keycloak](https://www.keycloak.org/)を使用
  ```bash
  user@host: ~/workspace $ docker compose up -d
  user@host: ~/workspace $ open http://localhost:8088/
  ```
- リソースサーバー構築
  リソースサーバーはKotlin + Spring Boot + Spring Securityで構築
  ```bash
  user@host: ~/workspace $ ./gradlew bootRun
  ```

## OAuthトランザクションの動作確認
```
+--------+                                +---------------+
|        |----(1) Credential ------------>|               |
|        |                                | Authorization |
|        |<---(2) Access Token -----------| 　  Server    |
|        |                                |               |<-----|
|        |                                +---------------+      |
| Client |                                                       | (4) Request Public Keys
|        |                                +---------------+      |     Response JWK
|        |----(3) API Call with JWT ----->|               |------|
|        |                                |   Resource    |
|        |                                |    Server     |------| (5) Verify Signature
|        |<---(7) API Response -----------|               |<-----| (6) Verify Permissions
+--------+                                +---------------+
```
### トークンの発行
認可サーバに対して、Credentialを使用してアクセストークン発行をリクエストする

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

### トークンのリフレッシュ
クライアントクレデンシャルの場合、レスポンスにリフレッシュトークンを含めないほうが良さそう
expire切れたら再生成を行っていく

- [RFC6749 Client Credentials/Response](https://tools.ietf.org/html/rfc6749#section-4.4.3)

## 疑問点
- JWTの場合、Access Tokenは永続化していないのか？
  - TODO: dockerでmysql使うようにしてデータを確認する
  - AssertionなのでたぶんKeycloakではDBに保持していないはず
