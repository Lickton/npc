package npc

import chisel3._
import chisel3.util._
import chisel3.stage.ChiselStage

import config._

class RegFile extends Module {
    val io = IO(new Bundle {
        val rs1_addr = Input(UInt(Configs.REG_WIDRH.W))
        val rs2_addr = Input(UInt(Configs.REG_WIDRH.W))
        val rs1_data = Output(UInt(Configs.DATA_WIDTH.W))
        val rs2_data = Output(UInt(Configs.DATA_WIDTH.W))
        val rd_addr  = Input(UInt(Configs.REG_WIDRH.W))
        val rd_data  = Input(UInt(Configs.DATA_WIDTH.W))
        val rd_en    = Input(Bool())
    })

    val rf = RegInit(VecInit(Seq.fill(32)(0.U(Configs.DATA_WIDTH.W))))

    when (io.rd_en && (io.rd_addr =/= 0.U)) {
        rf(io.rd_addr) := io.rd_data
    }

    io.rs1_data := Mux((io.rs1_addr === 0.U), 0.U, rf(io.rs1_addr))
    io.rs2_data := Mux((io.rs2_addr === 0.U), 0.U, rf(io.rs2_addr))
}
