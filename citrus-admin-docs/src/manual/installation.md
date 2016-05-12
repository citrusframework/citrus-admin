### Installation

First fo all start the server web application and keep it running:

```mvn -pl citrus-admin-web spring-boot:run```

For active development and a short roundtrip you can use gulp:watch in order to automatically compile typescript sources on the fly when they change.

```mvn -pl citrus-admin-client frontend:gulp -Pgulp-watch```

If you change a source file (e.e *.js, *.ts, *.css) the sources will automatically be compiled and copied to the Maven target folder. The running
spring-boot application is able to automatically grab the newly compiled sources. Just go to the browser and hit refresh to see the changes.
If you change server Java sources spring-boot automatically restarts the web application so you may just hit refresh in your browser, too.