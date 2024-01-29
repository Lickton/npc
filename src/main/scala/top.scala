package npc

import chisel3._
import chisel3.util._
import chisel3.stage.ChiselStage

import config.Configs._

class top extends Module {
    val io = IO(new Bundle {
        val out = Output(UInt(ADDR_WIDTH.W))
    })

    val pc = Module(new ifu.PC)

    io.out := pc.io.curr_addr
}

object getVerilog extends App {
    new ChiselStage().emitVerilog(new top(), Array("-td", "vsrc"))
}
