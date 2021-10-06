#include <pthread.h>
#include "utils.h"

void initialize_header(Header *header, int data_length) {
    header->pid = getpid();
    header->data_size = data_length;
    header->errsv = 0;
    header->ready = 0;
    header->read_ready = 0;
    header->write_ready = 1;
    header->copy_ready = 0;
    header->call_terminated = 0;
}

void print_header_data (Packet *memory) {
    for (int i = 0; i < memory->header.data_size; i++) {
        switch (memory->data[i].type) {
            case INT:
                log_debug("Argument %d is an int -> %d", i, memory->data[i].i);
                break;
            case DOUBLE:
                log_debug("Argument %d is a double -> %f", i, memory->data[i].d);
                break;
            case STRING:
                log_debug("Argument %d is a string : %s", i, memory->data[i].s.string);
                break;
            case NOP:
                log_debug("Argument %d is nop", i);
                break;
            default:
                break;
        }
    }
}

int initialize_mutex(pthread_mutex_t *pmutex) {
    pthread_mutexattr_t mutexattr;

    int code = pthread_mutexattr_init(&mutexattr);
    if (code != 0) {
        return code;
    }

    code = pthread_mutexattr_setpshared(&mutexattr, PTHREAD_PROCESS_SHARED);

    if (code != 0) {
        return code;
    }

    return pthread_mutex_init(pmutex, &mutexattr);
}

int initialize_cond(pthread_cond_t *pcond) {
    pthread_condattr_t condattr;

    int code = pthread_condattr_init(&condattr);

    if (code != 0) {
        return code;
    }

    code = pthread_condattr_setpshared(&condattr, PTHREAD_PROCESS_SHARED);

    if (code != 0) {
        return code;
    }

    return pthread_cond_init(pcond, &condattr);
}

void lock(pthread_mutex_t *mutex){
    int code = pthread_mutex_lock(mutex);
    if (code != 0) {
        thread_error_exit(__FILE__, __LINE__, code, "mutex_lock");
    }
    log_debug("Process %d acquired lock ", (int) getpid());
}

void unlock(pthread_mutex_t *mutex){
    int code = pthread_mutex_unlock(mutex);
    if (code != 0) {
        thread_error_exit(__FILE__, __LINE__, code, "mutex_unlock");
    }
    log_debug("Process %d released lock ", (int) getpid());
}

void signal_cond(pthread_cond_t *cond) {
    int code = pthread_cond_signal(cond);
    if (code != 0) {
        thread_error_exit(__FILE__, __LINE__, code, "Cond wcond");
    }
    log_debug("Process %d signaled ", (int) getpid());
}

void wait_cond(pthread_cond_t *cond, pthread_mutex_t *mutex) {
    int code = pthread_cond_wait(cond, mutex);
    if (code != 0) {
        thread_error_exit(__FILE__, __LINE__, code, "Pthread_cond_wait");
    }
}

void thread_error_exit(const char *file, int line, int code, char *txt) {
    if (txt != NULL)
        fprintf(stderr, "[%s] in file %s in line %d :  %s\n",
                txt, file, line, strerror(code));
    else
        fprintf(stderr, "in file %s in line %d :  %s\n",
                file, line, strerror(code));
    exit(1);
}

char *create_shmocom_name(pid_t pid_client) {
    char *copy_memory_name = malloc(strlen(DEFAULT_SHARED_MEMORY_NAME) + sizeof(char) * (pid_client+100));
    sprintf(copy_memory_name, "%s_%d", DEFAULT_SHARED_MEMORY_NAME, pid_client);
    return copy_memory_name;
}


