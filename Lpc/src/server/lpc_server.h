#include "../utils/utils.h"
#include "lpc_client_functions.h"
#include <stddef.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <errno.h>
#include <pthread.h>
#include <sys/mman.h>
#include <unistd.h>
#include <sys/wait.h>
#include <signal.h>

#ifndef LPC_LPC_SERVER_H
#define LPC_LPC_SERVER_H

#define FUN_NAME_LENGTH 48
#define FUN_NUMBER 4

typedef int (*fun)(void*, int);

typedef struct {
    char fun_name[FUN_NAME_LENGTH];
    fun function;
} lpc_function;

/**
 * Array of all lpc_functions that can be called by clients.
 */
lpc_function lpc_functions[FUN_NUMBER];

/**
 * Creates a shared memory object.
 *
 * @param name - name of the shared memory object
 * @param capacity - size of the shared memory object
 * @return pointer to the shared memory object
 *
 * If the shared memory with name "name" already exists,
 * modifies the size of the shared memory to match "capacity * page_size"
 */
void *lpc_create(const char *name, size_t capacity);

/**
 * Wait for a client call to call a function and write its result to the shared memory object
 * pointed by memory.
 *
 * @param memory the shared memory object to write results on.
 *
 * @return 0 if everything goes well, -1 otherwise.
 */
int lpc_execute(Packet *memory);

/**
 * Creates an lpc_function.
 *
 * @param name function name
 * @param f function pointer
 * @return the lpc_function created
 */
lpc_function create_lpc_function(char *name, void *function);

/**
 * Initializes all lpc_functions that the server can handle.
 */
void initialize_lpc_functions();

/**
 * Returns a function pointer searching through an array of lpc_function.
 *
 * @param fun_name represent the function name to search for
 * @return the function pointer associated with the name passed as argument
 */
fun get_lpc_function_by_name(char *fun_name);

#endif //LPC_LPC_SERVER_H