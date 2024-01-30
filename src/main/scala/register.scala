package register

import chisel3._
import chisel3.util._
import chisel3.stage.ChiselStage

import config.Configs._

class General_Register extends Module {
    val io = IO(new Bundle {
        // from Decoder
        val read = Input(Bool())
        val rs1 = Input(UInt(REGISTER_WIDRH.W))
        val rs2 = Input(UInt(REGISTER_WIDRH.W))

        val write = Input(Bool())
        val rd = Input(UInt(REGISTER_WIDRH.W))
        val data = Input(UInt(DATA_WIDTH.W))

        // to ALU
        val src1 = Output(UInt(DATA_WIDTH.W))
        val src2 = Output(UInt(DATA_WIDTH.W))
    })

    val registers = RegInit(VecInit(Seq.fill(REGISTER_NUM)(0.U(DATA_WIDTH.W))))

    when (io.read) {
        io.src1 := registers(io.rs1)
        io.src2 := registers(io.rs2)
    }.elsewhen (io.write && io.rd =/= 0.U) {
        registers(io.rd) := io.data
        io.src1 := 0.U
        io.src2 := 0.U
    }.otherwise {
        io.src1 := 0.U
        io.src2 := 0.U
    }
}

object getVerilog extends App {
    new ChiselStage().emitVerilog(new General_Register(), Array("-td", "vsrc"))
}
