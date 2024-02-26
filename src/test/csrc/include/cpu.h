#ifndef __CPU_H__
#define __CPU_H__

#include <common.h>
#include <VSimTop.h>
#include <verilated.h>
#include <verilated_vcd_c.h>

void init_verilator(int argc, char **argv);
void cpu_exec(uint64_t n);

#endif