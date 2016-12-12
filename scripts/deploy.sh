#!/usr/bin/env bash

set -u

if [ ! -v AWS_SESSION_TOKEN ]; then
  source ./scripts/switch-role.sh
fi

readonly DOCKER_NAME=micropost/backend
readonly AWS_ACCOUNT_NUMBER=$(aws sts get-caller-identity --output text --query 'Account')
readonly IMAGE_URL=${AWS_ACCOUNT_NUMBER}.dkr.ecr.${AWS_DEFAULT_REGION}.amazonaws.com/${DOCKER_NAME}

# Build
mvn clean package -DskipTests=true -Dmaven.javadoc.skip=true

# Ensure docker repository exists
aws ecr describe-repositories --repository-names ${DOCKER_NAME} || \
  aws ecr create-repository --repository-name ${DOCKER_NAME}

# Push to docker repository
eval $(aws ecr get-login)
docker build -t ${DOCKER_NAME} .
docker tag ${DOCKER_NAME}:latest ${IMAGE_URL}:latest
docker push ${IMAGE_URL}:latest

# Deploy
./scripts/ecs-deploy -c micropost -n backend -i ${IMAGE_URL}:latest
