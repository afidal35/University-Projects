#!/bin/sh

# Env
APP_HOME=/usr/app
CONTAINER=gla-build-container
IMAGE=gla-build-img

# Build the application
docker build -t $IMAGE ./../.

# Create a container and copy artifacts
docker stop $CONTAINER || true && docker rm $CONTAINER || true
docker create --name $CONTAINER $IMAGE
docker cp $CONTAINER:$APP_HOME/app/build/. ./app/build/