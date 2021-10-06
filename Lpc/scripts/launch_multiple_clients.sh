#!/bin/bash

export BUILD_DIR=../build

echo "Launching multiple clients"
./${BUILD_DIR}/lpc_client_good1.out >${BUILD_DIR}/good1.log 2>&1 &
./${BUILD_DIR}/lpc_client_good2.out >${BUILD_DIR}/good2.log 2>&1 &
./${BUILD_DIR}/lpc_client_good3.out >${BUILD_DIR}/good3.log 2>&1 &
./${BUILD_DIR}/lpc_client_good4.out >${BUILD_DIR}/good4.log 2>&1 &
./${BUILD_DIR}/lpc_client_bad1.out >${BUILD_DIR}/bad1.log 2>&1 &
./${BUILD_DIR}/lpc_client_bad2.out >${BUILD_DIR}/bad2.log 2>&1 &
