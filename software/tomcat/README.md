# Tomcat
Tomcat + Spring Bootを試す

Tomcat上だとapplication.ymlのsever設定画無視されてしまう問題が発生している
## コマンド
### local
```bash
$ ./gradlew bootRun
$ curl http://localhost:8080/hello \
  -H "Content-Type: application/json"
  hello
$ open http://localhost:8080/error
# スタックトレースが表示されない
```
### docker
```bash
$ ./gradlew bootWar
$ docker compose up -d
$ curl http://localhost:8080/hello \
  -H "Content-Type: application/json"
  hello-docker
$ open http://localhost:8080/error
# スタックトレースが表示されてしまう
```