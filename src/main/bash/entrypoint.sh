#!/bin/sh

exec java ${JAVA_OPTS} -Dspring.profiles.active=kubernetes -jar /${JAR_NAME} $*
