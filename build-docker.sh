#!/bin/bash

./gradlew clean build
BUILD_RESULT=$?
if [[ $BUILD_RESULT == 1 ]]; then
    echo "There was an error building the project. Please resolve errors and try again."
    exit $BUILD_RESULT
fi
docker build -t elastiprom:latest .