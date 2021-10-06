#include "lpc_client.h"

int main() {
    log_set_level(0); // INFO=2, TRACE=0

    log_info("Client process starting");
    Packet *memory = lpc_open(DEFAULT_SHARED_MEMORY_NAME);
    int code;

    lpc_string *str = lpc_make_string("Hello from client", 100);

    log_info("Calling fun_string(%s) function", str->string);

    log_trace("S before lpc_call to fun_hello is : %s", str->string);

    code = lpc_call(memory, "fun_hello", STRING, str, NOP);

    log_trace("Return code is : %d", code);
    log_trace("Errno variable is : %s", strerror(errno));
    log_trace("S after lpc_call to fun_hello is : %s", str->string);

    log_info("Result %s ", str->string);

    char *from_server = "Hello from server";

    assert(strcmp(str->string, from_server) == 0);
    assert(code == 0);

    free(str);

    lpc_close(DEFAULT_SHARED_MEMORY_NAME, memory);

    log_info("Exiting client");

    return 0;
}
