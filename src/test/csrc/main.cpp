#include <common.h>

void sdb_mainloop();
void init_monitor(int, char**);
void init_verilator(int, char**);
int is_exit_status_bad();

int main(int argc, char **argv) {
    init_monitor(argc, argv);
    init_verilator(argc, argv);
    sdb_mainloop();
    return is_exit_status_bad();
}