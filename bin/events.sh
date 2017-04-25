#!/bin/bash

set -e

URL=$(aws cloudformation describe-stacks \
    --query 'Stacks[?StackName==`oscon-2017-tutorial`].Outputs[0][?OutputKey==`ApiUrl`].OutputValue' \
    --output text)

URL=${URL}/events

echo "Sending requests to '$URL'"

curl -H "Content-type: application/json" \
    -s --data-raw "{\"timestamp\": `date +%s`, \"locationId\": \"location001\", \"temperature\": 32.0, \"locationName\": \"New York City\", \"state\": \"NY\"}" \
    ${URL}

echo
