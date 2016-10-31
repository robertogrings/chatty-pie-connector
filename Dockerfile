FROM docker.appdirectondemand.com/appdirect/java:1.0.7
MAINTAINER Partner Integrations team <partner.integrations@appdirect.com>

COPY ./target/*.jar /opt/

RUN mv $(ls ./opt/chatty-pie-connector*.jar) ./opt/chatty-pie-connector.jar

RUN ls -la /opt

EXPOSE 8080

ENTRYPOINT [ "java", "-server", "-Xms384m", "-Xmx384m", "-XX:MaxMetaspaceSize=128m", "-jar", "/opt/chatty-pie-connector.jar" ]
