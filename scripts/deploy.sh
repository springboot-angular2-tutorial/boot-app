#!/bin/sh

if [ -z "${ENV}" ]; then
  echo "ENV is required."
  exit 1
fi

# prepare tmp directory to deploy
TMP_DIR=.deploy

rm -rf ${TMP_DIR}
mkdir ${TMP_DIR}

# create jar
mvn clean package -DskipTests=true -Dmaven.javadoc.skip=true
cp ./target/springboot-angular2-tutorial-0.1.0.jar ${TMP_DIR}/app.jar

# create codedeploy archive
tar cvzf ${TMP_DIR}/codedeploy.tar.gz -C codedeploy .

# upload archives
aws s3 sync .deploy s3://deploy-${ENV}.hana053.com/micropost

# deploy
aws deploy create-deployment --application-name micropost \
  --s3-location bucket=deploy-${ENV}.hana053.com,key=micropost/codedeploy.tar.gz,bundleType=tgz \
  --deployment-group-name web
