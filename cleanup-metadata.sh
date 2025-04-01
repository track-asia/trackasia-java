#!/bin/bash

# Define Maven repository path
MAVEN_REPO=~/.m2/repository/io/github/track-asia

# Xóa tất cả các file maven-metadata-local.xml
echo "Xóa các file maven-metadata-local.xml..."
find $MAVEN_REPO -name "*maven-metadata-local.xml*" -exec rm -v {} \;

# Xóa tất cả các file có chứa metadata-local
echo "Xóa các file metadata-local..."
find $MAVEN_REPO -name "*metadata*" -exec rm -v {} \;

echo "Hoàn tất xóa các file metadata." 