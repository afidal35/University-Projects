#include "../src/client/lpc_client.h"
#include "unity/unity.h"
#include <stdlib.h>

char *mem;
ssize_t size;
char *name = "Test";

void setUp(void) {
    // set stuff up here
    int fd = shm_open("testmem", O_RDWR | O_CREAT, S_IRUSR | S_IWUSR);
    if (fd < 0) {
        perror("shm open");
        exit(1);
    }
    size = 4 * _SC_PAGESIZE;
    ftruncate(fd, size);
    mem = mmap(NULL, size, PROT_READ | PROT_WRITE, MAP_SHARED, fd, 0);

    strcpy(mem, name);
}

void tearDown(void) {
    // clean stuff up here
}

void test_client_open_shmem() {
    char *another_mem = (char *) lpc_open("testmem");
    TEST_ASSERT_EQUAL_MESSAGE(*another_mem, *name, "Shared memory should contain same value");
}

void test_client_close_shmem() {
    int ret = lpc_close("testmem", mem);
    TEST_ASSERT_EQUAL_MESSAGE(ret, 0, "Shared memory should be closed");
}

void test_client_lpc_string_creates_given_value() {
    char *str = "My Text\n";
    lpc_string *lpcstr = lpc_make_string(str, strlen(str));
    TEST_ASSERT_EQUAL_MESSAGE(*str, *lpcstr->string, "String values");
    TEST_ASSERT_EQUAL_MESSAGE(strlen(str), lpcstr->length, "String size");
}

void test_client_lpc_string_creates_empty_value() {
    lpc_string *lpcstr = lpc_make_string(NULL, 10);
    TEST_ASSERT_EQUAL( 10, lpcstr->length);
    char *str = "my";
    strcpy(lpcstr->string, str);
    TEST_ASSERT_EQUAL( strlen(str), strlen(lpcstr->string));
}

void test_client_lpc_string_creates_bigger_value() {
    char *str = "My Text\n";
    lpc_string *lpcstr = lpc_make_string(str, strlen(str) + 10);
    TEST_ASSERT_EQUAL( strlen(str) + 10, lpcstr->length);
    TEST_ASSERT_EQUAL('\0', lpcstr->string[-1]);
    TEST_ASSERT_EQUAL( strlen(str), strlen(lpcstr->string));
}

int main(void) {
    UNITY_BEGIN();
    RUN_TEST(test_client_open_shmem);
    RUN_TEST(test_client_close_shmem);
    RUN_TEST(test_client_lpc_string_creates_given_value);
    RUN_TEST(test_client_lpc_string_creates_empty_value);
    RUN_TEST(test_client_lpc_string_creates_bigger_value);
    return UNITY_END();
}
