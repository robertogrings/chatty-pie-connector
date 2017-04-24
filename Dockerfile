FROM openjdk:8u121-jdk-alpine

MAINTAINER Partner Integrations team <partner.integrations@appdirect.com>

ENV JAR_NAME=chatty-pie-connector.jar
ENV JAVA_OPTS="-Xms128M -Xmx128M -XX:MaxMetaspaceSize=64M"

COPY src/main/bash/entrypoint.sh /entrypoint.sh
COPY target/$JAR_NAME /$JAR_NAME
RUN chmod +x /entrypoint.sh

ENTRYPOINT ["/entrypoint.sh"]
