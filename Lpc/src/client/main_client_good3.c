#include "lpc_client.h"

int main() {
    log_set_level(0); // INFO=2, TRACE=0

    log_info("Client process starting");
    Packet *memory = lpc_open(DEFAULT_SHARED_MEMORY_NAME);
    int code;

    int fd = open("toto.txt", O_CREAT | O_RDWR, 0666);
    if (fd < 0)
        PANIC_EXIT("fd toto");

    char *t = "This is toto content";
    if (write(fd, t, strlen(t)) < 0)
        PANIC_EXIT("write toto");

    close(fd);

    log_trace("LPC Client -> fun_read lpc_call\n\n");

    lpc_string *str2 = lpc_make_string("Hey server, read the file !", 100);
    lpc_string *file_name = lpc_make_string("toto.txt", 100);

    log_info("Calling fun_read(%s, %s) function", str2->string, file_name->string);

    log_trace("S before lpc_call to fun_hello is : %s\n", str2->string);

    code = lpc_call(memory, "fun_read", STRING, str2, STRING, file_name, NOP);

    log_trace("Return code is : %d", code);
    log_trace("Errno variable is : %s", strerror(errno));
    log_trace("S after lpc_call to fun_hello is : %s", str2->string);

    log_info("Result %s", str2->string);

    assert(strcmp(t, str2->string) == 0);
    assert(code == 0);

    free(str2);
    free(file_name);

    lpc_close(DEFAULT_SHARED_MEMORY_NAME, memory);

    log_info("Exiting client");

    return 0;
}
