package npc

import chisel3._
import chisel3.util._
import chisel3.stage.ChiselStage

import config._

class Decode extends Module {
    val io = IO(new Bundle {
        val inst = Input(UInt(Configs.INST_WIDTH.W))
        val rs1_en = Output(Bool())
        val rs2_en = Output(Bool())
        val rs1_addr = Output(UInt(Configs.REG_WIDRH.W))
        val rs2_addr = Output(UInt(Configs.REG_WIDRH.W))
        val rd_en = Output(Bool())
        val rd_addr = Output(UInt(Configs.REG_WIDRH.W))
        val func = Output(UInt(3.W))
        val imm = Output(UInt(Configs.DATA_WIDTH.W))
    })

    val inst = io.inst
    val func = WireInit(UInt(3.W), Func.NOP.asUInt)
    val imm = WireInit(UInt(Configs.DATA_WIDTH.W), 0.U)

    val immI = Cat(Fill(20, inst(31)), inst(31, 20))
    val immS = Cat(Fill(20, inst(31)), inst(31, 25), inst(11, 7))
    val immB = Cat(Fill(20, inst(31)), inst(7), inst(30, 25), inst(11, 8), 0.U(1.W))
    val immU = Cat(inst(31, 12), Fill(12, 0.U))
    val immJ = Cat(Fill(12, inst(31)), inst(31), inst(19, 12), inst(20), inst(30, 21), 0.U(1.W))
    val immSHMT = Cat(Fill(27, 0.U), inst(24, 20))

    io.rs1_addr := inst(19, 15)
    io.rs2_addr := inst(24, 20)
    io.rd_addr  := inst(11, 7)
 
    io.rs1_en := false.B
    io.rs2_en := false.B
    io.rd_en  := false.B

    when (inst === Instructions.addi) {
        io.rs1_en := true.B
        io.rs2_en := false.B
        io.rd_en  := true.B
        imm := immI
        func := Func.ADD.asUInt
    }

    io.func := func
    io.imm := imm
}