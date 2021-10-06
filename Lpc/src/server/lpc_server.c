#include "lpc_server.h"

static void wait_for_client_to_fill_shmocom(Packet *default_memory);

static void wait_for_client_to_consume_result(Packet *memory);

static int execute_function(Packet *memory);

void *lpc_create(const char *name, size_t capacity) {

    // Unlink for now, check for EEXIST later
    log_trace("Unlinking existing shared memory : %s", name);
    shm_unlink(name);

    int fd = shm_open(name, O_RDWR | O_CREAT, S_IRUSR | S_IWUSR);
    if (fd < 0)
        PANIC_EXIT("Unable to open shared memory object");

    size_t page_size = sysconf(_SC_PAGESIZE);
    if (ftruncate(fd, (int) (capacity * page_size)) < 0)
        PANIC_EXIT("Unable to ftruncate shared memory object");

    void *adr = mmap(0, capacity * page_size, PROT_READ | PROT_WRITE, MAP_SHARED, fd, 0);
    if (adr == MAP_FAILED)
        PANIC_EXIT("Unable to mmap shared memory object");

    Packet packet;
    initialize_header(&packet.header, 10);

    int code;
    if ((code = initialize_mutex(&packet.header.mutex)) != 0)
        thread_error_exit(__FILE__, __LINE__, code, "init_mutex");

    if ((code = initialize_cond(&packet.header.rcond)) != 0)
        thread_error_exit(__FILE__, __LINE__, code, "init_rcond");

    if ((code = initialize_cond(&packet.header.wcond)) != 0)
        thread_error_exit(__FILE__, __LINE__, code, "init_wcond");

    memcpy(adr, &packet, sizeof(Packet));

    return adr;
}

int lpc_execute(Packet *default_memory) {

    struct sigaction str;
    str.sa_handler = SIG_IGN;
    sigemptyset(&str.sa_mask);
    str.sa_flags = SA_NOCLDWAIT;    // Specify flag for no zombies
    char *sa = getenv("SIGACTION");
    if(sa != NULL){
        if(sigaction(SIGCHLD, &str, NULL) < 0)
            PANIC_EXIT("sigaction");
    }

    lock(&default_memory->header.mutex);

    while (default_memory->header.read_ready == 0) {
        log_trace("Waiting for client to write his pid");
        int code = pthread_cond_wait(&default_memory->header.wcond, &default_memory->header.mutex);
        if (code != 0) {
            thread_error_exit(__FILE__, __LINE__, code, "Pthread_cond_wait");
        }
    }

    default_memory->header.read_ready = 0;          // Server cannot read anymore until client send his pid
    pid_t pid_client = default_memory->header.pid;

    char *shmocom_name = create_shmocom_name(pid_client);
    log_trace("Creating shared copy memory %s for client %d", shmocom_name, pid_client);
    Packet *memory = (Packet *) lpc_create(shmocom_name, DEFAULT_SHARED_MEMORY_SIZE);

    if (memory == NULL) {
        log_error("Unable to create shared copy memory %s", shmocom_name);
        free(shmocom_name);
        exit(1);
    }

    default_memory->header.copy_ready = 1;          // Flag to inform the client that the copy is ready

    unlock(&default_memory->header.mutex);
    signal_cond(&default_memory->header.wcond);     // Wake up the client

    wait_for_client_to_fill_shmocom(default_memory);

    pid_t child = fork();

    if (child < 0) {
        PANIC_EXIT("Unable to fork");
    } else if (child == 0) {

        log_trace("Spawned server handle this %d", getpid());

        int res = execute_function(memory);
        if (res == -1)
            return -1;
        log_trace("Function has been correctly executed");
        wait_for_client_to_consume_result(memory);
        log_trace("Client has consumed results wrote on memory");

        log_trace("Removing shared copy memory %s", shmocom_name);     // Removing shared copy memory
        shm_unlink(shmocom_name);
        free(shmocom_name);

        log_trace("Exiting process %d", getpid());

        exit(0);
    }

    if(kill(child, 0) < 0){
        if( errno == ESRCH )
            log_trace( "Process %d does not exists\n", (int) child);
        else
            log_trace("Kill");
    }else
        log_trace("Child process exists\n");

    return 0;
}

lpc_function create_lpc_function(char *name, void *function) {
    lpc_function f;
    strcpy(f.fun_name, name);
    f.function = function;
    return f;
}

void initialize_lpc_functions() {
    lpc_functions[0] = create_lpc_function("fun_addition", fun_addition);
    lpc_functions[1] = create_lpc_function("fun_hello", fun_hello);
    lpc_functions[2] = create_lpc_function("fun_read", fun_read);
    lpc_functions[3] = create_lpc_function("fun_concat", fun_replace_concat);
}

fun get_lpc_function_by_name(char *fun_name) {
    for (int i = 0; i < FUN_NUMBER; i++) {
        if (strcmp(lpc_functions[i].fun_name, fun_name) == 0)
            return lpc_functions[i].function;
    }
    return NULL;
}

/**
 * Waits for the client to fill the shared memory communication object with
 * function name and arguments.
 *
 * @param default_memory shared memory object to wait on.
 */
static void wait_for_client_to_fill_shmocom(Packet *default_memory) {
    lock(&default_memory->header.mutex);
    while (default_memory->header.copy_ready == 1) {
        log_trace("Waiting for shared copy memory to be opened by client");
        int code = pthread_cond_wait(&default_memory->header.wcond, &default_memory->header.mutex);
        if (code != 0) {
            thread_error_exit(__FILE__, __LINE__, code, "Pthread_cond_wait");
        }
    }

    default_memory->header.write_ready = 1;

    unlock(&default_memory->header.mutex);
    signal_cond(&default_memory->header.wcond);
}

/**
 * Waits until the client have consumed the result of the server call.
 *
 * @param memory shared memory object to wait on.
 */
static void wait_for_client_to_consume_result(Packet *memory) {
    lock(&memory->header.mutex);
    while (memory->header.call_terminated == 0) {
        log_trace("Waiting for client to finish lpc_call");
        int code = pthread_cond_wait(&memory->header.rcond, &memory->header.mutex);
        if (code != 0)
            thread_error_exit(__FILE__, __LINE__, code, "Pthread_cond_wait");
    }
    unlock(&memory->header.mutex);
}

/**
 * Executes the function specified by the shared memory parameters.
 *
 * @param memory the shared memory object to read and write results on.
 *
 * @return -1 if the function called does not exists, 0 otherwise.
 */
static int execute_function(Packet *memory) {
    lock(&memory->header.mutex); // Now the simple process start, executed by the child

    while (memory->header.ready == 0) {
        log_trace("Process %d waiting on condition ", (int) getpid());
        int code = pthread_cond_wait(&memory->header.rcond, &memory->header.mutex);
        if (code != 0)
            thread_error_exit(__FILE__, __LINE__, code, "Pthread_cond_wait");
    }

    log_trace("Calling function : %s", memory->header.fun_name);

    fun f = get_lpc_function_by_name(memory->header.fun_name);
    if (f == NULL)
        return -1;

    int function_return = f(memory->data, memory->header.data_size);

    memory->header.function_return = function_return;
    memory->header.errsv = errno;
    memory->header.ready = 0;

    unlock(&memory->header.mutex);

    log_trace("Function call is done, waking up client");
    signal_cond(&memory->header.rcond);

    return 0;
}
