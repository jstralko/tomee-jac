#!/bin/bash

pushd app
mvn install
[ $? -eq 0 ] || exit $?;
popd

#Wipe out old jars
rm rar-libs/*.jar

pushd ../generic-jms-ra
mvn clean install -DskipTests
[ $? -eq 0 ] || exit $?;
cp -v generic-jms-ra-jar/target/*.jar ../tomee-jac/rar-libs
popd

#pushd ../qpid-jms
#mvn clean install -DskipTests
#cp -v qpid-jms-client/target/*.jar ../tomee-jac/rar-libs
#[ $? -eq 0 ] || exit $?;
#popd

./build_wkamsp_jar.sh

docker stop $(docker ps | grep tomee-jac | tail -n 1 | awk '{ print $1 }')
docker build -t tomee-jac .
