Citrus Admin Docker Image
==============

This project builds and runs the [Docker](https://www.docker.com/) images for the Citrus admin web application.

Build image
---------

This project contains `Dockerfiles` and the Fabric8 [docker-maven-plugin](http://github.com/fabric8io/docker-maven-plugin) configuration for running the Citrus admin application in Docker. 
You can build the images locally with Maven calling

```
mvn docker:build
```

After that you should have a set of new images on your Docker host named `consol/citrus-admin`. You can use this base image in order to run
the Citrus admin application as a Docker container.

Start container
---------

The container is started using the Maven Docker plugin.

```
mvn docker:start
```

This command starts a new Citrus admin container. The web application should be accessible on

`http://localhost:8080`

Kubernetes
---------
 
The Citrus admin Docker approach is also ready to run in [Kubernetes](https://kubernetes.io/) or [Openshift](https://www.openshift.com/). When run as a 
Pod in Kubernetes the Citrus admin resources are able to access exposed services via Kubernetes service discovery.

Team
---------

ConSol* Software GmbH