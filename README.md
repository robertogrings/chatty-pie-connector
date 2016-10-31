# chatty-pie-connector

A minimal example of a connector using the service-integration SDK


##Running the application
To run locally, start the main class in 
```
ChattyPieConnectorApplication
```
This starts the connector application's http server and exposes a single endpoing

```
/api/v1/integration/processEvent
```

which a marketplace can send event notifications to.

Currently the connector implements a single event handler that treats
any event and returns a message containing the type of the event handled.

##Deploying the application

This application is deployed to Kubernetes

### Kubernetes:
Check the developer documentation available [here](https://appdirect.jira.com/wiki/display/EN/Developer+Access).

### Deployment process
You can deploy the application to Kubernetes by triggering the following
Jenkins job:

`http://jenkins.appdirect.com:8080/view/DSL/job/chatty-pie-connector-kubernetes-deploy-dsl/build?delay=0sec`

The `version` field corresponds to the version of the Docker image to deploy,
which in turn should match the project's version specified in the 
root `pom.xml`

Once deployed, the application can be accessed  at this URL: 
[https://dev-cpc.devappdirect.me](https://dev-cpc.devappdirect.me)

To verify that the server is running correctly, check if a GET request at 
[https://dev-cpc.devappdirect.me/health](https://dev-cpc.devappdirect.me/health)

returns 200(OK)

 
