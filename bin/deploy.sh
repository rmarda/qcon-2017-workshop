#!/bin/bash

set -e

BUCKET=symphonia-cloudformation
TEMPLATE_FILE=sam.yaml
OUTPUT_TEMPLATE_FILE=${TMPDIR}/output-`date +%s`-${TEMPLATE_FILE}
STACK_NAME="oscon-2017-tutorial"

# Check for creation of bucket, create if doesn't exist
aws s3api head-bucket --bucket ${BUCKET} 2>/dev/null \
    || aws s3api create-bucket --bucket ${BUCKET}

# Package and upload artifacts and resolve URIs in template
aws cloudformation package \
    --template-file ${TEMPLATE_FILE} \
    --s3-bucket ${BUCKET} \
    --output-template-file ${OUTPUT_TEMPLATE_FILE}

# Create (if necessary) and deploy Cloudformation stack
aws cloudformation deploy \
  --capabilities CAPABILITY_IAM \
  --template-file ${OUTPUT_TEMPLATE_FILE} \
  --stack-name ${STACK_NAME}

# Output API Gateway url
echo -n "API Gateway URL: "
aws cloudformation describe-stacks \
  --output text \
  --query "Stacks[?StackName==\`${STACK_NAME}\`]|[0].Outputs[?OutputKey==\`ApiUrl\`].OutputValue"

echo

# Clean up temporary output file
rm $OUTPUT_TEMPLATE_FILE
