FROM docker.appdirectondemand.com/appdirect/java:1.0.7
MAINTAINER Partner Integrations team <partner.integrations@appdirect.com>

ARG APPLICATION_JAR_NAME

COPY ./target/$APPLICATION_JAR_NAME /opt/chatty-pie-connector.jar

RUN echo $APPLICATION_JAR_NAME

EXPOSE 8080

ENTRYPOINT [ "java", "-server", "-Xms384m", "-Xmx384m", "-XX:MaxMetaspaceSize=128m", "-jar", "/opt/chatty-pie-connector.jar" ]
