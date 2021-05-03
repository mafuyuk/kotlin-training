# Zuul
https://spring.pleiades.io/projects/spring-cloud-gateway

## 環境構築
```bash
$ docker compose up -d
$ curl http://localhost:80/status/200 -i
HTTP/1.1 200 OK
Server: gunicorn/19.9.0
Date: Mon, 03 May 2021 02:55:42 GMT
Connection: keep-alive
Content-Type: text/html; charset=utf-8
Access-Control-Allow-Origin: *
Access-Control-Allow-Credentials: true
Content-Length: 0

$ ./gradlew bootRun
$ curl http://localhost:8080/status/200 -i
```

## 参考リンク
- [Netflix Zuul ルーティング](https://spring.pleiades.io/guides/gs/routing-and-filtering/)
- [Api Gateway using ZUUL #1 || Netflix ZUUL || Netflix Zuul with Spring Boot || Green Learner](https://www.youtube.com/watch?v=-I-9gK8NWXY)