#include "lpc_client.h"

int main() {
    log_set_level(0); // INFO=2, TRACE=0

    log_info("Client process starting");
    Packet *memory = lpc_open(DEFAULT_SHARED_MEMORY_NAME);
    int code;

    lpc_string *s1 = lpc_make_string("Five", 20);
    lpc_string *s2 = lpc_make_string("Ten", 20);
    lpc_string *s3 = lpc_make_string("Twenty", 20);
    int a = 5;
    double b = 2;
    lpc_string *s4 = lpc_make_string("Concat here", 100);

    log_info("Calling fun_concat function");

    log_trace("S1 before lpc_call to fun_concat is : %s ", s1->string);
    log_trace("S1 size before lpc_call to fun_concat is : %d ", s1->length);

    log_trace("S2 before lpc_call to fun_concat is : %s ", s2->string);
    log_trace("S2 size before lpc_call to fun_concat is : %d ", s2->length);

    log_trace("S3 before lpc_call to fun_concat is : %s ", s3->string);
    log_trace("S3 size before lpc_call to fun_concat is : %d ", s3->length);

    log_trace("S4 before lpc_call to fun_concat is : %s ", s4->string);
    log_trace("S4 size before lpc_call to fun_concat is : %d ", s4->length);

    code = lpc_call(memory, "fun_concat",
                    STRING, s1, STRING, s2, STRING, s3, INT, &a, DOUBLE, &b, STRING, s4, NOP);

    log_trace("Return code is : %d", code);
    log_trace("Errno variable is : %s", strerror(errno));

    log_trace("S1 after lpc_call to fun_concat is : %s ", s1->string);
    log_trace("S1 size after lpc_call to fun_concat is : %d ", s1->length);

    log_trace("S2 after lpc_call to fun_concat is : %s ", s2->string);
    log_trace("S2 size after lpc_call to fun_concat is : %d ", s2->length);

    log_trace("S3 after lpc_call to fun_concat is : %s ", s3->string);
    log_trace("S3 size after lpc_call to fun_concat is : %d ", s3->length);

    log_trace("S4 after lpc_call to fun_concat is : %s ", s4->string);
    log_trace("S4 size after lpc_call to fun_concat is : %d ", s4->length);

    lpc_close(DEFAULT_SHARED_MEMORY_NAME, memory);

    log_info("Exiting client");

    return 0;
}
