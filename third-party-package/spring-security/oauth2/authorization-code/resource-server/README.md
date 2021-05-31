# Resource Server Sample
## 環境構築
- 認可サーバー構築
  認可サーバーはOSSの [Okta](https://www.okta.com/jp/) を使用
  OktaはBaaSで、無料トライアルを利用
- リソースサーバー構築
  リソースサーバーはKotlin + Spring Boot + Spring Securityで構築
  ```bash
  user@host: ~/workspace $ ./gradlew bootRun
  ```

## OAuthトランザクションの動作確認
```
+----------+
| Resource |
|   Owner  |
|          |
+----------+
     ^
     |
    (B)
+----|-----+          Client Identifier      +---------------+
|         -+----(A)-- & Redirection URI ---->|               |
|  User-   |                                 | Authorization |
|  Agent  -+----(B)-- User authenticates --->|     Server    |
|          |                                 |               |
|         -+----(C)-- Authorization Code ---<|               |
+-|----|---+                                 +---------------+
  |    |                                         ^      v
 (A)  (C)                                        |      |
  |    |                                         |      |
  ^    v                                         |      |
+---------+                                      |      |
|         |>---(D)-- Authorization Code ---------'      |
|  Client |          & Redirection URI                  |
|         |                                             |
|         |<---(E)----- Access Token -------------------'
+---------+       (w/ Optional Refresh Token)
```
### 認可コードの取得
- 新規シークレットウィンドウを開く
- [ページ](https://creationline-kamono-test.okta.com/oauth2/v1/authorize?client_id=0oaty07zv5hjHAOJG5d6&nonce=aaas&redirect_uri=http://localhosts:8080&response_type=code&scope=openid+email&state=aaaw) を開きログインを行う
- リダイレクトURIのQueryParamの`code`が返ってくるのでそれを控える
  ```bash
  user@host: ~/workspace $ export CODE="コード"
  ```

### トークンの発行
認可サーバに対して、Credentialを使用してアクセストークン発行をリクエストする

- トークンエンドポイントの確認
  - クライアントはトークンエンドポイントに対してアクセストークンを要求する
  ```bash
  user@host: ~/workspace $ curl https://creationline-kamono-test.okta.com/.well-known/openid-configuration | jq .token_endpoint
  "https://creationline-kamono-test.okta.com/oauth2/v1/token"
  ```
  - クレデンシャルの暗号化
  ```bash
  user@host: ~/workspace $ export CLIENT_ID="クライアントID"
  user@host: ~/workspace $ export CLIENT_SECRET="クライアントシークレット"

  user@host: ~/workspace $ echo -n "$CLIENT_ID:$CLIENT_SECRET" | openssl base64
  user@host: ~/workspace $ export CLIENT_CREDENTIAL="クライアントクレデンシャル"
  ```
  - アクセストークン取得
  ```bash
  user@host: ~/workspace $ curl -XPOST https://creationline-kamono-test.okta.com/oauth2/v1/token \
  -H "Authorization: Basic ${CLIENT_CREDENTIAL}" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=authorization_code" \
  -d "code=${CODE}" \
  -d "redirect_uri=http://localhosts:8080" \
  -d "scope=email" | jq .access_token
  ```
  - アクセストークンの内容確認
  ```bash
  # アクセストークンをデコードして中身を確認できる
  user@host: ~/workspace $ open https://jwt.io/#debugger
  user@host: ~/workspace $ open https://jwt.io/#debugger-io?token=eyJraWQiOiJ0TTJHcC0wNkZoUkUzaURkV1VtaXlnYkwxMk9BY0hFTmE5Zl93bHNMcG04IiwiYWxnIjoiUlMyNTYifQ.eyJ2ZXIiOjEsImp0aSI6IkFULk1ZcWJkd19kWHBqSDFONno5YjgxWVhSM3ZIZ2JWbjlvMFMzcHRvVWRHU28iLCJpc3MiOiJodHRwczovL2NyZWF0aW9ubGluZS1rYW1vbm8tdGVzdC5va3RhLmNvbSIsImF1ZCI6Imh0dHBzOi8vY3JlYXRpb25saW5lLWthbW9uby10ZXN0Lm9rdGEuY29tIiwic3ViIjoiVGFuYWthVGFyb0BleGFtcGxlLmNvbSIsImlhdCI6MTYyMjQ2MzI0NiwiZXhwIjoxNjIyNDY2ODQ2LCJjaWQiOiIwb2F0eTA3enY1aGpIQU9KRzVkNiIsInVpZCI6IjAwdXR4eWtjMXFXUFNhVjhQNWQ2Iiwic2NwIjpbIm9wZW5pZCIsImVtYWlsIl19.Y3mEIDD5F_3XLMEMT56cty8encOQdyTvMUeCW8imoExnZtZim6BEJW0NZrse0TICNDW5ah7OwDdMot58ZSssknqyQSJtkbjf5x1oBOSlHyPKbl_0CdHmwx3I5p14mP1qifN7u1StScOQzpRxsXF8JmVW9jvjrO5uITCej3oYEqJ64V8JddCnifWAkZUgKBpF4GpThyyeQ0hCJYqzi6h2G15pIz-N1Jq8JsvMg5siQYWNnaUF4iETjoS1EvQVTS0HUyvaPFoyL_DRjQRmQx3TT5zKdNGiownXn-5gh8fSxDT48WSwBr4TG_mfHyMPV3YFRZVwEtRbw57Dn6IvwGUH8Q
  ```

### トークンの使用
```bash
user@host: ~/workspace $ export ACCESS_TOKEN="アクセストークン"
user@host: ~/workspace $ curl -XGET localhost:8080/home \
-H "Content-Type: application/json" \
-H "Authorization: Bearer ${ACCESS_TOKEN}" \
-v
```

### トークンのリフレッシュ

