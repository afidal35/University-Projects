#include "lpc_client.h"

static void write_pid_to_shmo(Packet *default_memory);

static void wait_for_shmocom_to_be_created(Packet *default_memory);

static Packet *open_shmocom(char *shmocom_name);

static void write_function_data_to_shmocom(Packet *memory, va_list ap, const char *fun_name);

static void read_function_data_from_shmocom(Packet *memory, va_list ap, int *function_return);

static void signal_server_lpc_call_finished(Packet *memory);

void *lpc_open(const char *name) {
    int fd = shm_open(name, O_RDWR, S_IRUSR | S_IWUSR);
    if (fd < 0) {
        perror("Unable to open shared memory object");
        return NULL;
    }

    struct stat st;
    memset(&st, 0, sizeof(struct stat));

    if (fstat(fd, &st) < 0) {
        perror("Unable to get file stat");
        return NULL;
    }

    void *adr = mmap(0, st.st_size, PROT_READ | PROT_WRITE, MAP_SHARED, fd, 0);
    if (adr == MAP_FAILED) {
        perror("Unable to mmap file");
        return NULL;
    }
    return adr;
}

int lpc_close(const char *name, void *mem) {
    int fd = shm_open(name, O_RDONLY, S_IRUSR);
    if (fd < 0) {
        log_error("lpc_close cannot open shared memory object %s", name);
        return -1;
    }

    struct stat st;
    memset(&st, 0, sizeof(struct stat));

    if (fstat(fd, &st) < 0) {
        perror("Unable to get file stat");
        return -1;
    }

    if (munmap(mem, st.st_size) < 0) {
        perror("Unable to unmap shared memory object");
        return -1;
    }

    close(fd);
    return 0;
}

lpc_string *lpc_make_string(const char *s, int length) {
    lpc_string *str;
    if (length > 0 && s == NULL) {
        str = malloc(sizeof(lpc_string) + length);
        str->length = length;
        memset(str->string, '\0', length);
        return str;
    }

    if (s != NULL && length == strlen(s)) {
        str = malloc(sizeof(lpc_string) + strlen(s));
        str->length = (int) strlen(s);
        strcpy(str->string, s);
        return str;
    }

    if (length >= strlen(s) + 1) {
        str = malloc(sizeof(lpc_string) + length);
        str->length = length;
        memset(str->string, '\0', length);
        strcpy(str->string, s);
        return str;
    }

    return NULL;
}

int lpc_call(Packet *default_memory, const char *fun_name, ...) {
    va_list ap;
    int function_return;

    log_trace("Writing pid to shmo");
    write_pid_to_shmo(default_memory);

    log_trace("Waiting for shmo com to be created");
    wait_for_shmocom_to_be_created(default_memory);

    pid_t pid_client = getpid();

    char *shmocom_name = create_shmocom_name(pid_client);

    log_trace("Opening shmo com : %s", shmocom_name);
    Packet *memory = open_shmocom(shmocom_name);

    log_trace("Writing function args to shmo com");
    va_start(ap, fun_name);
    write_function_data_to_shmocom(memory, ap, fun_name);
    va_end(ap);

    log_trace("Reading shmo com and writing data to args");
    va_start(ap, fun_name);
    read_function_data_from_shmocom(memory, ap, &function_return);
    va_end(ap);

    log_trace("Signaling server that lpc_call finished");
    signal_server_lpc_call_finished(memory);

    log_trace("Closing shmo com");
    lpc_close(shmocom_name, memory);
    free(shmocom_name);

    return function_return;
}

/**
 * Client write his pid to shmo
 * @param default_memory
 */
static void write_pid_to_shmo(Packet *default_memory) {
    lock(&default_memory->header.mutex);

    while (default_memory->header.write_ready == 0) {
        wait_cond(&default_memory->header.wcond,
                  &default_memory->header.mutex);
    }

    default_memory->header.read_ready = 1;
    default_memory->header.write_ready = 0;
    default_memory->header.pid = getpid();

    unlock(&default_memory->header.mutex);

    signal_cond(&default_memory->header.wcond);
}

/**
 * Waiting for shmo communication to be created by the server
 * @param default_memory
 */
static void wait_for_shmocom_to_be_created(Packet *default_memory) {
    lock(&default_memory->header.mutex);

    while (default_memory->header.copy_ready == 0) {
        wait_cond(&default_memory->header.wcond,
                  &default_memory->header.mutex);
    }

    default_memory->header.copy_ready = 0;  // Reset flag
    default_memory->header.pid = -1;        // Remove pid

    unlock(&default_memory->header.mutex);
    signal_cond(&default_memory->header.wcond);
}

/**
 * Open shmo communication
 * @param shmocom_name
 * @return
 */
static Packet *open_shmocom(char *shmocom_name) {
    Packet *memory = lpc_open(shmocom_name);

    if (memory == NULL) {
        log_error("Unable to open shared copy memory %s", shmocom_name);
        free(shmocom_name);
        exit(1);
    }

    return memory;
}

/**
 * Write function data and parameters to shmo communication
 * Signal server that shmo communication can be read
 * @param memory
 * @param ap
 * @param fun_name
 */
static void write_function_data_to_shmocom(Packet *memory, va_list ap, const char *fun_name) {
    lpc_type type;
    int i = 0;
    lock(&memory->header.mutex);

    while (memory->header.ready) {
        wait_cond(&memory->header.rcond,
                  &memory->header.mutex);
    }

    memory->header.ready = 1;
    strcpy(memory->header.fun_name, fun_name);
    while ((type = va_arg(ap, lpc_type)) != NOP) {
        switch (type) {
            case INT:
                memory->data[i].type = INT;
                memory->data[i].i = *(va_arg(ap, int *));
                log_debug("argument %d is an int : %d", i, memory->data[i].i);
                break;
            case DOUBLE:
                memory->data[i].type = DOUBLE;
                memory->data[i].d = *(va_arg(ap, double *));
                log_debug("argument %d is a double : %f", i, memory->data[i].d);
                break;
            case STRING:
                memory->data[i].type = STRING;
                memcpy(&memory->data[i].s, va_arg(ap, lpc_string * ), sizeof(lpc_string));
                log_debug("argument %d is a string : %s", i, memory->data[i].s.string);
                break;
            default:
                break;
        }
        i++;
    }

    // Write NOP to the last index of data
    memory->data[i].type = NOP;
    memory->header.data_size = i;

    unlock(&memory->header.mutex);
    signal_cond(&memory->header.rcond);
}

/**
 * Read function data from shmo communication when signaled
 * @param memory
 * @param ap
 * @param function_return
 */
static void read_function_data_from_shmocom(Packet *memory, va_list ap, int *function_return) {
    int i = 0;
    lpc_type type;
    lock(&memory->header.mutex);

    while (memory->header.ready) {
        wait_cond(&memory->header.rcond,
                  &memory->header.mutex);
    }

    while ((type = va_arg(ap, lpc_type)) != NOP) {
        switch (type) {
            case INT:
                memcpy((void *) va_arg(ap, int *), &memory->data[i].i, sizeof(int));
                log_debug("argument %d is an int : %d", i, memory->data[i].i);
                break;
            case DOUBLE:
                memcpy((void *) va_arg(ap, double *), &memory->data[i].d, sizeof(double));
                log_debug("argument %d is a double : %f", i, memory->data[i].d);
                break;
            case STRING:
                memcpy((void *) va_arg(ap, lpc_string * ), &memory->data[i].s, sizeof(lpc_string));
                log_debug("argument %d is a string : %s", i, memory->data[i].s.string);
                break;
            default:
                break;
        }
        i++;
    }

    errno = memory->header.errsv;
    *function_return = memory->header.function_return;

    unlock(&memory->header.mutex);
    signal_cond(&memory->header.rcond);
}

/**
 * Signal server that lpc call has terminated
 * @param memory
 */
static void signal_server_lpc_call_finished(Packet *memory) {
    lock(&memory->header.mutex);
    signal_cond(&memory->header.rcond);
    unlock(&memory->header.mutex);
}