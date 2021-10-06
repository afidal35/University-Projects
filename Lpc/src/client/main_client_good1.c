#include "lpc_client.h"

int main() {
    log_set_level(0); // INFO=2, TRACE=0

    log_info("Client process starting");
    Packet *memory = lpc_open(DEFAULT_SHARED_MEMORY_NAME);

    int code;
    int a = 10;
    int b = 20;
    int s = 0;

    log_info("Calling fun_addition(%d, %d, %d) function", a, b, s);

    log_trace("S before lpc_call to fun_addition is : %d ", s);

    code = lpc_call(memory, "fun_addition", INT, &a, INT, &b, INT, &s, NOP);

    log_trace("Return code is : %d", code);
    log_trace("Errno variable is : %s", strerror(errno));
    log_trace("S after lpc_call to fun_addition is : %d ", s);

    log_info("Result %d ", s);

    assert(s == a + b);
    assert(code == 0);

    lpc_close(DEFAULT_SHARED_MEMORY_NAME, memory);

    log_info("Exiting client");

    return 0;
}
