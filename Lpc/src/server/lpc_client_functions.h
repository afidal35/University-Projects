#include "../utils/packet.h"
#include "errno.h"
#include <sys/mman.h>
#include <unistd.h>
#include <string.h>
#include <sys/file.h>
#include <stdio.h>
#include <stdlib.h>

#ifndef LPC_LPC_CLIENT_FUNCTIONS
#define LPC_LPC_CLIENT_FUNCTIONS

int fun_addition(Data data [], int size);
int fun_hello(Data data [], int size);
int fun_read(Data data [], int size);
int fun_replace_concat(Data data [], int size);

#endif //LPC_LPC_CLIENT_FUNCTIONS