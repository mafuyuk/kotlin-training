# Tomcat
Tomcat + Spring Bootを試す

## コマンド
### local
```bash
$ ./gradlew bootRun
$ curl http://localhost:8080/hello \
  -H "Content-Type: application/json"
  hello
$ open http://localhost:8080/error
```
### docker
```bash
$ ./gradlew bootWar
$ docker compose up -d
$ curl http://localhost:8080/hello \
  -H "Content-Type: application/json"
  hello-docker
$ open http://localhost:8080/error
```