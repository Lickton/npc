#include <iostream>
#include <stdlib.h>
#include <assert.h>
#include <stdint-gcc.h>

// Replace all IR to your own module name
// Visual Studio Code : Press Ctrl + F
// Vi / Vim / Neovim  : :%s/IR/???/g

#include "../obj_dir/VIR.h"  // !!! displace IR
#include "verilated.h"
#include "verilated_vcd_c.h"

VIR *top;
VerilatedContext *contextp;
VerilatedVcdC *tfp;

char Moduel_name[35] = "IR";
uint32_t memory[1024];

extern "C" void pmem_read(uint32_t paddr, uint32_t *rword) {
    uint32_t real_addr = (paddr & 0x7fffffff);
    *rword = memory[real_addr];
}

void init(int argc, char **argv);
void init_mem();
void reset(int n);
void freeup();

void cycles(int n)
{
    for (int i = 0; i < n; i++) {
        contextp->timeInc(1);

        top->clock ^= 1;
        top->io_inst_addr = i;
        top->eval();
        // printf("%d \t: %d\n", contextp->time(), top->clock);
        
        tfp->dump(contextp->time());
    }
}

int main(int argc, char **argv)
{
    init(argc, argv);
    init_mem();

    reset(10);

    cycles(100);

    freeup();

    return 0;
}

void init(int argc, char **argv)
{
    contextp = new VerilatedContext;
    contextp->commandArgs(argc, argv);
    top = new VIR{contextp}; // !!! diplace here

    Verilated::traceEverOn(true);
    tfp = new VerilatedVcdC;
    top->trace(tfp, 99);
    char wave_name[35];
    sprintf(wave_name, "obj_dir/%s.vcd", Moduel_name);
    tfp->open(wave_name);
}

void init_mem()
{
    uint32_t opcode = 0x0010011;
    uint32_t rd     = 1 << 7;
    uint32_t funct3 = 0;
    uint32_t rs1    = 1 << 14;
    for (int i = 1; i < 100; i++) {
        uint32_t imm    = i << 15;
        uint32_t inst   = opcode | rd | funct3 | rs1 | imm;
        memory[i] = inst;
    }
}

void reset(int n)
{
    top->reset = 1;
    for (int i = 0; i < n; i++) {
        contextp->timeInc(1);

        top->clock ^= 1;
        top->eval();

        tfp->dump(contextp->time());
    }
    top->reset = 0;
}

void freeup()
{
    delete top;
    delete contextp;
    tfp->close();
}