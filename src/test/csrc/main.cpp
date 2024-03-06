#include <common.h>

void sdb_mainloop();
void init_monitor(int, char**);
void init_verilator(int, char**);
int is_exit_status_bad();
void end_verilator();

int main(int argc, char **argv) {
    init_monitor(argc, argv);
    init_verilator(argc, argv);
    sdb_mainloop();
    end_verilator();
    return is_exit_status_bad();
}