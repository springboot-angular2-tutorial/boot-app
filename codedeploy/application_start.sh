#!/bin/sh

if [ ! -e /opt/micropost/env.sh ]; then
  echo "This is a newly created instance. Deployment will be done in cloud-init."
  exit 0
fi

. /opt/micropost/env.sh

(
cd /opt/provisioning

cat << EOF > inventory
[deploy]
localhost
EOF

ansible-playbook -i inventory --connection=local -e "deploy_bucket=${S3_DEPLOY_BUCKET}" site.yml
)