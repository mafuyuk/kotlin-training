# Client Credentials Sample
## 環境構築
- 認可サーバー構築
  ```bash
  user@host: ~/workspace $ docker compose up -d
  ```
- リソースサーバー構築
  ```bash
  user@host: ~/workspace $ ./gradlew bootRun
  ```

## OAuthトランザクションの動作確認
```http request
+--------+                                +---------------+
|        |                                |               |
|        |----(1) Credential ------------>| Authorization |
|        |                                | 　  Server    |
|        |<---(2) Access Token -----------|               |
|        |                                +---------------+
| Client |                                
|        |                                +---------------+
|        |----(3) API Call with Token --->|               |
|        |                                |   Resource    |
|        |<---(4) API Response -----------|    Server     |
|        |                                |               |
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
  user@host: ~/workspace $ export CLIENT_CREDENTIAL="8bb69f21-6965-41a1-b0e6-7907435d2ddc"

  user@host: ~/workspace $ echo -n "$CLIENT_ID:$CLIENT_CREDENTIAL" | openssl base64
  ZGVtby1hcHA6OGJiNjlmMjEtNjk2NS00MWExLWIwZTYtNzkwNzQzNWQyZGRj
  ```
  - アクセストークン取得
  ```bash
  user@host: ~/workspace $ export ACCESS_TOKEN="ZGVtby1hcHA6OGJiNjlmMjEtNjk2NS00MWExLWIwZTYtNzkwNzQzNWQyZGRj"
  user@host: ~/workspace $ curl -XPOST http://localhost:8088/auth/realms/demo/protocol/openid-connect/token \
  -H "Authorization: Basic ${ACCESS_TOKEN}" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d 'grant_type=client_credentials' \
  -d 'scope=read' | jq .access_token
  ```

### トークンの使用
トークンを検証するのはリソースサーバ

  - トークンを付与しないパターンの確認
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
  {"timestamp":"2021-04-19T13:43:29.066+00:00","status":403,"error":"Forbidden","message":"Access Denied","path":"/user/100"}403
  ```
  - トークンを使用してのAPIリクエスト
  ```bash
  user@host: ~/workspace $ export ACCESS_TOKEN="上記で取得したトークンを貼る"
  user@host: ~/workspace $ curl -XGET localhost:8080/user/100 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${ACCESS_TOKEN}" \
  -w %{http_code}
  ```

## 参考リンク
- https://baubaubau.hatenablog.com/entry/2021/02/12/201803
- https://qiita.com/TakahikoKawasaki/items/200951e5b5929f840a1f
  - クライアント・クレデンシャルズフローでは、リフレッシュトークンを発行すべきではないとされている
- https://qiita.com/TakahikoKawasaki/items/63ed4a9d8d6e5109e401
- https://thinkit.co.jp/article/17621
  - Access Type を confidential
  - Standard Flow Enabled → 認可コード
  - Implicit Flow Enabled → インプリシットフロー
  - Direct Access Grants Enabled → リソースオーナーパスワード
  - Service Accounts Enabled → クライアントクレデンシャル
- https://access.redhat.com/documentation/ja-jp/red_hat_single_sign-on/7.4/html/server_administration_guide/clients
  - アクセス/クライアントタイプの説明がある
- https://storage.googleapis.com/authlete-website/slides/20200701_Client_Auth.pdf
  - アクセス/クライアントタイプの説明がある
- https://onigra.github.io/blog/2018/02/25/kc-with-webapi/
  - アクセスタイプがbearer-onlyにした場合は認証は別の場所で行いそのレスポンスにアクセストークンを付与する方式
  - 構成としては参考になるかも？
- https://qiita.com/yagiaoskywalker/items/66defea2ccb618c5633d#%E3%82%A2%E3%82%AF%E3%82%BB%E3%82%B9%E5%88%B6%E5%BE%A1%E3%81%AE%E6%8B%92%E5%90%A6%E3%81%AE%E3%82%BF%E3%82%A4%E3%83%9F%E3%83%B3%E3%82%B0%E3%81%8C%E9%81%85%E5%BB%B6%E3%81%99%E3%82%8B%E3%83%91%E3%82%BF%E3%83%BC%E3%83%B3
- https://github.com/keycloak/keycloak-documentation/blob/master/server_admin/topics/clients/oidc/service-accounts.adoc
  - クライアントクレデンシャルについて
- https://qiita.com/TakahikoKawasaki/items/e508a14ed960347cff11
  - インプリシット・フローのみであれば認可エンドポイントの実装だけで済む
  - リソースオーナー・パスワード・クレデンシャルズ・フローもしくはクライアント・クレデンシャルズ・フローだけであればトークンエンドポイントの実装だけで済む
- https://github.com/jgrandja/spring-security-oauth-5-2-migrate
  - 実装の参考になる
  - 他のパターンは https://github.com/spring-projects/spring-security/wiki/OAuth-2.0-Migration-Guide から探すとよい
- https://qiita.com/kazuki43zoo/items/e925f134e65d7595aa3c
  - Macherの参考になる
- https://github.com/spring-projects/spring-security/tree/5.4.5/samples/
  - spring securityのサンプル
- https://tools.ietf.org/html/rfc6749#section-2.1
  - クライアントタイプについて
- https://keycloak-documentation.openstandia.jp/4.0.0.Final/ja_JP/server_admin/index.html#_service_accounts
- https://k-ota.dev/keycloak-oidc-guide/
- https://qiita.com/opengl-8080/items/032ed0fa27a239bdc1cc
- https://kiririmode.hatenablog.jp/entry/20170205/1486287614
  - トークンの種類
  - Assertion トークンを使用する必要がありそうなのでJWTを使う必要がでてくる
  - ただしAssertion トークンは途中で無効化ができないためexpire対応するしかない
- https://jwt.io/#debugger