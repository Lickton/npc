package npc

import chisel3._
import chisel3.util._
import chisel3.stage.ChiselStage

import config._

class InstFetch extends Module {
    val io = IO(new Bundle {
        /*
         * Program Counter I/O
         *  - imem  : instruction memory IO
         *  - pc    : current instruction address
         *  - inst  : instruction fetch from imem
         **/
        val imem = new RomIO
        val pc   = Output(UInt(Configs.ADDR_WIDTH.W))
        val inst = Output(UInt(Configs.INST_WIDTH.W))
    })

    val pc_en = RegInit(false.B)
    pc_en := true.B

    val pc = RegInit(Configs.START_ADDR.U(32.W))
    pc := pc + Configs.INST_BYTE_WIDTH.U

    io.imem.ren := true.B
    io.imem.raddr := pc

    io.pc := Mux(pc_en, pc, 0.U)
    io.inst := Mux(pc_en, io.imem.rdata, 0.U)
}