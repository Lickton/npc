package npc

import chisel3._
import chisel3.util._
import chisel3.stage.ChiselStage

class passThrough extends Module {
    val io = IO(new Bundle {
        val in = Input(UInt(4.W))
        val out = Output(UInt(4.W))
    })

    io.out := io.in
}