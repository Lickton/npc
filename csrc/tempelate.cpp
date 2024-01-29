#include <iostream>
#include <stdlib.h>
#include <assert.h>

// Replace all {MODULE_NAME} to your own module name
// Visual Studio Code : Press Ctrl + F
// Vi / Vim / Neovim  : :%s/{MODULE_NAME}/???/g

#include "../obj_dir/V{MODULE_NAME}.h"  // !!! displace {MODULE_NAME}
#include "verilated.h"
#include "verilated_vcd_c.h"

V{MODULE_NAME} *top;
VerilatedContext *contextp;
VerilatedVcdC *tfp;

char Moduel_name[35] = "{MODULE_NAME}";

void init();
void reset(int n);
void freeup();

void cycles(int n)
{
    for (int i = 0; i < n; i++) {
        contextp->timeInc(1);

        top->clock ^= 1;
        top->eval();
        // printf("%d \t: %d\n", contextp->time(), top->clock);
        
        tfp->dump(contextp->time());
    }
}

int main(int argc, char **argv)
{
    init();

    reset(10);

    cycles(1000);

    freeup();

    return 0;
}

void init()
{
    contextp = new VerilatedContext;
    contextp->commandArgs(argc, argv);
    top = new V{MODULE_NAME}{contextp}; // !!! diplace here

    Verilated::traceEverOn(true);
    tfp = new VerilatedVcdC;
    top->trace(tfp, 99);
    char wave_name[35];
    sprintf(wave_name, "obj_dir/%s.vcd", Moduel_name);
    tfp->open(wave_name);
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