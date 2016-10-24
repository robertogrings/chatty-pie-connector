# chatty-pie-connector

A minimal example of a connector using the service-integration SDK


##Running the applicaiton
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
