#!/bin/sh

exec java ${JAVA_OPTS} -jar /${JAR_NAME} --spring.profiles.active=kubernetes $*
