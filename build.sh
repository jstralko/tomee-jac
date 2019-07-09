#!/bin/bash

pushd app
mvn install
[ $? -eq 0 ] || exit $?;
popd

#Wipe out old jar
rm amqps-service-info.jar 2>/dev/null

pushd generic-jms-ra
#TODO: fix so we dont have to skipTests
mvn clean install -DskipTests
[ $? -eq 0 ] || exit $?;
popd

pushd azureservicebus-jms-ra
mvn clean install
[ $? -eq 0 ] || exit $?;
popd

jar -cvf amqps-service-info.jar META-INF

docker stop $(docker ps | grep tomee-jac | tail -n 1 | awk '{ print $1 }')
docker build -t tomee-jac .
