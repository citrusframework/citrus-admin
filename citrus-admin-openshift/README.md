Citrus Admin Openshift
==============

This project configures and runs the citrus-admin application as deployment in [Openshift](https://www.openshift.com/).

Configuration
---------

This project contains YAML configuration files that describe the resources to deploy. You can visit the deployment configuration here

[openshift.yml](src/main/fabric8/openshift.yml)

The configuration uses some properties that you can customize while performing the deployment:

* `openshift.domain` the cluster domain **default=paasint.consol.de**
* `openshift.namespace` the target project namspace **default=citrus**
* `application.name` the application name **default=citrus-admin**

You can set these properties with Maven using the `-Dproperty.name=property.value` property name value pair settings.

Deployment to Openshift
---------

We can perform a complete deployment to Openshift with a Maven build using the **fabric8-maven-plugin**. The citrus-admin deployment includes the build configuration, a pod, 
a service, a route, an image stream and a replica set. All these Openshift resources are configured in [src/main/resources/fabric8](src/main/resources/fabric8). The configuration is ready to
go for a deployment with the *fabric8-maven-plugin*.

Of course you need a connection to the Openshift cluster first. We can do this by using the Openshift command line tool.

```
oc login
```

```
mvn -pl citrus-admin-openshift fabric8:deploy -Dopenshift.namespace=citrus -Dapplication.name=citrus-admin
```

This command starts a new deployment in Openshift. After that you should see the new deployment in the Openshift dashboard. Of course we need to connect
to a Openshift cluster. 

[Minishift](https://www.openshift.org/minishift/) is a fantastic way to get started with a local Openshift cluster for testing. Please refer to the 
[installation](https://docs.openshift.org/latest/minishift/getting-started/installing.html) description on how to set up the Minishift environment.
 
You will see the citrus-admin deployment with a new pod running and a service that connects you to the citrus-admin application web UI. The service is exposed to the world with

http://{application.name}.paasint.consol.de

Open your browser pointing to the citrus-admin instance web UI and you can start to create new tutorial sessions.

Environment settings
---------

You can add some environment variables in order to adjust the citrus-admin application behavior. The environment settings should be placed on the Openshift deployment. 
You can manage the environment settings with your Openshift namespace web UI.

These are the available settings:

* `CITRUS_ADMIN_PROJECT_HOME`: Preselect project on startup
* `CITRUS_ADMIN_ROOT_DIRECTORY`: System root as base of all projects (default: user home directory)
* `CITRUS_ADMIN_WORKING_DIRECTORY`: Base directory for new projects (default: root directory)
* `CITRUS_ADMIN_PROJECT_REPOSITORY`: Git project repository to load on startup (default: not set)
* `CITRUS_ADMIN_PROJECT_REPOSITORY_BRANCH`: Git project branch (default: master)
* `CITRUS_ADMIN_PROJECT_REPOSITORY_MODULE`: Module name (directory) representing the sub module in a Maven multi module repository (default: not set)
* `CITRUS_ADMIN_MAVEN_ARCHETYPE_COORDINATES`: Maven archetype coordinates (groupId:artifactId:version) to auto generate new project on startup (default: not set)
* `CITRUS_ADMIN_MAVEN_PROJECT_COORDINATES`: Project coordinates for new project generated from archetype (default: com.consol.citrus:citrus-project:1.0.0)
* `CITRUS_ADMIN_MAVEN_PROJECT_PACKAGE`: Package name for newly generated project (default: com.consol.citrus)
* `CITRUS_ADMIN_JAVA_SOURCE_DIRECTORY`: Java sources directory (default: *src/test/java*)
* `CITRUS_ADMIN_XML_SOURCE_DIRECTORY`: XML test sources directory (default: *src/test/resources*)
* `CITRUS_ADMIN_SPRING_APPLICATION_CONTEXT`: Path to Spring application context file (default: *src/test/resources/citrus-context.xml*)
* `CITRUS_ADMIN_SPRING_JAVA_CONFIG`: Java class holding Spring bean configurations (default: *com.consol.citrus.CitrusEndpointConfig*)
* `CITRUS_ADMIN_TEST_BASE_PACKAGE`: Base package where to add new tests (default: *com.consol.citrus*)

If you want to automatically load tutorials from a git repository you can set the env **CITRUS_ADMIN_PROJECT_REPOSITORY** pointing to a git clone url. The
citrus-admin will automatically clone the repository at startup.

Resource limits
---------

Your Openshift deployment automatically has limited resources. This may cause the pod to restart occasionally when resource limits are reached. As the citrus-admin application
creates new pods and performs some heavy operations you may want to increase the resource limits requested. You can do so in your Openshift deployment configuration.
Adding more pods in the replica set with service load balancing may also improve this.

Team
---------

```
ConSol Software GmbH
Christoph Deppisch
christoph.deppisch@consol.de

http://www.citrusframework.org
```