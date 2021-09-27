# Mn Stock Broker Application
## Micronaut 2.5.7 Documentation

- [User Guide](https://docs.micronaut.io/2.5.7/guide/index.html)
- [API Reference](https://docs.micronaut.io/2.5.7/api/index.html)
- [Configuration Reference](https://docs.micronaut.io/2.5.7/guide/configurationreference.html)
- [Micronaut Guides](https://guides.micronaut.io/index.html)
---

## Features

1. Dependency Injection
2. Micronaut Config
3. Lombok
4. Open API
---
# How to build/start/run project
## Before running the project
You must:
- install maven

## Running the Micronaut application
Simply run ```mvn clean install``` followed by ```mvn mn:run``` in the main directory

### Interactive API Documentation

To access each of the below interactive API documentation options for this project you will have to do the following: 
1. Make sure application is running
2. Open up the address where your server is running on a browser (displayed in the terminal where application was 
started)
3. Add the individual endpoints described below to the address where your server is running

   ie. server is running on http://localhost:8080 so to access swagger-ui you go to http://localhost:8080/swagger-ui


#### Swagger UI
The swagger endpoint is: ``/swagger-ui``

[Click here for Swagger documentation](https://swagger.io/docs/)

#### Rapidoc
The rapidoc endpoint is: ``/rapidoc``

[Click here for Rapidoc documentation](https://mrin9.github.io/RapiDoc/)

#### Redoc
The redoc endpoint is: ``/redoc``

[Click here for Redoc documentation](https://redoc.ly/docs)

## Useful Maven commands
Generally when we run any of the commands below, we add the mvn clean step so that the target folder generated 
from the previous build is removed before running a newer build. This is how the command would look on integrating 
the clean step with install phase: mvn clean install

If we want to run a step in debug mode for more detailed build information and logs, we will add -X to the command.

### mvn clean
Cleans the project and removes all files generated by the previous build

### mvn compile
Compiles source code of the project

### mvn test-compile
Compiles the test source code

### mvn test
Runs tests for the project
