#include "lpc_server.h"
#include <stdio.h>
#include <unistd.h>
#include <string.h>

void exit_server(int sig) {
    write(1, "Ctrl-c captured, exiting ...", 28);
    if (shm_unlink(DEFAULT_SHARED_MEMORY_NAME) < 0) {
        perror("Shm unlink");
        exit(1);
    }
    exit(0);
}

int main() {
    struct sigaction sa;

    log_set_level(2);
    log_info("Server is starting");
    log_info("Server pid : %d", (int) getpid());

    initialize_lpc_functions();

    Packet *memory = (Packet *) lpc_create(DEFAULT_SHARED_MEMORY_NAME, DEFAULT_SHARED_MEMORY_SIZE);

    memset(&sa, 0, sizeof(struct sigaction));
    sigemptyset(&sa.sa_mask);
    sigaddset(&sa.sa_mask, SIGINT);
    sigaddset(&sa.sa_mask, SIGTERM);

    sa.sa_handler = exit_server;
    if(sigaction(SIGINT, &sa, NULL) < 0) {
        perror("sigaction SIGINT");
        exit(1);
    }
    if(sigaction(SIGTERM, &sa, NULL) < 0) {
        perror("sigaction SIGTERM");
        exit(1);
    }

    log_info("Server started");

    while (1) {
        lpc_execute(memory);
    }

    return 0;
}
