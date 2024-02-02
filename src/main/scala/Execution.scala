package npc

import chisel3._
import chisel3.util._
import chisel3.stage.ChiselStage

import config._

class Execution extends Module {
    val io = IO(new Bundle {
        val func = Input(UInt(3.W))
        val in1 = Input(UInt(Configs.DATA_WIDTH.W))
        val in2 = Input(UInt(Configs.DATA_WIDTH.W))
        val out = Output(UInt(Configs.DATA_WIDTH.W))
    })

    val in1 = io.in1
    val in2 = io.in2
    val out = WireInit(UInt(Configs.DATA_WIDTH.W), 0.U)
    val func = io.func

    out := 0.U

    when (func === Func.ADD.asUInt) {
        out := in1 + in2
    }

    io.out := out
}