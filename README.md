Citrus Administration Web UI ![Logo][1]
==============

This is a web administration user interface for the integration test framework 
Citrus [www.citrusframework.org][2] written in Angular2. Major functionality objectives
are project and configuration management as well as test execution and reporting.

Preconditions
---------

At the moment we are focused on supporting standard Citrus projects such as using Maven, 
TestNG or JUnit and Java 7 or higher.
   
Usage
---------

* Clone the repository and build locally using Maven

```mvn clean install```

* Use Spring Boot to start administration web application

```mvn -pl citrus-admin-web spring-boot:run```

* Open your browser and point to 'http://localhost:8080'

* Select Citrus project home and open project

Development
---------

First fo all start the server web applicaiton and keep it running:

```mvn -pl citrus-admin-web spring-boot:run```

For active development and a short roundtrip you can use gulp:watch in order to automatically compile typescript sources on the fly when they change.

```mvn -pl citrus-admin-client frontend:gulp -Pgulp-watch```

If you change a source file (e.e *.js, *.ts, *.css) the sources will automatically be compiled and copied to the Maven target folder. The running
spring-boot application is able to automatically grab the newly compiled sources. Just go to the browser and hit refresh to see the changes.
If you change server Java sources spring-boot automatically restarts the web application so you may just hit refresh in your browser, too.

Resources
---------

* Citrus's source repository is hosted on github.com. You can clone the
repository with git://github.com/christophd/citrus-admin.git as URL

* Find our blog and more interesting articles around Citrus on
http://labs.consol.de and checkout the various post categories for
selecting a specific topic.

* http://www.citrusframework.org offers tutorials and more information about
Citrus framework.

Licensing
---------
  
Copyright 2006-2016 ConSol* Software GmbH.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
  
Consulting
---------

Just in case you need professional support for Citrus have a look at
'http://www.citrusframework.org/contact.html'.
Contact user@citrusframework.org directly for any request or questions
(or use the contact form at 'http://www.consol.com/contact/')

Bugs
---------

Please report any bugs and/or feature requests to dev@citrusframework.org
or directly to http://github.com/christophd/citrus-admin/issues
  
Team
---------

ConSol* Software GmbH
Christoph Deppisch
christoph.deppisch@consol.de

http://www.citrusframework.org

Information
---------

For more information on Citrus see [www.citrusframework.org][2], including
a complete [reference manual][3].

 [1]: http://www.citrusframework.org/images/brand_logo.png "Citrus"
 [2]: http://www.citrusframework.org
 [3]: http://www.citrusframework.org/reference/html/

