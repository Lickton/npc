#include <reg.h>

const char *regs[] = {
    "$0", "ra", "sp", "gp", "tp", "t0", "t1", "t2",
    "s0", "s1", "a0", "a1", "a2", "a3", "a4", "a5",
    "a6", "a7", "s2", "s3", "s4", "s5", "s6", "s7",
    "s8", "s9", "s10", "s11", "t3", "t4", "t5", "t6"
};



VSimTop* top;

void reg_display() {
    printf("PC %08x  ", top->io_pc);
    printf("%s %08x  ", regs[1], top->rootp->SimTop__DOT__core__DOT__rf__DOT__rf_1);
    printf("%s %08x  ", regs[2], top->rootp->SimTop__DOT__core__DOT__rf__DOT__rf_2);
    printf("%s %08x\n", regs[3], top->rootp->SimTop__DOT__core__DOT__rf__DOT__rf_3);
    printf("%s %08x  ", regs[4], top->rootp->SimTop__DOT__core__DOT__rf__DOT__rf_4);
    printf("%s %08x  ", regs[5], top->rootp->SimTop__DOT__core__DOT__rf__DOT__rf_5);
    printf("%s %08x  ", regs[6], top->rootp->SimTop__DOT__core__DOT__rf__DOT__rf_6);
    printf("%s %08x\n", regs[7], top->rootp->SimTop__DOT__core__DOT__rf__DOT__rf_7);
    printf("%s %08x  ", regs[8], top->rootp->SimTop__DOT__core__DOT__rf__DOT__rf_8);
    printf("%s %08x  ", regs[9], top->rootp->SimTop__DOT__core__DOT__rf__DOT__rf_9);
    printf("%s %08x  ", regs[10], top->rootp->SimTop__DOT__core__DOT__rf__DOT__rf_6);
    printf("%s %08x\n", regs[11], top->rootp->SimTop__DOT__core__DOT__rf__DOT__rf_7);
    printf("%s %08x  ", regs[12], top->rootp->SimTop__DOT__core__DOT__rf__DOT__rf_4);
    printf("%s %08x  ", regs[13], top->rootp->SimTop__DOT__core__DOT__rf__DOT__rf_5);
    printf("%s %08x  ", regs[14], top->rootp->SimTop__DOT__core__DOT__rf__DOT__rf_6);
    printf("%s %08x\n", regs[15], top->rootp->SimTop__DOT__core__DOT__rf__DOT__rf_7);
    printf("%s %08x  ", regs[16], top->rootp->SimTop__DOT__core__DOT__rf__DOT__rf_4);
    printf("%s %08x  ", regs[17], top->rootp->SimTop__DOT__core__DOT__rf__DOT__rf_5);
    printf("%s %08x  ", regs[18], top->rootp->SimTop__DOT__core__DOT__rf__DOT__rf_6);
    printf("%s %08x\n", regs[19], top->rootp->SimTop__DOT__core__DOT__rf__DOT__rf_7);
    printf("%s %08x  ", regs[20], top->rootp->SimTop__DOT__core__DOT__rf__DOT__rf_4);
    printf("%s %08x  ", regs[21], top->rootp->SimTop__DOT__core__DOT__rf__DOT__rf_5);
    printf("%s %08x  ", regs[22], top->rootp->SimTop__DOT__core__DOT__rf__DOT__rf_6);
    printf("%s %08x\n", regs[23], top->rootp->SimTop__DOT__core__DOT__rf__DOT__rf_7);
    printf("%s %08x  ", regs[24], top->rootp->SimTop__DOT__core__DOT__rf__DOT__rf_4);
    printf("%s %08x  ", regs[25], top->rootp->SimTop__DOT__core__DOT__rf__DOT__rf_5);
    printf("%s %08x  ", regs[26], top->rootp->SimTop__DOT__core__DOT__rf__DOT__rf_6);
    printf("%s %08x\n", regs[27], top->rootp->SimTop__DOT__core__DOT__rf__DOT__rf_7);
    printf("%s %08x  ", regs[28], top->rootp->SimTop__DOT__core__DOT__rf__DOT__rf_4);
    printf("%s %08x  ", regs[29], top->rootp->SimTop__DOT__core__DOT__rf__DOT__rf_5);
    printf("%s %08x  ", regs[30], top->rootp->SimTop__DOT__core__DOT__rf__DOT__rf_6);
    printf("%s %08x\n", regs[31], top->rootp->SimTop__DOT__core__DOT__rf__DOT__rf_7);
}
