#!/bin/sh

TMP_DIR=.deploy

rm -rf ${TMP_DIR}
mkdir ${TMP_DIR}

mvn clean package -DskipTests=true -Dmaven.javadoc.skip=true
cp ./target/springboot-angular2-tutorial-0.1.0.jar ${TMP_DIR}/app.jar

tar cvzf ${TMP_DIR}/codedeploy.tar.gz -C codedeploy .
