# Client Credentials Sample

## アプリケーション起動
```ShellSession
user@host: ~/workspace $ ./gradlew bootRun
```

```ShellSession
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
user@host: ~/workspace $ curl -XGET localhost:8080/user/100 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJSRm1jNkY3aGtZaDFKVFd4b25fM09TR1hoZWt5eklDUlMxU3dIS1FJOTZnIn0.eyJleHAiOjE2MTg0NTkxMjMsImlhdCI6MTYxODQ1ODgyMywianRpIjoiZWE5Mzc0N2YtYjkyMS00NTQ2LWI5ZjYtYTE5NDY0ZTJiNzg3IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDg4L2F1dGgvcmVhbG1zL2RlbW8iLCJzdWIiOiIzZWJiNzFlYS1jNmU4LTRlYzgtYjhhOC0wZDI2ZWQ0MTNmODAiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJkZW1vLWFwcCIsImFjciI6IjEiLCJzY29wZSI6InJlYWQiLCJjbGllbnRJZCI6ImRlbW8tYXBwIiwiY2xpZW50SG9zdCI6IjE3Mi4yMC4wLjEiLCJjbGllbnRBZGRyZXNzIjoiMTcyLjIwLjAuMSJ9.dkY849d4GL3daX02efrTYuo364Go4ncSB5OkWbq4u8HYfM-3bUqCq5MVAyAPDTF8eTlAX9ZPTHjBtAtTJUfcS8zH-ykAx-Rc0RHIonTM1WjxYhl2YMdJoHT1klVH7Wa6hIKsBfAYq01ydFh7fVJ4XmdjILEbDbw8fQHbGfIwetCtVxXK-RPk429tsK6kvZjY0H5a6jHmvW8gIvj_OisELJ2hGTcVzWBDBPb_QGXyFP5YtTfk-wWR-t-IoDSlJ40tkuisqimPqGKh7yfhV653ZNm8_hShUP_47M0x7NiIylinzCp6zO8z595N7AQpGxOS8-M_tfnmxFxztjOB6EgMQw" | jq
```


## トークンエンドポイントの確認
```ShellSession
$ curl http://localhost:8088/auth/realms/demo/.well-known/openid-configuration | jq .token_endpoint
"http://localhost:8088/auth/realms/demo/protocol/openid-connect/token"
```

## クライアントクレデンシャルでアクセストークン取得
Keycloak側でクライアントクレデンシャルonにするためには以下の設定が必要
`Service Accounts: On`
`Access Type: confidential`

```bash
$ export CLIENT_ID="demo-app"
$ export CLIENT_CREDENTIAL="8bb69f21-6965-41a1-b0e6-7907435d2ddc"

$ echo "Basic $(echo -n "$CLIENT_ID:$CLIENT_CREDENTIAL" | openssl base64)"
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

# クライアントタイプについて
https://tools.ietf.org/html/rfc6749#section-2.1


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