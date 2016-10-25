FROM docker.appdirectondemand.com/appdirect/java:1.0.7
MAINTAINER Partner Integrations team <partner.integrations@appdirect.com>

ENV APP_NAME=chatty-pie-connector

COPY ./target/chatty-pie-connector*.jar /opt/chatty-pie-connector.jar

EXPOSE 8090

ENTRYPOINT [ "java", "-server", "-Xms384m", "-Xmx384m", "-XX:MaxMetaspaceSize=128m", "-jar", "/opt/chatty-pie-connector.jar" ]
