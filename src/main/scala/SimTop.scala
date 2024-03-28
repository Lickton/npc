import chisel3._
import chisel3.util._

import npc._

import device.memory.AXI_2r1w
import device.accelerator.CNNCore
import interface._
import utils.XBar

class SimTop extends Module {
    val io = IO(new Bundle {
        val pc    = Output(UInt(32.W))
        val inst  = Output(UInt(32.W))
        val break = Output(Bool())
    })

    val core = Module(new Core(32))
    val cnn_core = Module(new CNNCore(32))
    io.break := core.io.break

    // val mem = Module(new Ram_2r1w(32))
    val mem = Module(new AXI_2r1w(32))
    val xbar = Module(new XBar(32, 2))
    
    // mem.io.imem <> core.io.imem
    // mem.io.dmem <> core.io.dmem
    AXI4Lite.conncet(core.io.imem, mem.io.imem)
    // AXI4Lite.conncet(core.io.dmem, mem.io.dmem)
    AXI4Lite.conncet(core.io.dmem, xbar.in)
    AXI4Lite.conncet(xbar.out(0), mem.io.dmem)
    AXI4Lite.conncet(xbar.out(1), cnn_core.io)

    io.pc := mem.io.imem.read.araddr
    io.inst := mem.io.imem.read.rdata
}