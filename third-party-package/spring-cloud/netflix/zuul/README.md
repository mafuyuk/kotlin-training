# Zuul1
Zuul1はServletでZuul2はReactive

Zuul1を利用しているため
Spring Bootのversionは2.2.x, 2.3.x
Spring CloudのversionはHoxtonに固定される

https://spring.io/projects/spring-cloud

## 環境構築
```bash
$ docker compose up -d
$ ./gradlew bootRun
```

# 実行
## httpbinを叩く
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

$ curl http://localhost:80/status/201 -i
HTTP/1.1 201 CREATED
Server: gunicorn/19.9.0
Date: Thu, 06 May 2021 07:01:00 GMT
Connection: keep-alive
Content-Type: text/html; charset=utf-8
Access-Control-Allow-Origin: *
Access-Control-Allow-Credentials: true
Content-Length: 0
```

## httpbinをプロキシ経由で叩く
```bash
$ curl http://localhost:8080/status/200 -i
HTTP/1.1 200 
Date: Thu, 06 May 2021 07:03:26 GMT
Access-Control-Allow-Origin: *
Access-Control-Allow-Credentials: true
Content-Type: text/html;charset=utf-8
Transfer-Encoding: chunked


$ curl http://localhost:8080/status/201 -i
HTTP/1.1 404 
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers
Content-Type: application/json
Transfer-Encoding: chunked
Date: Thu, 06 May 2021 07:03:47 GMT

{"timestamp":"2021-05-06T07:03:47.000+00:00","status":404,"error":"Not Found","message":"","path":"/status/201"}% 
```

## 参考リンク
- [Netflix Zuul ルーティング](https://spring.pleiades.io/guides/gs/routing-and-filtering/)
- [Api Gateway using ZUUL #1 || Netflix ZUUL || Netflix Zuul with Spring Boot || Green Learner](https://www.youtube.com/watch?v=-I-9gK8NWXY)
- [Spring REST with a Zuul Proxy](https://www.baeldung.com/spring-rest-with-zuul-proxy)