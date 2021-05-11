TODO: 非ブロッキングなゲートウェイだったので一旦調査を途中でとりやめ(WebFlux使わないといけない)
# Spring Cloud Gateway
https://spring.pleiades.io/projects/spring-cloud-gateway

## 環境構築
```bash
$ docker compose up -d
$ ./gradlew bootRun
```

## 実行
### httpbinを叩く
```bash
$ curl http://localhost:80/status/200 -i
HTTP/1.1 200 OK
Server: gunicorn/19.9.0
Date: Mon, 03 May 2021 02:55:42 GMT
Connection: keep-alive
Content-Type: text/html; charset=utf-8
Access-Control-Allow-Origin: *
Access-Control-Allow-Credentials: true
Content-Length: 0
```

### プロキシ経由でhttpbinを叩く
```bash
$ curl http://localhost:8080/status/200 -i
```

# 参考リンク
- [](https://www.baeldung.com/spring-cloud-gateway)