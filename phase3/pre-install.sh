#!/bin/bash

set -e

REPOSITORY_ZIP="repository.zip"
S3_REPOSITORY_ZIP="s3://${MAVEN_S3_BUCKET}/${REPOSITORY_ZIP}"

echo "HOME: ${HOME}"
echo "S3_REPOSITORY_ZIP: ${S3_REPOSITORY_ZIP}"

pushd ${HOME}

if aws s3 ls ${S3_REPOSITORY_ZIP}; then
    echo "[${S3_REPOSITORY_ZIP}] found. Downloading to [${REPOSITORY_ZIP}]."
    aws s3 cp ${S3_REPOSITORY_ZIP} ${REPOSITORY_ZIP}
    unzip -q ${REPOSITORY_ZIP}
else
    echo "[${S3_REPOSITORY_ZIP}] not found."
fi

popd
