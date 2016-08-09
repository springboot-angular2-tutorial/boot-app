#!/bin/sh

. /opt/micropost/env.sh

(
cd /opt/provisioning

cat << EOF > inventory
[deploy]
localhost
EOF

ansible-playbook -i inventory --connection=local -e "deploy_bucket=${S3_DEPLOY_BUCKET}" site.yml
)