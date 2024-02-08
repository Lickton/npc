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
uint32_t inst_memory[6000];
uint32_t data_memory[6000];

extern "C" int  read_imem(uint32_t addr) {
    uint32_t _addr = (addr & 0x7fffffff) >> 2;
    return inst_memory[_addr];
}

extern "C" int  read_dmem(uint32_t addr) {
    uint32_t _addr = (addr & 0x7fffffff) >> 2;
    return data_memory[_addr];
}

extern "C" void write_dmem(uint32_t addr, uint32_t wmask, uint32_t wdata) {
    uint32_t _addr = (addr & 0x7fffffff) >> 2;
    data_memory[_addr] = wdata & wmask;
}


void init(int argc, char **argv);
long load_img(char *filename);
void reset(int n);
void freeup();

void cycles()
{
    while (1) {
        contextp->timeInc(1);

        top->clock ^= 1;
        top->eval();
        // printf("%d \t: %d\n", contextp->time(), top->clock);
        tfp->dump(contextp->time());
        if (top->io_break) {
            printf("\n\033[32m===ebreak simulation end===\033[0m\n\n");
            break;
        }
    }
}

int main(int argc, char **argv)
{
    init(argc, argv);
    if (argc != 2) {
        printf("%s:%d> arg with file name!\n", __FILE__, __LINE__);
        return 0;
    }
    // printf("argv[1] = %s\n", argv[1]);
    int size = load_img(argv[1]);
    if (size > 4096) {
        printf("File too big\n");
        return 0;
    }

    reset(5);

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

long load_img(char *img_file)
{
    FILE *fp = fopen(img_file, "rb");
    fseek(fp, 0, SEEK_END);
    long size = ftell(fp);

    printf("%s:%d> Image %s, size = %ld\n", __FILE__, __LINE__, img_file, size);

    fseek(fp, 0, SEEK_SET);
    fread(inst_memory, size, 1, fp);
    fclose(fp);

    return size;
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