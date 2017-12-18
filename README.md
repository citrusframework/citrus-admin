Citrus Admin Web UI ![Logo][1]
=========

This is a web administration user interface for the integration test framework 
Citrus [www.citrusframework.org][2]. Major functionality objectives
are project and configuration management as well as test execution and reporting.

![Dashboard](citrus-admin-docs/src/main/asciidoc/images/screenshots/project-dashboard.png)

See the complete [User Manual](https://christophd.github.io/citrus-admin/) for a detailed description 
on all features ond how to use them.

Docker image
---------

The administration UI is available as Docker image (`consol/citrus-admin:latest`). You can pull the image and link it to your local Citrus project:

```
docker run -d -p 8080:8080 -v $PWD:/maven -e CITRUS_ADMIN_PROJECT_HOME=/maven consol/citrus-admin:latest
```

The command above loads the Docker image and runs a new Citrus web UI container. The container is provided with a volume mount that makes the current directory accessible from within the container.
This current directory is then used as project home so the admin UI will automatically open the Citrus project from that directory. Once the container is running
you can point your local browser to [http://localhost:8080]() in order to access the web UI.

The `CITRUS_ADMIN_PROJECT_HOME` environment setting is optional and is used to automatically open a project on container startup. You can leave out this setting in order to select a project folder
in your mounted working directory when starting the web UI.

In case you do not have a Citrus project ready yet, the admin UI can also create a new project for you. It is possible to run a Maven archetype on container startup that creates a complete new project for you.
You can set the Maven archetype coordinates (*groupId*, *artifactId*, *version*) as environment variables when running the container.

```
docker run -d -p 8080:8080 -v $PWD:/maven -e CITRUS_ADMIN_MAVEN_ARCHETYPE_COORDINATES=com.consol.citrus.mvn:citrus-quickstart:2.7.2 consol/citrus-admin:latest
```

The UI will load the Maven archetype and create the project sources when the container is started. The new project gets its Maven coordinates from another environment setting:

```
-e CITRUS_ADMIN_MAVEN_PROJECT_COORDINATES=com.consol.citrus:citrus-sample:1.0.0
```

Another way to load a new project on container startup is to specify a git repository URL. The Citrus admin Docker container will then load the project sources from that git repository on startup:

```
docker run -d -p 8080:8080 -v $PWD:/maven -e CITRUS_ADMIN_PROJECT_REPOSITORY=https://github.com/account/citrus-project.git consol/citrus-admin:latest
```

The command above will load the project sources from git with URL `https://github.com/account/citrus-project.git` and open that project afterwards. The git repository of course should hold the Citrus project sources. In case
the Citrus project is located in a sub module in that git repository you can load that sub module by specifying additional environment properties:

```
-e CITRUS_ADMIN_PROJECT_REPOSITORY_MODULE=/integration/citrus-test -e CITRUS_ADMIN_PROJECT_REPOSITORY_BRANCH=bugfix
```

With these options we are able to start the Docker image as container with special customizations via environment settings. 

Use Docker Maven plugin
---------

The citrus-admin Docker image works great with the Fabric8 Docker Maven plugin (https://dmp.fabric8.io/). You can add the plugin configuration as follows in your
Maven POM:

```xml
<plugin>
    <groupId>io.fabric8</groupId>
    <artifactId>docker-maven-plugin</artifactId>
    <configuration>
      <verbose>true</verbose>
      <images>
        <image>
          <alias>citrus-admin</alias>
          <name>consol/citrus-admin:latest</name>
          <run>
            <namingStrategy>alias</namingStrategy>
            <ports>
              <port>8080:8080</port>
            </ports>
            <volumes>
              <from>
                <image>application</image>
              </from>
            </volumes>
            <env>
              <CITRUS_ADMIN_PROJECT_HOME>/maven</CITRUS_ADMIN_PROJECT_HOME>
            </env>
            <wait>
              <http>
                <url>http://localhost:8080/setup</url>
                <method>GET</method>
                <status>200</status>
              </http>
              <time>60000</time>
              <shutdown>500</shutdown>
            </wait>
            <log>
              <enabled>true</enabled>
              <color>green</color>
            </log>
          </run>
        </image>
        <image>
          <alias>application</alias>
          <name>application:${project.version}</name>
          <build>
            <assembly>
              <descriptorRef>project</descriptorRef>
            </assembly>
          </build>
        </image>
      </images>
    </configuration>
</plugin>
```

Now you can build the images locally with Maven calling

```
mvn docker:build
```

After that you should have a set of new images on your Docker host. You can run these images as Docker container.

```
mvn docker:start
```
   
Download
---------

The Citrus administration UI is a web application that uses Spring boot and Angular2. First of all download the latest distribution which
is a Java WAR file located at [labs.consol.de/maven/repository](https://labs.consol.de/maven/repository/com/consol/citrus/citrus-admin-web):

```
curl -o citrus-admin.war https://labs.consol.de/maven/repository/com/consol/citrus/citrus-admin-web/1.0.3-SNAPSHOT/citrus-admin-web-1.0.3-SNAPSHOT-executable.war
```

Save the Java web archive to a folder on your local machine and start the Spring boot web application. The downloaded artifact should be executable
from command line like this:

```
java -jar citrus-admin.war
```

You will see the application starting up. Usually you will see some console log output. The web server should start within seconds. Once the application is up and running
you can open your browser and point to [http://localhost:8080](http://localhost:8080).
 
That's it you are ready to use the Citrus administration UI. Next thing to do is to create or open a project.

Development
---------

After forking/cloning the source code repository from [https://github.com/christophd/citrus-admin](https://github.com/christophd/citrus-admin) you can build the application locally with Maven:

```
mvn clean install
```

You can start the web server as Spring Boot application.

```
mvn -pl citrus-admin-web spring-boot:run
```

For active development and a short round trip you can use the angular-cli dev-server in order to automatically compile typescript sources on the fly when they change.

```
mvn -pl citrus-admin-client package -Pdevelopment
```

If you change a source file (e.e *.js, *.ts, *.css) the sources will automatically be compiled and copied to the Maven target folder. The running
spring-boot application is able to automatically grab the newly compiled sources. Just go to the browser and hit refresh to see the changes.
If you change server Java sources spring-boot automatically restarts the web application so you may just hit refresh in your browser, too.

The development server is running on its own port 4200 ([http://localhost:4200](http://localhost:4200)). To avoid cors issues an api proxy to the backend is provided out of the box. 
You can configure the proxy settings in [proxy.conf.json](citrus-admin-client/src/main/resources/static/proxy.conf.json). 

Limitations
---------

At the moment we are focused on supporting standard Citrus projects using Maven, 
TestNG/JUnit and Java 8.

Resources
---------

* Citrus's source repository is hosted on github.com. You can fork/clone the
repository on [https://github.com/christophd/citrus-admin](https://github.com/christophd/citrus-admin)

* Find our blog and more interesting articles around Citrus on
[http://labs.consol.de](http://labs.consol.de) and checkout the various post categories for selecting a specific topic.

* [https://www.citrusframework.org](https://www.citrusframework.org) offers tutorials and more information about the Citrus framework.

Licensing
---------
  
Copyright 2006-2017 ConSol* Software GmbH.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
  
Bugs
---------

Please report any bugs and/or feature requests to dev@citrusframework.org
or directly to http://github.com/christophd/citrus-admin/issues
  
Team
---------

```
ConSol Software GmbH
Christoph Deppisch
christoph.deppisch@consol.de

http://www.citrusframework.org
```

Information
---------

For more information on Citrus see [www.citrusframework.org][2], including
a complete [reference manual][3].

 [1]: http://www.citrusframework.org/img/brand-logo.png "Citrus"
 [2]: http://www.citrusframework.org
 [3]: http://www.citrusframework.org/reference/html/

