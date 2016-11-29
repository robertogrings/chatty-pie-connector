#!/bin/sh

exec java ${JAVA_OPTS} -jar /${JAR_NAME} --signature.validation.use.https=true $*
