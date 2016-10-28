FROM docker.appdirectondemand.com/appdirect/java:1.0.7
MAINTAINER Partner Integrations team <partner.integrations@appdirect.com>

COPY ./target/chatty-pie-connector*.jar /opt/

RUN mv /opt/$(ls ./opt/chatty-pie-connector*.jar | xargs -n 1 basename) /opt/chatty-pie-connector.jar

EXPOSE 8080

ENTRYPOINT [ "java", "-server", "-Xms384m", "-Xmx384m", "-XX:MaxMetaspaceSize=128m", "-jar", "/opt/chatty-pie-connector.jar" ]
