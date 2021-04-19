# Client Credentials Sample
# 環境構築
```bash
user@host: ~/workspace $ docker compose up -d
user@host: ~/workspace $ ./gradlew bootRun
```

# 動作確認
## リクエスト取得
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

# 認可チェックしているパスにアクセストークンを付与したリクエストを送る
user@host: ~/workspace $ export ACCESS_TOKEN="eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJGSW96MktSMm1QaDJiQjZLSjZGNlloYUFfMjQxT3Fpd3IwZFMydnFMMlVZIn0.eyJleHAiOjE2MTg4MDI5ODEsImlhdCI6MTYxODgwMjY4MSwianRpIjoiYWZjZjMzODQtNzBkYy00NjE2LWJmOGUtYmYwNDdlMzgwYjk3IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDg4L2F1dGgvcmVhbG1zL2RlbW8iLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiMDcwODBjMDgtODE0MC00M2ZhLWIyNjQtZTIzYzk4YTkyNGViIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiZGVtby1hcHAiLCJhY3IiOiIxIiwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6ImVtYWlsIHByb2ZpbGUiLCJjbGllbnRIb3N0IjoiMTcyLjI3LjAuMSIsImNsaWVudElkIjoiZGVtby1hcHAiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsInByZWZlcnJlZF91c2VybmFtZSI6InNlcnZpY2UtYWNjb3VudC1kZW1vLWFwcCIsImNsaWVudEFkZHJlc3MiOiIxNzIuMjcuMC4xIn0.XQ4i2NYhslIcqeKym1gwqSs8ILwWzlDIvzNSWrQI7rkTl6q4JFFY84TKeVLcznJXjTXGvjvGVJlifwYWJc__ymip2yD5paeQGRi09LhO0Zp82PS2LHeeKy5cqmcCWskUKLh3XSyhAoiRRFsKhVllWtPySbGC4JmbXn6qLHi1nxxR8fz9js_NwVblzP1GkddShteCtLTJnq1NFwdNvuT6gYGfNKDfAA2Hb4Dy2UWaS1-KL_MfpqTH8ExEOuSfuvE5dIPuCAmgW4v0rUOP2nwzbf7lj7h8j43fbnclhngoQVZiWCBOtfPCIIgGKtot8tnwkKjQ3Zou-Tgn5pruaE7QRg"
user@host: ~/workspace $ curl -XGET localhost:8080/user/100 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${ACCESS_TOKEN}" \
  -w %{http_code}
```

## トークンエンドポイントの確認
```bash
$ curl http://localhost:8088/auth/realms/demo/.well-known/openid-configuration | jq .token_endpoint
"http://localhost:8088/auth/realms/demo/protocol/openid-connect/token"
```

## クライアントクレデンシャルでアクセストークン取得
```bash
# 固定値になるので毎回叩く必要なし
#$ export CLIENT_ID="demo-app"
#$ export CLIENT_CREDENTIAL="8bb69f21-6965-41a1-b0e6-7907435d2ddc"
#
#$ echo "Basic $(echo -n "$CLIENT_ID:$CLIENT_CREDENTIAL" | openssl base64)"
#Basic ZGVtby1hcHA6OGJiNjlmMjEtNjk2NS00MWExLWIwZTYtNzkwNzQzNWQyZGRj

$ curl -XPOST http://localhost:8088/auth/realms/demo/protocol/openid-connect/token \
-H "Authorization: Basic ZGVtby1hcHA6OGJiNjlmMjEtNjk2NS00MWExLWIwZTYtNzkwNzQzNWQyZGRj" \
-H "Content-Type: application/x-www-form-urlencoded" \
-d 'grant_type=client_credentials' | jq .

{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJSRm1jNkY3aGtZaDFKVFd4b25fM09TR1hoZWt5eklDUlMxU3dIS1FJOTZnIn0.eyJleHAiOjE2MTg0Mjc4MzEsImlhdCI6MTYxODQyNzUzMSwianRpIjoiZmM3NzUxYzgtY2E4Ni00YjQ4LThiYzMtMTQ2OGViY2NmYWU1IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDg4L2F1dGgvcmVhbG1zL2RlbW8iLCJzdWIiOiIzZWJiNzFlYS1jNmU4LTRlYzgtYjhhOC0wZDI2ZWQ0MTNmODAiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJkZW1vLWFwcCIsImFjciI6IjEiLCJzY29wZSI6InJlYWQiLCJjbGllbnRJZCI6ImRlbW8tYXBwIiwiY2xpZW50SG9zdCI6IjE3Mi4yMC4wLjEiLCJjbGllbnRBZGRyZXNzIjoiMTcyLjIwLjAuMSJ9.YUt72iFJmblTyU3hLiwB8a-UNHaU0UWtwkHu3dWY2i4Xg51PGTRFm5IWp2UAg8jvkSRjKF6A6BbTScd38hDoDqQjKvJPEDXFl5NE-EHsRp6nGwvOSW7GewFSbm8OHr45GSqDsybIy6bTLDKgI08wYC0P6N6Vp-uZrNa2ug-IWZN8VABRiU2o3EHdStJuGdvDQTKSnoyZcHTYDouerzW7ENhERtmma8ISaZHeAif8YBC-li8CM0SJ_zKTfQTILt_a_G_jBfJMe2UsVVmqEUULvrLhaG1V-VUTY5W6xrWV3STN9LHZpal10-2mIjvfrtHsLkNknwOMvQx5Y8EwViRDMw",
  "expires_in": 300,
  "refresh_expires_in": 0,
  "token_type": "Bearer",
  "not-before-policy": 0,
  "scope": "read"
}
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