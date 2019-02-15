FROM openjdk:8u121-jdk-alpine

MAINTAINER Connectors team <pi-connector-rotation@appdirect.com>

ENV JAR_NAME=chatty-pie-connector.jar
ENV JAVA_OPTS="-Xms128M -Xmx128M -XX:MaxMetaspaceSize=64M"

RUN addgroup -g 1000 chatty-pie-connector && \
    adduser -S -u 1000 -g chatty-pie-connector chatty-pie-connector
    
USER chatty-pie-connector

COPY --chown=chatty-pie-connector:chatty-pie-connector src/main/bash/entrypoint.sh /entrypoint.sh
COPY --chown=chatty-pie-connector:chatty-pie-connector target/$JAR_NAME /$JAR_NAME

RUN chmod +x /entrypoint.sh

ENTRYPOINT ["/entrypoint.sh"]
