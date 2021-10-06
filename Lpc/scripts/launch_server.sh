#!/bin/bash

export BUILD_DIR=../build
echo "Launching server"
./${BUILD_DIR}/lpc_server.out > ${BUILD_DIR}/server.log