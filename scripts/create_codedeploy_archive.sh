#!/bin/sh

# Pack all files required for codedeploy.

if [ -z "${ENV}" ]; then
  echo "Not a branch to deploy. Exit."
  exit
fi

TMP_DIR=.deploy

rm -rf ${TMP_DIR}
mkdir ${TMP_DIR}

mvn package -DskipTests=true -Dmaven.javadoc.skip=true
cp ./target/springboot-angular2-tutorial-0.1.0.jar ${TMP_DIR}/app.jar
cp -rfp codedeploy/* ${TMP_DIR}
cp -rfp ansible ${TMP_DIR}/ansible
aws s3 cp s3://deploy.hana053.com/application.yml ${TMP_DIR} --region ap-southeast-1

tar cvzf micropost-${TRAVIS_COMMIT}.tar.gz -C ${TMP_DIR} .
rm -rf ${TMP_DIR}/*
mv micropost-${TRAVIS_COMMIT}.tar.gz ${TMP_DIR}
