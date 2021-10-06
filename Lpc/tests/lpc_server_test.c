#include "../src/server/lpc_server.h"
#include "../src/utils/utils.h"
#include "unity/unity.h"

void setUp(void) {
    // set stuff up here
}

void tearDown(void) {
    // clean stuff up here
}

void test_LpcCreate_should_CreateValidMemoryFile() {
    void *shmem = lpc_create("dumby_name", 100);
    TEST_ASSERT_NOT_EQUAL_MESSAGE(MAP_FAILED, shmem, "LPC creates a valid memory file");
    munmap(shmem, 100);
}

int main(void) {
    UNITY_BEGIN();
    RUN_TEST(test_LpcCreate_should_CreateValidMemoryFile);
    return UNITY_END();
}
