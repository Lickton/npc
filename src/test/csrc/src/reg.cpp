#include <reg.h>

const char *regs[] = {
    "$0", "ra", "sp", "gp", "tp", "t0", "t1", "t2",
    "s0", "s1", "a0", "a1", "a2", "a3", "a4", "a5",
    "a6", "a7", "s2", "s3", "s4", "s5", "s6", "s7",
    "s8", "s9", "s10", "s11", "t3", "t4", "t5", "t6"
};

extern VSimTop* top;

static int gpr(int x) {
    assert(x >= 0 && x <= 31);
    int reg[32] = {0};
    reg[0 ] = *(&top->rootp->SimTop__DOT__core__DOT__rf__DOT__rf_0 );
    reg[1 ] = *(&top->rootp->SimTop__DOT__core__DOT__rf__DOT__rf_1 );
    reg[2 ] = *(&top->rootp->SimTop__DOT__core__DOT__rf__DOT__rf_2 );
    reg[3 ] = *(&top->rootp->SimTop__DOT__core__DOT__rf__DOT__rf_3 );
    reg[4 ] = *(&top->rootp->SimTop__DOT__core__DOT__rf__DOT__rf_4 );
    reg[5 ] = *(&top->rootp->SimTop__DOT__core__DOT__rf__DOT__rf_5 );
    reg[6 ] = *(&top->rootp->SimTop__DOT__core__DOT__rf__DOT__rf_6 );
    reg[7 ] = *(&top->rootp->SimTop__DOT__core__DOT__rf__DOT__rf_7 );
    reg[8 ] = *(&top->rootp->SimTop__DOT__core__DOT__rf__DOT__rf_8 );
    reg[9 ] = *(&top->rootp->SimTop__DOT__core__DOT__rf__DOT__rf_9 );
    reg[10] = *(&top->rootp->SimTop__DOT__core__DOT__rf__DOT__rf_10);
    reg[11] = *(&top->rootp->SimTop__DOT__core__DOT__rf__DOT__rf_11);
    reg[12] = *(&top->rootp->SimTop__DOT__core__DOT__rf__DOT__rf_12);
    reg[13] = *(&top->rootp->SimTop__DOT__core__DOT__rf__DOT__rf_13);
    reg[14] = *(&top->rootp->SimTop__DOT__core__DOT__rf__DOT__rf_14);
    reg[15] = *(&top->rootp->SimTop__DOT__core__DOT__rf__DOT__rf_15);
    reg[16] = *(&top->rootp->SimTop__DOT__core__DOT__rf__DOT__rf_16);
    reg[17] = *(&top->rootp->SimTop__DOT__core__DOT__rf__DOT__rf_17);
    reg[18] = *(&top->rootp->SimTop__DOT__core__DOT__rf__DOT__rf_18);
    reg[19] = *(&top->rootp->SimTop__DOT__core__DOT__rf__DOT__rf_19);
    reg[20] = *(&top->rootp->SimTop__DOT__core__DOT__rf__DOT__rf_20);
    reg[21] = *(&top->rootp->SimTop__DOT__core__DOT__rf__DOT__rf_21);
    reg[22] = *(&top->rootp->SimTop__DOT__core__DOT__rf__DOT__rf_22);
    reg[23] = *(&top->rootp->SimTop__DOT__core__DOT__rf__DOT__rf_23);
    reg[24] = *(&top->rootp->SimTop__DOT__core__DOT__rf__DOT__rf_24);
    reg[25] = *(&top->rootp->SimTop__DOT__core__DOT__rf__DOT__rf_25);
    reg[26] = *(&top->rootp->SimTop__DOT__core__DOT__rf__DOT__rf_26);
    reg[27] = *(&top->rootp->SimTop__DOT__core__DOT__rf__DOT__rf_27);
    reg[28] = *(&top->rootp->SimTop__DOT__core__DOT__rf__DOT__rf_28);
    reg[29] = *(&top->rootp->SimTop__DOT__core__DOT__rf__DOT__rf_29);
    reg[30] = *(&top->rootp->SimTop__DOT__core__DOT__rf__DOT__rf_30);
    reg[31] = *(&top->rootp->SimTop__DOT__core__DOT__rf__DOT__rf_31);
    return reg[x];
}

void reg_display() {
    for (int i = 0; i < 32; i += 4) {
        printf("%s %08x  ", regs[i], gpr(i));
        printf("%s %08x  ", regs[i+1], gpr(i+1));
        printf("%s %08x  ", regs[i+2], gpr(i+2));
        printf("%s %08x\n", regs[i+3], gpr(i+3));
    }
}