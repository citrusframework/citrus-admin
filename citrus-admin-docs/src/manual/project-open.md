## Open project

You can open any Citrus project. The only prerequisit is that you have access to the project home on your local machine. When the administration UI is
started you have to open a project first.

![Open](screenshots/project-open.png)

The project home selection form is displayed automatically when no project has been selected yet. You can preselect a project home when starting the administration UI
by setting a system environment variable at startup:

```java -Dcitrus.admin.project.home=/Users/myaccount/path/tp/citrus/project/home -jar citrus-admin-web-1.0.0-beta-5.war```

When pre selecting a project home the project is opened automatically and the [project dashboard](project-dashboard.md) is displayed. Now back to the project home selection if no project has bee pre selected yet.

You need to specify the project home which is the root directory of your Citrus project. You can specify the complete path manually or pick the home directory over the file browser.

![Open](screenshots/project-home.png)

Once you have specified the project home you are ready to hit the **Open** button. Citrus will read the project information and open the [project dashboard](project-dashboard.md). The administration UI is looking
for several things in your project in order to gain information about the project. The files scanned are:

| Path                    | Description                           |
| ----------------------- | ------------------------------------- |
| **${project-home}/pom.xml** | Reads information from Maven POM  |
| **${project-home}/src/test/resources** | Reads XML test cases   |
| **${project-home}/src/test/resources/citrus-context.xml** | Reads Spring bean configuration |
| **${project-home}/src/test/java** | Reads test cases |

### Customize project settings

It is possible that your project uses a different folder layout for test resources and test sources (e.g. *src/it/resources* and *src/it/java*). Then the project open operation will fail with errors. We can fix this by customizing the project settings
manually in prior to opening the project. 

There are two different approaches to customizing the project settings: First of all you can use system properties when starting the administration UI application:

```
java -Dcitrus.admin.project.home=/Users/myaccount/path/tp/citrus/project/home -Dcitrus.admin.java.source.directory=src/it/java 
-Dcitrus.admin.xml.source.directory=src/it/resources -jar citrus-admin-web-1.0.0-beta-5.war
```

You can set the following system properties:

| Property                   | Description                           |
| -------------------------- | ------------------------------------- |
| server.port                | Web server port                       |
| citrus.admin.project.home               | Preselect project on startup          |
| citrus.admin.root.directory             | System root as base of all projects (default: user home directory) |
| citrus.admin.java.source.directory      | Java sources directory (default: *src/test/java*)         |
| citrus.admin.xml.source.directory       | XML test sources directory (default: *src/test/resources*) |
| citrus.admin.spring.application.context | Path to Spring application context file (default: *src/test/resources/citrus-context.xml*) |
| citrus.admin.spring.java.config | Java class holding Spring bean configurations (default: *com.consol.citrus.CitrusEndpointConfig*) |
| citrus.admin.test.base.package | Base package where to add new tests (default: *com.consol.citrus*) |
| maven.home.directory       | Path to Maven home that should be used in admin UI (when not set environment variable MAVEN_HOME or M2_HOME is used as default) |

You can also use Spring boot properties, e.g. a custom server port:

```
java -Dserver.port=8181 -jar citrus-admin-web-1.0.0-beta-5.war
```

The exact same properties are also available when set as environment variables:

| Environment variable                   | Description                           |
| -------------------------- | ------------------------------------- |
| CITRUS_ADMIN_PROJECT_HOME               | Preselect project on startup          |
| CITRUS_ADMIN_ROOT_DIRECTORY             | System root as base of all projects (default: user home directory) |
| CITRUS_ADMIN_JAVA_SOURCE_DIRECTORY      | Java sources directory (default: *src/test/java*)         |
| CITRUS_ADMIN_XML_SOURCE_DIRECTORY       | XML test sources directory (default: *src/test/resources*) |
| CITRUS_ADMIN_SPRING_APPLICATION_CONTEXT | Path to Spring application context file (default: *src/test/resources/citrus-context.xml*) |
| CITRUS_ADMIN_SPRING_JAVA_CONFIG | Java class holding Spring bean configurations (default: *com.consol.citrus.CitrusEndpointConfig*) |
| CITRUS_ADMIN_TEST_BASE_PACKAGE | Base package where to add new tests (default: *com.consol.citrus*) |


A second approach would be to create a project settings file in your Citrus project root directory. The project settings are stored in a file called **citrus-project.json**. When you open a Citrus project for the first time the administration UI creates this project settings file
automatically. But now we want to create this file manually in order to set custom directories and settings prior to opening the project. The setting file uses JSON data format and looks like this:
 
```
{
  "name" : "citrus-sample-project",
  "description" : "",
  "version" : "1.0.0",
  "settings" : {
    "basePackage" : "com.consol.citrus.samples",
    "citrusVersion" : "2.7.2-SNAPSHOT",
    "springApplicationContext" : "src/test/resources/citrus-context.xml",
    "javaSrcDirectory" : "src/test/java/",
    "xmlSrcDirectory" : "src/test/resources/",
    "javaFilePattern" : "/**/*Test.java,/**/*IT.java",
    "xmlFilePattern" : "/**/*Test.xml,/**/*IT.xml",
    "useConnector" : true,
    "connectorActive" : true,
    "tabSize" : 2,
    "build" : {
      "@class" : "com.consol.citrus.admin.model.build.maven.MavenBuildConfiguration",
      "type" : "maven",
      "properties" : [ ],
      "testPlugin" : "maven-failsafe",
      "command" : null,
      "profiles" : "",
      "useClean" : false
    }
  }
}
```

So you can force the administration UI to use these settings when opening the project. Just create the **citrus-project.json** file in the Citrus project home directory before opening the project. 
