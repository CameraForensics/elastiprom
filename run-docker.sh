#!/bin/bash

ENV_FILE=$1

if [[ -z "$ENV_FILE" ]]; then
    ENV_FILE="env-sample"
fi

echo "Running with env file: $ENV_FILE"

docker run --rm -it -p 8080:8080 --name elastiprom --env-file $ENV_FILE elastiprom:latest
