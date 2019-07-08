#!/bin/bash

pushd app
mvn install
[ $? -eq 0 ] || exit $?;
popd

#Wipe out old jars
rm rar-libs/*.jar 2>/dev/null

#Wipe out old rar
rm *.rar 2>/dev/null

pushd generic-jms-ra
mvn clean install -DskipTests
[ $? -eq 0 ] || exit $?;
cp -v generic-jms-ra-jar/target/*Final.jar ../rar-libs
popd

pushd azureservicebus-jms-ra
mvn clean install
[ $? -eq 0 ] || exit $?;
cp -v target/*.rar ../ra.rar
popd

./build_wkamsp_jar.sh

docker stop $(docker ps | grep tomee-jac | tail -n 1 | awk '{ print $1 }')
docker build -t tomee-jac .
