#include "stdio.h"
#include "stdlib.h"
#include "packet.h"
#include "log.h"
#include <stdlib.h>
#include <stdarg.h>
#include <assert.h>
#include <string.h>
#include <unistd.h>

#ifndef LPC_UTILS_H
#define LPC_UTILS_H

#define PANIC_EXIT(msg) ({perror(msg); exit(1);})

void initialize_header(Header *header, int data_length);

void print_header_data(Packet *memory);

int initialize_mutex(pthread_mutex_t *pmutex);

int initialize_cond(pthread_cond_t *pcond);

void lock(pthread_mutex_t *mutex);

void unlock(pthread_mutex_t *mutex);

void signal_cond(pthread_cond_t *cond);

void wait_cond(pthread_cond_t *cond, pthread_mutex_t *mutex);

void thread_error_exit(const char *file, int line, int code, char *txt);

char *create_shmocom_name(pid_t pid_client);

#endif
