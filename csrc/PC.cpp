#include <iostream>
#include <stdlib.h>
#include <assert.h>

#include "../obj_dir/VPC.h"  // !!!change {MODULE_NAME}
#include "verilated.h"
#include "verilated_vcd_c.h"

VPC *top;
VerilatedContext *contextp;
VerilatedVcdC *tfp;

char Moduel_name[35] = "PC";

void init(int argc, char **argv);
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
    init(argc, argv);

    reset(10);

    cycles(10);

    reset(10);

    cycles(10);

    freeup();

    return 0;
}

void init(int argc, char **argv)
{
    contextp = new VerilatedContext;
    contextp->commandArgs(argc, argv);
    top = new VPC{contextp};

    Verilated::traceEverOn(true);
    tfp = new VerilatedVcdC;
    top->trace(tfp, 99);
    char wave_name[35];
    sprintf(wave_name, "obj_dir/%s.vcd", Moduel_name);
    printf("\n\n\n%s\n\n\n", wave_name);
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