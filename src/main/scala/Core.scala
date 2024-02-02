package npc

import chisel3._
import chisel3.util._
import chisel3.stage.ChiselStage

import config._

class Core extends Module {
    val io = IO(new Bundle {
        val imem = new RomIO
        val out = Output(UInt(Configs.DATA_WIDTH.W))
        val break = Output(Bool())
    })

    val fetch = Module(new InstFetch)
    fetch.io.imem <> io.imem

    val decode = Module(new Decode)
    decode.io.inst := fetch.io.inst
    io.break := decode.io.break

    val rf = Module(new RegFile)
    rf.io.rs1_addr := decode.io.rs1_addr
    rf.io.rs2_addr := decode.io.rs2_addr
    rf.io.rd_addr := decode.io.rd_addr
    rf.io.rd_en := decode.io.rd_en

    val execution = Module(new Execution)
    execution.io.func := decode.io.func
    execution.io.in1 := Mux(decode.io.rs1_en, rf.io.rs1_data, 0.U)
    execution.io.in2 := Mux(decode.io.rs2_en, rf.io.rs2_data, decode.io.imm)
    rf.io.rd_data := execution.io.out

    io.out := execution.io.out
}