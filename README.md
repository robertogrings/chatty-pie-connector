# chatty-pie-connector

A minimal example of a connector using the service-integration SDK

## Requirements
* Java 8
* Docker
* Mysql running on `localhost:3306` with `root/password`

## Running the application
### In the IDE
* run `ChattyPieConnectorApplication`

### On the command-line
* `./mvnw package && java -jar target/chatty-pie-connector-*`

This starts the connector application's http server and exposes the main
endpoint `/api/v1/integration/processEvent` which a marketplace can send event notifications to.

The connector implements a single event handler that treats
any event and returns a message containing the type of the event handled.

## Deploying the application
This application is deployed to Kubernetes

### Kubernetes:
Check the developer documentation available [here](https://appdirect.jira.com/wiki/display/EN/Developer+Access).

### Jenkins Job
You can deploy the application to Kubernetes by triggering the following
Jenkins job: `https://pi.ci.appdirect.tools/job/chatty-pie-connector-kubernetes-deploy-dsl/build?delay=0sec`

The `version` field corresponds to the version of the Docker image to deploy,
which in turn should match the project's version specified in the 
root `pom.xml`

## Confirming it worked
* make sure all pods are running the same binary with `./check-deploy-integrity.sh`
    * this script checks to make sure all pods are returning the same git hash

## Accessing the app
Once deployed, the application can be accessed at this URL: https://dev-cpc.devappdirect.me/info

To verify that the server is running correctly, check if a GET request at https://dev-cpc.devappdirect.me/health
returns 200(OK)
