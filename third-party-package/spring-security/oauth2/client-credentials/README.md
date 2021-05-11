# Client Credentials
- [RFC6749 Client Credentials](https://tools.ietf.org/html/rfc6749#section-4.4)

## 参考リンク
### Keycloak
- [keycloakでOIDC認証するときに知っておくと良いこと](https://k-ota.dev/keycloak-oidc-guide/)
- [Keycloakのインストールと構築例](https://thinkit.co.jp/article/17621)
    - Access Type を confidential
    - Standard Flow Enabled → 認可コード
    - Implicit Flow Enabled → インプリシットフロー
    - Direct Access Grants Enabled → リソースオーナーパスワード
    - Service Accounts Enabled → クライアントクレデンシャル
- [Keycloak仕様 サービス・アカウント](https://keycloak-documentation.openstandia.jp/4.0.0.Final/ja_JP/server_admin/index.html#_service_accounts)
- [Keycloakを使ってWeb APIに対してアクセストークンを使ったリクエストを行う](https://onigra.github.io/blog/2018/02/25/kc-with-webapi/)
    - アクセスタイプがbearer-onlyにした場合は認証は別の場所で行いそのレスポンスにアクセストークンを付与する方式
    - 構成としては参考になるかも？
- [Keycloakのクライアント・アダプターで認可サービスを試してみる（Tomcat編）](https://qiita.com/yagiaoskywalker/items/66defea2ccb618c5633d)
- [keycloak-documentation クライアントクレデンシャルについて](https://github.com/keycloak/keycloak-documentation/blob/master/server_admin/topics/clients/oidc/service-accounts.adoc)

### 実装の参考
- [Spring Security と Keycloak を使ってリソースサーバーを作ってみた](https://baubaubau.hatenablog.com/entry/2021/02/12/201803)
- [Spring Security 使い方メモ　認証・認可](https://qiita.com/opengl-8080/items/032ed0fa27a239bdc1cc)
- [Spring Securityのサンプル](https://github.com/spring-projects/spring-security/tree/5.4.5/samples/)
- [Spring Security OAuth 5.2 Migration Sample](https://github.com/jgrandja/spring-security-oauth-5-2-migrate)
    - 他のパターンは https://github.com/spring-projects/spring-security/wiki/OAuth-2.0-Migration-Guide から探すとよい
- [Spring Security 5.3.3で Resource Server を構成する](https://dev.classmethod.jp/articles/resource-server-configuration-with-spring-security5/)
    - token introspection
- [JWS + JWK in a Spring Security OAuth2 Application](https://www.baeldung.com/spring-security-oauth2-jws-jwk)
    - トークンの鍵とってきて検証する部分の参考になりそう
- [Using JWT with Spring Security OAuth](https://www.baeldung.com/spring-security-oauth-jwt)
    - JwtDecoder周り
- [Keycloak Embedded in a Spring Boot Application](https://www.baeldung.com/keycloak-embedded-in-spring-boot-app)

### フローについて
- https://www.authlete.com/ja/resources/videos/

#### OAuth2.0
クライアント・クレデンシャルズフローでは、リフレッシュトークンを発行すべきではないとされている

- [OAuth 2.0 の勉強のために認可サーバーを自作する](https://qiita.com/TakahikoKawasaki/items/e508a14ed960347cff11)
    - インプリシット・フローのみであれば認可エンドポイントの実装だけで済む
    - リソースオーナー・パスワード・クレデンシャルズ・フローもしくはクライアント・クレデンシャルズ・フローだけであればトークンエンドポイントの実装だけで済む
- [第8章 クライアントの管理](https://access.redhat.com/documentation/ja-jp/red_hat_single_sign-on/7.4/html/server_administration_guide/clients)
    - アクセス/クライアントタイプの説明がある
- [クライアント認証](https://storage.googleapis.com/authlete-website/slides/20200701_Client_Auth.pdf)
    - アクセス/クライアントタイプの説明がある
- [OAuth 2.0 全フローの図解と動画](https://qiita.com/TakahikoKawasaki/items/200951e5b5929f840a1f)
- [OAuth 2.0 クライアント認証](https://qiita.com/TakahikoKawasaki/items/63ed4a9d8d6e5109e401)

#### JWT
- [OAuth アクセストークンの実装に関する考察](https://qiita.com/TakahikoKawasaki/items/970548727761f9e02bcd)
- [アクセストークンの例](https://jwt.io/#debugger-io?token=eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJvNU50ZFNTLXZrRk5ZdEpjbmNPMjg3NGRTclNscUZxOTY5dEhtX2laX3p3In0.eyJleHAiOjE2MTkyNzI4NTgsImlhdCI6MTYxOTI3MjU1OCwianRpIjoiZTkwMzkwN2YtYmRiZC00ODY5LWI2NjItN2NlYWQxNzI0ZDM2IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDg4L2F1dGgvcmVhbG1zL2RlbW8iLCJzdWIiOiJkMzk1ZTJkMy0xZDRjLTRlNTgtOGExOC01YTE5ZmQwZTQ2MjciLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJkZW1vLWFwcCIsImFjciI6IjEiLCJzY29wZSI6InJlYWQgd3JpdGUiLCJjbGllbnRJZCI6ImRlbW8tYXBwIiwiY2xpZW50SG9zdCI6IjE5Mi4xNjguMTI4LjEiLCJjbGllbnRBZGRyZXNzIjoiMTkyLjE2OC4xMjguMSJ9.U8_1Apww3YVbQUtuznWMsjGJl01c-uBD1jngcuU--FaJ2SOiW0dSvY4L_2NC4MOKgLdKify9HfisNlBoWdCbkglfA9S2xNtASrMkyZoBJhIDrbXqTkssFqou4yB7jX_qLPVfCXyEHdvGSm_UwgCydtsG-C8oyP6KKNum4ICrOJjuHu_vPiXPSUZqOuWbRb-WBqp-lbrW3bpcg2U5ZF39dBNv2ASYxLJqMIYGMrl5cNIyFRRr9ZCX6lvCHXqomgDhguK9dZjOjsjVN9Yr19QolHmfjcXnXQGVEwrh28RZ7Phu_IzIabknYUTHudPaqrafEKmhvVtHNKQLS9JOOZ71wQ&publicKey=-----BEGIN%20PUBLIC%20KEY-----%0AMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnkkfCs90osWBFMwl%2FDUy%0AVQYJxlc%2BYHWRU2X5ITLU0clgTJKsBjSvnUMSucOqVpML9SxnHIyJebidRgKtqLpo%0AY7RdQJg4oabnOHYwmfBeQiuTTJTahsFWlepWpPPag%2BkF12Rnw4N00%2BGCKa67nII8%0Ag19Bt3qLezt2S%2FLTP4rElH7NiOlH7kSL4tRqgyt8dfiq4Ky3fzSlIJLTqSGCoaRP%0AG141aTGZJn4fU%2B99H%2FESfIOtdW3Uwcu%2Bh2jSNL9OUc1CxEcJxBNqvCimRCjRQ5jG%0AGJFHwqmXo6IbJWEqB8dTfIiPO73UAVF1cyIMupFIb4guBkPVS45DfEhVLuJY3yZC%0AHwIDAQAB%0A-----END%20PUBLIC%20KEY-----%0A)
- [クレームについて](https://openid.net/specs/openid-connect-core-1_0.html)

### RFC
- [IETF JOSE WG と OAuth WG から一気に9本の RFC が！](https://oauth.jp/blog/2015/05/20/jose-and-oauth-assertion-rfcs/)

- [The OAuth 2.0 Authorization Framework](https://datatracker.ietf.org/doc/rfc6749/)
    - [日本語訳](https://openid-foundation-japan.github.io/rfc6749.ja.html)
    - [Client Types](https://tools.ietf.org/html/rfc6749#section-2.1)
- [An IETF URN Sub-Namespace for OAuth](https://datatracker.ietf.org/doc/rfc6755/)
- [JSON Web Signature (JWS)](https://datatracker.ietf.org/doc/rfc7515/)
- [JSON Web Encryption (JWE)](https://datatracker.ietf.org/doc/rfc7516/)
- [JSON Web Key (JWK)](https://datatracker.ietf.org/doc/rfc7517/)
    - [日本語訳](https://openid-foundation-japan.github.io/rfc7517.ja.html)
- [JSON Web Algorithms (JWA)](https://datatracker.ietf.org/doc/rfc7518/)
- [JSON Web Token (JWT)](https://datatracker.ietf.org/doc/rfc7519/)
    - JWT は JSON (の Base64 URL Encode) 形式で Assertion を生成するための仕様
- [Examples of Protecting Content Using JSON Object Signing and Encryption (JOSE)](https://datatracker.ietf.org/doc/rfc7520/)
- [Assertion Framework for OAuth 2.0 Client Authentication and Authorization Grants](https://datatracker.ietf.org/doc/rfc7521/)
- [JSON Web Token (JWT) Profile for OAuth 2.0 Client Authentication and Authorization Grants](https://datatracker.ietf.org/doc/rfc7523/)