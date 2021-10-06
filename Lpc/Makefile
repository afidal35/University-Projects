TARGET_EXEC_SERVER ?= lpc_server.out
TARGET_EXEC_CLIENT1 ?= lpc_client_good1.out
TARGET_EXEC_CLIENT2 ?= lpc_client_good2.out
TARGET_EXEC_CLIENT3 ?= lpc_client_good3.out
TARGET_EXEC_CLIENT4 ?= lpc_client_bad1.out
TARGET_EXEC_CLIENT5 ?= lpc_client_bad2.out
TARGET_EXEC_CLIENT6 ?= lpc_client_good4.out

CC		= gcc
ifeq ($(shell uname -s), Darwin)
CC=clang
endif
CFLAGS	= -std=c99
CFLAGS += -Wall
CFLAGS += -g
CFLAGS += -fsanitize=address -fno-omit-frame-pointer
CFLAGS += -D_XOPEN_SOURCE=500

LDFLAGS = -lm -fsanitize=address
ifeq ($(shell uname -s), Linux)
LDFLAGS += -lrt
endif
LDFLAGS += -pthread

BUILD_DIR ?= ./build
SRC_DIR ?= ./src

SRC_DIR_CLIENT ?= ./src/client
SRC_DIR_SERVER ?= ./src/server
SRC_DIR_UTILS ?= ./src/utils

CFLAGS += -I$(SRC_DIR)/.
CFLAGS += -I$(SRC_DIR_CLIENT)/.
CFLAGS += -I$(SRC_DIR_SERVER)/.
CFLAGS += -I$(SRC_DIR_UTILS)/.

all: $(BUILD_DIR)/$(TARGET_EXEC_CLIENT1) \
	$(BUILD_DIR)/$(TARGET_EXEC_CLIENT2) \
	$(BUILD_DIR)/$(TARGET_EXEC_CLIENT3) \
	$(BUILD_DIR)/$(TARGET_EXEC_CLIENT4) \
	$(BUILD_DIR)/$(TARGET_EXEC_CLIENT5) \
	$(BUILD_DIR)/$(TARGET_EXEC_CLIENT6) \
	$(BUILD_DIR)/$(TARGET_EXEC_SERVER)

.PHONY: clean all valgrind

# Shared objects
OBJS_SHARED = $(BUILD_DIR)/utils.o $(BUILD_DIR)/log.o
$(BUILD_DIR)/utils.o: $(SRC_DIR_UTILS)/utils.c
	$(MKDIR_P) $(dir $@)
	$(CC) $(CFLAGS) -c $< -o $@

$(BUILD_DIR)/log.o: $(SRC_DIR_UTILS)/log.c
	$(MKDIR_P) $(dir $@)
	$(CC) $(CFLAGS) -c $< -o $@

# Good client 1
OBJS_CLIENT1 = $(BUILD_DIR)/main_client_good1.o $(BUILD_DIR)/lpc_client.o $(OBJS_SHARED)

$(BUILD_DIR)/main_client_good1.o: $(SRC_DIR_CLIENT)/main_client_good1.c
	$(MKDIR_P) $(dir $@)
	$(CC) $(CFLAGS) -c $< -o $@

$(BUILD_DIR)/lpc_client.o: $(SRC_DIR_CLIENT)/lpc_client.c
	$(MKDIR_P) $(dir $@)
	$(CC) $(CFLAGS) -c $< -o $@

$(BUILD_DIR)/$(TARGET_EXEC_CLIENT1): $(OBJS_CLIENT1)
	$(MKDIR_P) $(dir $@)
	$(CC) $(OBJS_CLIENT1) -o $@ $(LDFLAGS)

# Good client 2
OBJS_CLIENT2 = $(BUILD_DIR)/main_client_good2.o $(BUILD_DIR)/lpc_client.o $(OBJS_SHARED)

$(BUILD_DIR)/main_client_good2.o: $(SRC_DIR_CLIENT)/main_client_good2.c
	$(MKDIR_P) $(dir $@)
	$(CC) $(CFLAGS) -c $< -o $@

$(BUILD_DIR)/$(TARGET_EXEC_CLIENT2): $(OBJS_CLIENT2)
	$(MKDIR_P) $(dir $@)
	$(CC) $(OBJS_CLIENT2) -o $@ $(LDFLAGS)

# Good client 3
OBJS_CLIENT3 = $(BUILD_DIR)/main_client_good3.o $(BUILD_DIR)/lpc_client.o $(OBJS_SHARED)

$(BUILD_DIR)/main_client_good3.o: $(SRC_DIR_CLIENT)/main_client_good3.c
	$(MKDIR_P) $(dir $@)
	$(CC) $(CFLAGS) -c $< -o $@

$(BUILD_DIR)/$(TARGET_EXEC_CLIENT3): $(OBJS_CLIENT3)
	$(MKDIR_P) $(dir $@)
	$(CC) $(OBJS_CLIENT3) -o $@ $(LDFLAGS)

# Bad client 1
OBJS_CLIENT4 = $(BUILD_DIR)/main_client_bad1.o $(BUILD_DIR)/lpc_client.o $(OBJS_SHARED)

$(BUILD_DIR)/main_client_bad1.o: $(SRC_DIR_CLIENT)/main_client_bad1.c
	$(MKDIR_P) $(dir $@)
	$(CC) $(CFLAGS) -c $< -o $@

$(BUILD_DIR)/$(TARGET_EXEC_CLIENT4): $(OBJS_CLIENT4)
	$(MKDIR_P) $(dir $@)
	$(CC) $(OBJS_CLIENT4) -o $@ $(LDFLAGS)

# Bad client 2
OBJS_CLIENT5 = $(BUILD_DIR)/main_client_bad2.o $(BUILD_DIR)/lpc_client.o $(OBJS_SHARED)

$(BUILD_DIR)/main_client_bad2.o: $(SRC_DIR_CLIENT)/main_client_bad2.c
	$(MKDIR_P) $(dir $@)
	$(CC) $(CFLAGS) -c $< -o $@

$(BUILD_DIR)/$(TARGET_EXEC_CLIENT5): $(OBJS_CLIENT5)
	$(MKDIR_P) $(dir $@)
	$(CC) $(OBJS_CLIENT5) -o $@ $(LDFLAGS)

# Good client 4
OBJS_CLIENT6 = $(BUILD_DIR)/main_client_good4.o $(BUILD_DIR)/lpc_client.o $(OBJS_SHARED)

$(BUILD_DIR)/main_client_good4.o: $(SRC_DIR_CLIENT)/main_client_good4.c
	$(MKDIR_P) $(dir $@)
	$(CC) $(CFLAGS) -c $< -o $@

$(BUILD_DIR)/$(TARGET_EXEC_CLIENT6): $(OBJS_CLIENT6)
	$(MKDIR_P) $(dir $@)
	$(CC) $(OBJS_CLIENT6) -o $@ $(LDFLAGS)

# Server
OBJS_SERVER = $(BUILD_DIR)/main_server.o \
	$(BUILD_DIR)/lpc_server.o \
	$(BUILD_DIR)/lpc_client_functions.o \
	$(OBJS_SHARED)

$(BUILD_DIR)/main_server.o: $(SRC_DIR_SERVER)/main_server.c
	$(MKDIR_P) $(dir $@)
	$(CC) $(CFLAGS) -c $< -o $@

$(BUILD_DIR)/lpc_server.o: $(SRC_DIR_SERVER)/lpc_server.c
	$(MKDIR_P) $(dir $@)
	$(CC) $(CFLAGS) -c $< -o $@

$(BUILD_DIR)/lpc_client_functions.o: $(SRC_DIR_SERVER)/lpc_client_functions.c
	$(MKDIR_P) $(dir $@)
	$(CC) $(CFLAGS) -c $< -o $@

$(BUILD_DIR)/$(TARGET_EXEC_SERVER): $(OBJS_SERVER)
	$(MKDIR_P) $(dir $@)
	$(CC) $(OBJS_SERVER) -o $@ $(LDFLAGS)

clean:
	$(RM) -r $(BUILD_DIR)

valgrind:
	valgrind --leak-check=full \
         --show-leak-kinds=all \
         --track-origins=yes \
         --verbose \
         --log-file=$(BUILD_DIR)/valgrind-out.txt \
         $(BUILD_DIR)/$(TARGET_EXEC_SERVER)
	valgrind --leak-check=full \
		 --show-leak-kinds=all \
		 --track-origins=yes \
		 --verbose \
		 --log-file=$(BUILD_DIR)/valgrind-out.txt \
		 $(BUILD_DIR)/$(TARGET_EXEC_CLIENT)

MKDIR_P ?= mkdir -p
