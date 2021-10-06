# ==========================================
#   Unity Project - A Test Framework for C
#   Copyright (c) 2007 Mike Karlesky, Mark VanderVoord, Greg Williams
#   [Released under MIT License. Please refer to license.txt for details]
# ==========================================

#We try to detect the OS we are running on, and adjust commands as needed
CLEANUP = rm -f
MKDIR = mkdir -p
TARGET_EXTENSION=.out

C_COMPILER=gcc
ifeq ($(shell uname -s), Darwin)
C_COMPILER=clang
endif

UNITY_ROOT=./unity
PROJECT_ROOT=..
PROJECT_SRC=./../src

CFLAGS=-std=c99
CFLAGS += -Wall
CFLAGS += -D_XOPEN_SOURCE=500
#CFLAGS += -Wextra
#CFLAGS += -Wpointer-arith
#CFLAGS += -Wcast-align
#CFLAGS += -Wwrite-strings
#CFLAGS += -Wswitch-default
#CFLAGS += -Wunreachable-code
#CFLAGS += -Winit-self
#CFLAGS += -Wmissing-field-initializers
#CFLAGS += -Wno-unknown-pragmas
#CFLAGS += -Wstrict-prototypes
#CFLAGS += -Wundef
#CFLAGS += -Wold-style-definition
#CFLAGS += -Wno-misleading-indentation


ifeq ($(shell uname -s), Linux)
LDFLAGS += -lrt
endif
LDFLAGS += -pthread


TARGET_BASE1=test_server
TARGET1 = $(TARGET_BASE1)$(TARGET_EXTENSION)
SRC_FILES1=$(UNITY_ROOT)/unity.c \
	$(PROJECT_ROOT)/src/server/lpc_server.c  \
	$(PROJECT_ROOT)/src/server/lpc_client_functions.c  \
	$(PROJECT_ROOT)/tests/lpc_server_test.c \
	$(PROJECT_ROOT)/src/utils/utils.c \
	$(PROJECT_ROOT)/src/utils/log.c

TARGET_BASE2=test_client
TARGET2 = $(TARGET_BASE2)$(TARGET_EXTENSION)
SRC_FILES2=$(UNITY_ROOT)/unity.c \
	$(PROJECT_ROOT)/src/client/lpc_client.c  \
	$(PROJECT_ROOT)/tests/lpc_client_test.c \
	$(PROJECT_ROOT)/src/utils/utils.c \
	$(PROJECT_ROOT)/src/utils/log.c


INC_DIRS=-Isrc -I$(UNITY_ROOT)/ -I$(PROJECT_SRC)/
all: clean default

default: $(SRC_FILES1) $(SRC_FILES2)
	$(C_COMPILER) $(CFLAGS) $(INC_DIRS) $(SYMBOLS) $(SRC_FILES1) -o $(TARGET1) $(LDFLAGS)
	- ./$(TARGET1)
	$(C_COMPILER) $(CFLAGS) $(INC_DIRS) $(SYMBOLS) $(SRC_FILES2) -o $(TARGET2) $(LDFLAGS)
	- ./$(TARGET2)

clean:
	$(CLEANUP) $(TARGET1) $(TARGET2)

ci: CFLAGS += -Werror
ci: default
