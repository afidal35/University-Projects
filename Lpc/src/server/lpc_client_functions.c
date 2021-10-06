#include "lpc_client_functions.h"

int fun_addition(Data data [], int size) {
    if (size > 3){
        errno = E2BIG;
        return -1;
    }
    if (size < 3) {
        errno = EINVAL;
        return -1;
    }
    for(int i=0;i<size;i++) {
        if(data[i].type != INT) {
            errno = EINVAL;
            return -1;
        }
    }
    int result = data[0].i + data[1].i;
    memmove(&data[2].i, &result, sizeof(int));
    return 0;
}

int fun_hello(Data data [], int size) {
    if (size > 1){
        errno = E2BIG;
        return -1;
    }
    if (size < 1) {
        errno = EINVAL;
        return -1;
    }
    if(data[0].type != STRING) {
        errno = EINVAL;
        return -1;
    }
    char *s = "Hello from server";
    if (data[0].s.length < strlen(s)) {
        int mone = -1;
        memmove(&data[0].s.length, &mone, sizeof(int));
        errno = ENOMEM;
        return -1;
    }
    memmove(&data[0].s.string, s, strlen(s));
    return 0;
}

int fun_read(Data data [], int size) {
    if (size > 2){
        errno = E2BIG;
        return -1;
    }
    if (size < 2) {
        errno = EINVAL;
        return -1;
    }
    if(data[0].type != STRING || data[1].type != STRING) {
        errno = EINVAL;
        return -1;
    }
    int fd = open(data[1].s.string, O_CREAT | O_RDWR, 0666);
    if (fd < 0)
        return -1;
    int length = data[0].s.length;
    char buffer [length];
    if (read(fd, buffer, length) < 0)
        return -1;
    memmove(&data[0].s.string, buffer, length);

    return 0;
}

int fun_replace_concat(Data data [], int size) {
    if (size > 6){
        errno = E2BIG;
        return -1;
    }
    if (size < 6) {
        errno = EINVAL;
        return -1;
    }
    for(int i=0;i<3;i++) {
        if(data[i].type != STRING) {
            errno = EINVAL;
            return -1;
        }
    }
    if (data[3].type != INT || data[4].type != DOUBLE || data[5].type != STRING) {
        errno = EINVAL;
        return -1;
    }
    char *s1 = "Replaced first";
    char *s2 = "Replaced second";
    char *s3 = "Replaced third";
    char *tab[3];
    tab[0] = s1;
    tab[1] = s2;
    tab[2] = s3;
    for (int i=0;i<3;i++) {
        if (data[i].s.length < strlen(tab[i])) {
            int mone = -1;
            memmove(&data[i].s.length, &mone, sizeof(int));
            errno = ENOMEM;
        }
    }

    if(errno == ENOMEM)
        return -1;

    int total_length = strlen(data[0].s.string) + strlen(data[1].s.string) + strlen(data[2].s.string);
    char s_concat[total_length + 40];
    sprintf(s_concat, "%s + %s + %s + %d + %f = 42",
            data[0].s.string, data[1].s.string, data[2].s.string, data[3].i, data[4].d);

    memmove(&data[0].s.string, tab[0], strlen(tab[0]));
    memmove(&data[1].s.string, tab[1], strlen(tab[1]));
    memmove(&data[2].s.string, tab[2], strlen(tab[2]));

    memmove(&data[5].s.string, s_concat, strlen(s_concat));
    return 0;
}