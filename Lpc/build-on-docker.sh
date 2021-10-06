#!/bin/bash

export PROJECT_DIR="/tmp/LPC"
export PATH=/usr/local/bin:$PATH

# Build
docker run -i \
  -v "$(pwd)":"$PROJECT_DIR" \
  --rm clion/remote-cpp-env:0.5 \
  bash -c "
  cd $PROJECT_DIR &&
  make clean &&
  make all"

# Test
docker run -i \
  -v "$(pwd)":"$PROJECT_DIR" \
  --rm clion/remote-cpp-env:0.5 \
  bash -c "
  cd $PROJECT_DIR/tests &&
  make clean &&
  make all"
