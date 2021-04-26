# Resource(Authorization含む)Server Sample

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
- クレデンシャルの暗号化
  ```bash
  user@host: ~/workspace $ export CLIENT_ID="demo-app"
  user@host: ~/workspace $ export CLIENT_SECRET="8bb69f21-6965-41a1-b0e6-7907435d2ddd"

  user@host: ~/workspace $ echo -n "$CLIENT_ID:$CLIENT_SECRET" | openssl base64
  ZGVtby1hcHA6OGJiNjlmMjEtNjk2NS00MWExLWIwZTYtNzkwNzQzNWQyZGRk
  ```
- アクセストークン取得
  ```bash
  user@host: ~/workspace $ export CLIENT_CREDENTIAL="ZGVtby1hcHA6OGJiNjlmMjEtNjk2NS00MWExLWIwZTYtNzkwNzQzNWQyZGRk"
  user@host: ~/workspace $ curl -XPOST http://localhost:8080/oauth/token \
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
  home2200

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
  
## トークンの内容
## 詳細
今回はHS256利用する
base64でエンコードしピリオド(.)で繋いだヘッダーとペイロードを署名アルゴリズム(HS256)でハッシュ化しJWTに格納、JWT利用時に検証することで改ざん対策を行う

### 動作確認
[アクセストークン例](https://jwt.io/#debugger-io?token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6WyJyZWFkIl0sImV4cCI6MTYxOTQwMDYxNSwiYXV0aG9yaXRpZXMiOlsiUk9MRV9VU0VSIl0sImp0aSI6InZGaVFzT0VSdkowbTRVcS05aEl4ZXh3X1hUayIsImNsaWVudF9pZCI6ImRlbW8tYXBwIn0.xsvCWnISqJ9lWJLUll4-blRg3BfxCpeQkXCdm9swYcY)

- 改ざんれていないかの確認
  - 署名アルゴリズムとSecretの確認
  ※ 実際にはこちらは見れないようにしないといけない
  ```bash
  user@host: ~/workspace $ curl  http://localhost:8080/oauth/token_key \
     -H "Authorization: Basic ${CLIENT_CREDENTIAL}"
  {"alg":"HMACSHA256","value":"demo-app"}
  ```
  - ハッシュ化した値が`xsvCWnISqJ9lWJLUll4-blRg3BfxCpeQkXCdm9swYcY`と等しいかチェックする
  ```bash
  user@host: ~/workspace $ echo -n "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6WyJyZWFkIl0sImV4cCI6MTYxOTQwMDYxNSwiYXV0aG9yaXRpZXMiOlsiUk9MRV9VU0VSIl0sImp0aSI6InZGaVFzT0VSdkowbTRVcS05aEl4ZXh3X1hUayIsImNsaWVudF9pZCI6ImRlbW8tYXBwIn0" \
    | openssl dgst -sha256 -hmac "demo-app"
  c6cbc25a7212a89f655892d4965e3e6e5460dc17f10a979091709d9bdb3061c6
  ```

## 参考リンク
- [JWS + JWK in a Spring Security OAuth2 Application](https://www.baeldung.com/spring-security-oauth2-jws-jwk)
- [Spring Security OAuth 2.0 Roadmap Update](https://spring.io/blog/2019/11/14/spring-security-oauth-2-0-roadmap-update)
  - Authorization Serverのサポートがなくなった
- [攻撃して学ぶJWT【ハンズオンあり】](https://moneyforward.com/engineers_blog/2020/09/15/jwt/)
  - セキュリティに関して
- [spring jwt decoder openid token](https://stackoverflow.com/questions/54512182/spring-jwt-decoder-openid-token)
  - リソースサーバーでHS256設定を利用する参考