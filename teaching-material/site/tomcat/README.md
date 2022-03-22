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

## 参照リンク
* https://spring.pleiades.io/spring-boot/docs/current/reference/html/application-properties.html#application-properties.server
  サーバープロパティはSpring Bootの組み込みサーバーのみに適用される
* https://community.jaspersoft.com/wiki/hide-stack-trace-thrown-tomcat-while-deploying-jasperreports-server-due-error
* https://tomcat.apache.org/tomcat-9.0-doc/config/valve.html#Error_Report_Valve
* https://utrhira.gitbooks.io/note/content/javaaps/tomcat/dockerized_use.html