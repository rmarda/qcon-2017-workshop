#!/bin/bash

set -e

URL=$(aws cloudformation describe-stacks \
    --query 'Stacks[?StackName==`pipeline-demo`].Outputs[0][?OutputKey==`ApiUrl`].OutputValue' \
    --output text)

echo "Sending requests to '$URL'"

grep '^.\{19\}$' /usr/share/dict/words \
  | xargs -P 5 -I % sh -c "curl -s --data-raw % ${URL}; echo"