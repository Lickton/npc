package ifu

import chisel3._
import chisel3.util._
import chisel3.stage.ChiselStage

import config.Configs._

class PC extends Module {
    val io = IO(new Bundle {
        val curr_addr  = Output(UInt(ADDR_WIDTH.W))
    })

    val counter = RegInit(UInt(ADDR_WIDTH.W), START_ADDR.U)

    when (reset.asBool) {
        counter := START_ADDR.U
    }
    
    counter := counter + INST_BYTE_WIDTH.U
    io.curr_addr := counter
}

object getVerilog extends App {
    new ChiselStage().emitVerilog(new PC(), Array("-td", "vsrc"))
}
