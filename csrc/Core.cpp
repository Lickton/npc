#include <iostream>
#include <stdlib.h>
#include <assert.h>
#include <stdint-gcc.h>

// Replace all Core to your own module name
// Visual Studio Code : Press Ctrl + F
// Vi / Vim / Neovim  : :%s/Core/???/g

#include "../obj_dir/VCore.h"  // !!! displace Core
#include "verilated.h"
#include "verilated_vcd_c.h"

VCore *top;
VerilatedContext *contextp;
VerilatedVcdC *tfp;

char Moduel_name[35] = "Core";
uint32_t memory[1024];

void init(int argc, char **argv);
void init_mem();
void reset(int n);
void freeup();

void cycles()
{
    while (1) {
        contextp->timeInc(1);

        top->clock ^= 1;
        // printf("en:%d raddr:%u\n", top->io_imem_ren, top->io_imem_raddr);
        uint32_t read_addr = (top->io_imem_raddr & 0x7fffffff) / 4;
        top->io_imem_rdata = memory[read_addr];
        top->eval();
        // printf("%d \t: %d\n", contextp->time(), top->clock);
        tfp->dump(contextp->time());
        if (top->io_break) {
            printf("ebreak simulation end\n");
            break;
        }
    }
}

int main(int argc, char **argv)
{
    init(argc, argv);
    init_mem();

    reset(10);

    cycles();

    freeup();

    return 0;
}

void init(int argc, char **argv)
{
    contextp = new VerilatedContext;
    contextp->commandArgs(argc, argv);
    top = new VCore{contextp}; // !!! diplace here

    Verilated::traceEverOn(true);
    tfp = new VerilatedVcdC;
    top->trace(tfp, 99);
    char wave_name[35];
    sprintf(wave_name, "obj_dir/%s.vcd", Moduel_name);
    tfp->open(wave_name);
}

void init_mem()
{
    int i;
    for (i = 0; i < 1000; i++) {
        memory[i] = 0x00108093;
    }
    memory[i] = 0x00100073;
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