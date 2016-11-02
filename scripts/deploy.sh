#!/usr/bin/env bash

set -u

if [ ! -v AWS_SESSION_TOKEN ]; then
  source ./scripts/switch-role.sh
fi

# create and upload jar
mvn clean package -DskipTests=true -Dmaven.javadoc.skip=true
aws s3 cp ./target/springboot-angular2-tutorial-0.1.0.jar s3://deploy-${ENV}.hana053.com/micropost/app.jar

# Publish to SNS Topic
account_number=$(aws sts get-caller-identity --output text --query 'Account')
aws sns publish --topic-arn "arn:aws:sns:${AWS_DEFAULT_REGION}:${account_number}:backend_app_updated" \
   --message "${ENV}: ${TRAVIS_COMMIT}"

