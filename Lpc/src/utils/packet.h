#ifndef LPC_PACKET_H
#define LPC_PACKET_H

#include <sys/types.h>

#define MAX_SIZE_DATA 10
#define FUN_NAME_SIZE 100
#define DEFAULT_SHARED_MEMORY_NAME "shmem"
#define DEFAULT_SHARED_MEMORY_SIZE 2048

/*
 *  lpc type
 */

typedef enum {
    STRING, DOUBLE, INT, NOP
} lpc_type;

typedef struct {
    int length;
    char string[100];
} lpc_string;

/*
 * Shared memory header
 */

typedef struct Header {
    pid_t pid;
    pthread_mutex_t mutex;
    pthread_cond_t rcond;
    pthread_cond_t wcond;
    int read_ready; /*
                    * 0 If a process CANNOT read on the memory
                    * 1 If a process can read on the memory
                    */
    int write_ready;/*
                    * 0 If a process CANNOT write on the memory
                    * 1 If a process can write on the memory
                    */
    int copy_ready; /*
                    * 0 If a copy of the memory is NOT ready
                    * 1 If a copy of the memory is ready
                    */
    int call_terminated; /*
                    * 0 If lpc_call is ongoing
                    * 1 If lpc_call has terminated
                    */
    int ready;
    int errsv;
    int data_size;
    char fun_name[FUN_NAME_SIZE];
    int function_return;
} Header;

/*
 * Shared memory data
 */

typedef struct Data {
    lpc_type type;
    union {
        int i;
        lpc_string s;
        double d;
    };
} Data;

/*
 * Shared memory object
 */

typedef struct Packet {
    Header header;
    Data data[MAX_SIZE_DATA];
} Packet;

#endif //LPC_PACKET_H
