#include <verilated.h>
#include <verilated_vcd_c.h>
#include "VReLU.h"

#include <stdint-gcc.h>
#include <stdio.h>

VReLU* top;
VerilatedContext* contextp;
VerilatedVcdC* tfp;

int8_t buff [16] = {
    123, 85, -64, -17, -39, 77, -95, -61, 65, 0, 0, 0, 0, 0, 0, 0
};

void reset() {
    top->reset = 1;
    for (int i = 0; i < 3; i++) {
        top->clock ^= 1;
        top->eval();
    }
    top->reset = 0;
}

void tick() {
    top->clock ^= 1;
    top->eval();

    contextp->timeInc(1);
    tfp->dump(contextp->time());

    top->clock ^= 1;
    top->eval();

    contextp->timeInc(1);
    tfp->dump(contextp->time());
}

void init(int argc, char **argv) {
    contextp = new VerilatedContext;
    contextp->commandArgs(argc, argv);
    top = new VReLU{contextp};

    Verilated::traceEverOn(true);
    tfp = new VerilatedVcdC;
    top->trace(tfp, 99);
    tfp->open("ReLU.vcd");
    reset();
}

void check() {
    puts("Before ReLU");
    for (int i = 0; i < 10; i++) {
        printf("%3d ", buff[i]);
    }
    printf("\n");
    
    puts("After ReLU");
    for (int i = 0; i < 10; i++) {
        printf("%3d ", buff[i] > 0 ? buff[i] : 0);
    }
    printf("\n");
}


int main(int argc, char **argv) {
    init(argc, argv);

    check();

    top->io_enable = true;
    top->io_in_valid = true;

    top->io_in_bits_0 = buff[0];
    top->io_in_bits_1 = buff[1];
    top->io_in_bits_2 = buff[2];
    top->io_in_bits_3 = buff[3];
    top->io_in_bits_4 = buff[4];
    top->io_in_bits_5 = buff[5];
    top->io_in_bits_6 = buff[6];
    top->io_in_bits_7 = buff[7];
    top->io_in_bits_8 = buff[8];
    top->io_in_bits_9 = buff[9];

    tick();
    tick();

    top->final();
    tfp->close();

    delete top;
    delete contextp;

    return 0;
}