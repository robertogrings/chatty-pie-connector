# chatty-pie-connector

A minimal example of a connector using the service-integration SDK

## Requirements
* Java 8
* Docker
* Mysql running on `localhost:3306` with `root/password`

## Generating the DB classes
* Ensure you have a local instance of MySql running (localhost:3306)
* Ensure that your local db contains the latest version of the schema
    * You just have to run the application once while pointing to your local db and the schema would be
		updated automatically
* run `./mvnw clean install -DskipDbCodeGeneration=false -Dmodel.source.db.username=[your-local-db-user] -Dmodel.source.db.password=[your-local-db-password] -Dmodel.source.db.url=[connection-string-for-your-db-here]`.
	* All flags EXCEPT `-DskipDbCodeGeneration=false` are optional: if your local MySql instance is running on localhost 3306 and has
	  a root user called `root` with a password `password`, you can ommit them them;
	* If you run `./mvnw clean install` without any extra flags, this will build the project without regenerating the db sources
* The new sources will be generated directly in `src/main/java/com/chattypie/persistence/model`

## Running the application
### In the IDE
* run `ChattyPieConnectorApplication`

### On the command-line
* `./mvnw package && java -jar target/chatty-pie-connector.jar`

This starts the connector application's http server and exposes the main
endpoint `localhost:8080/api/v1/integration/processEvent` which a marketplace
can send event notifications to.

### Changing the default port
* By default, it starts on port `8080`.
* To override, add the `-Dserver.port=XXXX` jvm arg

### Starting dependencies locally
By default the application is expecting a MySQL instance to be running
at `localhost:3306` and a smtp server running on `localhost:3025`.
If you do not have both, we offer a docker compose file at the root. Run it with

    docker-compose up

## Accessing the app
The chatty-pie-connector should always be deployed in 3 environments: DEV, TEST and PROD
The URL for each of these three endpoints are available below:

* The DEV instance is available at https://dev-cpc.devappdirect.me
* The TEST instance is available at https://test-cpc.devappdirect.me
* The PROD instance is available at https://cpc.appdirect.com

To verify that the server is running correctly, check if a GET request at the `/health`
endpoint of a running CPC instance. For example, the endpoint for the dev 
[https://dev-cpc.devappdirect.me/health](https://dev-cpc.devappdirect.me/health)
returns 200(OK)

## Dependencies version update
Using the [`versions` maven plugin](http://www.mojohaus.org/versions-maven-plugin/), you can keep
your dependencies & maven plugins up-to-date. Most of those commands directly modify your `pom.xml`.
* update the `spring-boot` parent version: `./mvnw versions:update-parent`
* update all dependencies: `./mvnw versions:use-latest-releases`
    * Warning: this takes _15 minutes_ due to each dependency polling `artifactory.appdirect.com` (and it seems as if that is very slow)
* display available maven plugin updates `./mvnw versions:display-plugin-updates`
    * If newer versions are available, you will need to manually update `pom.xml`.

## Deployment procedure:
The instructions for deploying the application vary according to the target environment. Check them out at the 
[project wiki](https://github.com/AppDirect/chatty-pie-connector/wiki/Deployment-Procedure) 

## TestRails
* [We have some testrails here](https://appdirect.testrail.com/index.php?/suites/view/98&group_by=cases:section_id&group_id=2139181&group_order=asc)
