package ifu

import chisel3._
import chisel3.util._
// import chisel3.util.HasBlackBoxResource
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

class ifetch extends BlackBox with HasBlackBoxResource {
    val io = IO(new Bundle {
        val addr = Input(UInt(ADDR_WIDTH.W))
        val inst = Output(UInt(INST_WIDTH.W))
    })

    addResource("/ifetch.v")
}

class IR extends Module {
    val io = IO(new Bundle {
        val inst_addr   = Input(UInt(ADDR_WIDTH.W))
        val instruction = Output(UInt(INST_WIDTH.W))
    })

    val inst = Reg(UInt(INST_WIDTH.W))
    val fetcher = Module(new ifetch())
    fetcher.io.addr := io.inst_addr

    inst := fetcher.io.inst
    io.instruction := inst
}

object getVerilog extends App {
    new ChiselStage().emitVerilog(new PC(), Array("-td", "vsrc"))
    new ChiselStage().emitVerilog(new IR(), Array("-td", "vsrc"))
}
