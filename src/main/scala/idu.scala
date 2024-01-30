package idu

import chisel3._
import chisel3.util._
import chisel3.stage.ChiselStage

import config.Configs._

class Decoder extends Module {
    val io = IO(new Bundle {
        // from IR
        val inst = Input(UInt(INST_WIDTH.W))

        // to ALU
        val imm  = Output(UInt(DATA_WIDTH.W))

        // to General Registers
        val rs1 = Output(UInt(REGISTER_WIDRH.W))
        val rs2 = Output(UInt(REGISTER_WIDRH.W))
        val rd  = Output(UInt(REGISTER_WIDRH.W))
    })

    val immI = Cat(Fill(20, io.inst(31)), io.inst(31, 20))
    val immS = Cat(Fill(20, io.inst(31)), io.inst(31, 25), io.inst(11, 7))
    val immB = Cat(Fill(20, io.inst(31)), io.inst(7), io.inst(30, 25), io.inst(11, 8), 0.U(1.W))
    val immU = Cat(io.inst(31, 12), Fill(12, 0.U))
    val immJ = Cat(Fill(12, io.inst(31)), io.inst(31), io.inst(19, 12), io.inst(20), io.inst(30, 21), 0.U(1.W))
    val immSHMT = Cat(Fill(27, 0.U), io.inst(24, 20))

    
}