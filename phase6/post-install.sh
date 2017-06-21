#!/bin/bash

set -e

REPOSITORY_ZIP="repository.zip"
M2_REPO=".m2/repository"
S3_REPOSITORY_ZIP="s3://${MAVEN_S3_BUCKET}/${REPOSITORY_ZIP}"

echo "HOME: ${HOME}"
echo "M2_REPO: ${M2_REPO}"
echo "S3_REPOSITORY_ZIP: ${S3_REPOSITORY_ZIP}"

pushd ${HOME}

OLD_CHECKSUM=
if [ -f ${REPOSITORY_ZIP} ]; then
    OLD_CHECKSUM=$(md5sum ${REPOSITORY_ZIP} | awk '{print $1}')
    echo "Old [${REPOSITORY_ZIP}] found, with checksum [$OLD_CHECKSUM]."
else
    echo "Old [${REPOSITORY_ZIP}] not found."
fi

echo "Updating old [${REPOSITORY_ZIP}] with contents of [${M2_REPO}]"

zip -qur ${REPOSITORY_ZIP} ${M2_REPO} || true # zip exits w/ status code 12 if nothing to update
NEW_CHECKSUM=$(md5sum ${REPOSITORY_ZIP} | awk '{print $1}')

echo "Updated [${REPOSITORY_ZIP}] has checksum [${NEW_CHECKSUM}]."

if [ "${OLD_CHECKSUM}" != "${NEW_CHECKSUM}" ]; then
    echo "Updated [${REPOSITORY_ZIP}] checksum [${NEW_CHECKSUM}] != old checksum [${OLD_CHECKSUM}]."
    echo "Uploading [${REPOSITORY_ZIP}] to [${S3_REPOSITORY_ZIP}]."
    aws s3 cp ${REPOSITORY_ZIP} ${S3_REPOSITORY_ZIP}
else
    echo "Updated [${REPOSITORY_ZIP}] checksum [${NEW_CHECKSUM}] == old checksum [${OLD_CHECKSUM}]."
    echo "No upload necessary."
fi

popd
