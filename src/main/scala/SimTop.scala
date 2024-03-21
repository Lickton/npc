import chisel3._
import chisel3.util._

import npc._
import memory._

class SimTop extends Module {
    val io = IO(new Bundle {
        val pc    = Output(UInt(32.W))
        val inst  = Output(UInt(32.W))
        val break = Output(Bool())
    })

    val core = Module(new Core(32))
    io.break := core.io.break

    val mem = Module(new Ram_2r1w(32))
    mem.io.imem <> core.io.imem
    mem.io.dmem <> core.io.dmem

    io.pc := mem.io.imem.raddr
    io.inst := mem.io.imem.rdata
}