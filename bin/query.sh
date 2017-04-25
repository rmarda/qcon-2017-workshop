#!/bin/bash

set -e

URL=$(aws cloudformation describe-stacks \
    --query 'Stacks[?StackName==`oscon-2017-tutorial`].Outputs[0][?OutputKey==`ApiUrl`].OutputValue' \
    --output text)

URL="${URL}/locations?state=NY"

echo "Sending query to '$URL'"

curl ${URL}

echo
