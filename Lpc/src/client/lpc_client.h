#ifndef LPC_LPC_CLIENT_H
#define LPC_LPC_CLIENT_H

#include <stddef.h>
#include <stdio.h>
#include <sys/mman.h>
#include <sys/fcntl.h>
#include <sys/stat.h>
#include <string.h>
#include <unistd.h>
#include <stdlib.h>
#include <stdarg.h>
#include <pthread.h>
#include <errno.h>
#include "../utils/packet.h"
#include "../utils/utils.h"

/**
 * Open the shared memory object which name is [name] and do its projection in memory.
 * @param name, the name of the shared memory object.
 * @return the address of the memory if it exists, NULL otherwise.
 */
void *lpc_open(const char *name);

/**
 * Call this function whenever the client does not want to make server function calls anymore.
 * @param name ""
 * @param mem ""
 * @return ""
 */
int lpc_close(const char *name, void *mem);

/**
 * A client process calls this function to make a server function call.
 * @param memory the address of the shared memory returned by [lpc_open] (see above).
 * @param fun_name the function name to be called.
 * @return ""
 */
int lpc_call(Packet *memory, const char *fun_name, ...);

/**
 * Helper aiming at constructing [lpc_string].
 * @param s string.
 * @param length string length.
 * @return
 * if length > 0 && s == NULL : allocate the memory for lpc_string with
 * the field [.string] of length [.length], [.length] will be equal to length.
 *
 * if length <= 0 && s != NULL : allocate the memory with the array [.string] of length strlen(s) + 1 and copy
 * the string pointed by [s], as well [.length] will be equal to length + 1.
 *
 * if length >= strlen(s) + 1 : allocate the structure ...
 *
 * NULL otherwise.
 */
lpc_string *lpc_make_string(const char *s, int length);

#endif //LPC_LPC_CLIENT_H
