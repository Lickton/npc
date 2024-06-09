#include <verilated.h>
#include <verilated_vcd_c.h>
#include "VConv.h"

#include <stdint-gcc.h>
#include <stdio.h>

VConv* top;
VerilatedContext* contextp;
VerilatedVcdC* tfp;

uint8_t buff [11][9] = {
    {  2,  5,  3,  6,  4,  8,  6,  7,  6},
    {235, 64,  6, 41, 52,200,153, 95,133},
    { 64,  6,  6, 52,200, 15, 95,133, 84},
    {  6,  6, 62,200, 15,177,133, 84, 85},
    { 41, 52,200,153, 95,133, 18, 65,206},
    { 52,200, 15, 95,133, 84, 65,206, 97},
    {200, 15,177,133, 84, 85,206, 97, 77},
    {153, 95,133, 18, 65,206, 98,191,112},
    { 95,133, 84, 65,206, 97,191,112, 48},
    {133, 84, 85,206, 97, 77,112, 48, 38},
    {  0,  0,  0,  0,  0,  0,  0,  0,  0}
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
    top = new VConv{contextp};

    Verilated::traceEverOn(true);
    tfp = new VerilatedVcdC;
    top->trace(tfp, 99);
    tfp->open("Conv.vcd");
    reset();
}

void check() {
    for (int i = 1; i < 11; i++) {
        int8_t res = 0;
        for (int j = 0; j < 9; j++) {
            res += (int8_t)buff[i][j] * (int8_t)buff[0][j];
        }
        printf("%d ", res);
    }
    printf("\n");
}

int main(int argc, char **argv) {
    init(argc, argv);

    check();

    top->io_kern_valid = true;
    top->io_kern_bits_0 = buff[0][0];
    top->io_kern_bits_1 = buff[0][1];
    top->io_kern_bits_2 = buff[0][2];
    top->io_kern_bits_3 = buff[0][3];
    top->io_kern_bits_4 = buff[0][4];
    top->io_kern_bits_5 = buff[0][5];
    top->io_kern_bits_6 = buff[0][6];
    top->io_kern_bits_7 = buff[0][7];
    top->io_kern_bits_8 = buff[0][8];
    tick();

    for (int i = 1; i <= 5; i++) {
        top->io_in_0_valid = true;
        int a = (i-1) * 2;
        int b = (i-1) * 2 + 1;
        top->io_idx_0 = a;
        top->io_idx_1 = b;
        /* kernel size: 3*3 */
        /* 0 2 4 6 8 10 12 14 */
        top->io_in_0_bits_0 = buff[i*2 - 1][0];
        top->io_in_0_bits_1 = buff[i*2 - 1][1];
        top->io_in_0_bits_2 = buff[i*2 - 1][2];
        top->io_in_0_bits_3 = buff[i*2 - 1][3];
        top->io_in_0_bits_4 = buff[i*2 - 1][4];
        top->io_in_0_bits_5 = buff[i*2 - 1][5];
        top->io_in_0_bits_6 = buff[i*2 - 1][6];
        top->io_in_0_bits_7 = buff[i*2 - 1][7];
        top->io_in_0_bits_8 = buff[i*2 - 1][8];

        /* 1 3 5 7 9 11 13 15 */
        top->io_in_1_bits_0 = buff[i * 2][0];
        top->io_in_1_bits_1 = buff[i * 2][1];
        top->io_in_1_bits_2 = buff[i * 2][2];
        top->io_in_1_bits_3 = buff[i * 2][3];
        top->io_in_1_bits_4 = buff[i * 2][4];
        top->io_in_1_bits_5 = buff[i * 2][5];
        top->io_in_1_bits_6 = buff[i * 2][6];
        top->io_in_1_bits_7 = buff[i * 2][7];
        top->io_in_1_bits_8 = buff[i * 2][8];
        tick();
    }

    top->io_en_0 = true;
    top->io_en_1 = true;

    tick();
    tick();

    top->final();
    tfp->close();

    delete top;
    delete contextp;

    return 0;
}