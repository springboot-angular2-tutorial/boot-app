#!/bin/sh

. $(dirname $0)/common_variables.sh

ansible-playbook -i "${DST_PATH}/ansible/inventories/local"  \
  -e "app_jar=${DST_PATH}/app.jar" \
  -e "app_application_yml=${DST_PATH}/application.yml" \
  --connection=local \
  --tags "deploy" \
  ${DST_PATH}/ansible/site.yml
