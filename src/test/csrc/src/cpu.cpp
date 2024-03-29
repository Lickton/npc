#include <cpu.h>
#include <reg.h>
#include <utils.h>

#include <VSimTop___024root.h>

#define MAX_INST_TO_PRINT 10

VSimTop* top;
VerilatedContext* contextp;
#ifdef VCD_TRACE
VerilatedVcdC* tfp;
#endif

static bool print_step = false;
static uint64_t timer = 0; // us
uint64_t run_guest_inst = 0;

void reset() {
    top->reset = 1;
    for (int i = 0; i < 3; i++) {
        top->clock ^= 1;
        top->eval();
    }
    top->reset = 0;
}

void init_verilator(int argc, char **argv) {
    contextp = new VerilatedContext;
    contextp->commandArgs(argc, argv);
    top = new VSimTop{contextp};
#ifdef VCD_TRACE
    Verilated::traceEverOn(true);
    tfp = new VerilatedVcdC;
    top->trace(tfp, 99);
    tfp->open("SimTop.vcd");
#endif
    reset();
}

static void statistic() {
    Log("host time spent = %llu us", timer);
    Log("total guest instructions = %llu", run_guest_inst);
    if (timer > 0) Log("simulation frequency = %llu inst/s", run_guest_inst * 1000000 / timer);
    else Log("Finish running in less than 1 us and can not calculate the simulation frequency");
}

void exec_once() {
    // 0 -> 1
    top->clock ^= 1;
    top->eval();

#ifdef VCD_TRACE
    contextp->timeInc(1);
    tfp->dump(contextp->time());
#endif
#ifdef INST_TRACE
    if (print_step) {
        printf("PC %08x %08x\n", top->io_pc, top->io_inst);
    }
#endif

    if (top->io_break) {
        npc_state.halt_ret = top->rootp->SimTop__DOT__core__DOT__rf__DOT__rf_10;
        npc_state.state = NPC_END;
        npc_state.halt_pc = top->io_pc;
        // printf("GPR 1 = %08x\n", gpr(1));
        // reg_display();
        return;
    }

    // 1 -> 0
    top->clock ^= 1;
    top->eval();

#ifdef VCD_TRACE
    contextp->timeInc(1);
    tfp->dump(contextp->time());
#endif
}

static void execute(uint64_t n) {
    for (; n > 0; n--) {
        run_guest_inst++;
        exec_once();
        if (npc_state.state != NPC_RUNNING) return;
    }
}

void cpu_exec(uint64_t n) {
    print_step = (n <= MAX_INST_TO_PRINT);
    uint64_t timer_start = get_time();

    execute(n);

    uint64_t timer_end = get_time();
    timer += timer_end - timer_start;

    if (npc_state.state == NPC_END) {
        if (npc_state.halt_ret == 0) {
            Log("NPC: %s at pc = %08x", ANSI_FMT("HIT GOOD TRAP", ANSI_FG_GREEN), npc_state.halt_pc);
        } else {
            Log("NPC: %s at pc = %08x", ANSI_FMT("HIT BAD TRAP", ANSI_FG_RED), npc_state.halt_pc);
        }
        statistic();
    }
}

void end_verilator() {
    delete top;
    delete contextp;

#ifdef VCD_TRACE
    tfp->close();
#endif
}