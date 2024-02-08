package npc

import chisel3._
import chisel3.util._
import chisel3.experimental._
import chisel3.util.HasBlackBoxResource

import config.Configs._

class RomIO extends Bundle {
    /*
     * Instruction ROM I/O signals
     *  - ren   : read enable
     *  - raddr : read address
     *  - rdata : read data from simulation environment
     **/
    val ren = Output(Bool())
    val raddr = Output(UInt(ADDR_WIDTH.W))
    val rdata = Input(UInt(DATA_WIDTH.W))
}

class RamIO extends RomIO {
    /*
     * Data RAM I/O signals - Extends from RomIO
     *  - wen   : read enable
     *  - waddr : read address
     *  - wmask : write which bytes
     *  - wdata : read data from simulation environment
     **/
    val wen   = Output(Bool())
    val waddr = Output(UInt(ADDR_WIDTH.W))
    val wdata = Output(UInt(DATA_WIDTH.W))
    val wmask = Output(UInt(DATA_WIDTH.W))
}

class ram_2r1w extends BlackBox with HasBlackBoxResource {
    val io = IO(new Bundle {
        val clock      = Input(Clock())
        
        val imem_ren   = Input(Bool())
        val imem_raddr = Input(UInt(ADDR_WIDTH.W))
        val imem_rdata = Output(UInt(DATA_WIDTH.W))
        // val dmem_ren   = Input(Bool())
        // val dmem_raddr = Input(UInt(ADDR_WIDTH.W))
        // val dmem_rdata = Output(UInt(DATA_WIDTH.W))
        // val dmem_wen   = Input(Bool())
        // val dmem_waddr = Input(UInt(ADDR_WIDTH.W))
        // val dmem_wmask = Input(UInt(DATA_WIDTH.W))
        // val dmem_wdata = Input(UInt(DATA_WIDTH.W))
    })

    addResource("ram_2r1w.v")
}

class Ram_2r1w extends Module {
    val io = IO(new Bundle {
        val imem = Flipped(new RomIO)
        // val dmem = Flipped(new RamIO)
    })
    
    val ram = Module(new ram_2r1w)

    ram.io.clock := clock

    ram.io.imem_ren := io.imem.ren
    ram.io.imem_raddr := io.imem.raddr
    io.imem.rdata := ram.io.imem_rdata

    // ram.io.dmem_ren := io.dmem.ren
    // ram.io.dmem_raddr := io.dmem.raddr
    // io.dmem.rdata := ram.io.dmem_rdata

    // ram.io.dmem_wen := io.dmem.wen
    // ram.io.dmem_waddr := io.dmem.waddr
    // ram.io.dmem_wmask := io.dmem.wmask
    // ram.io.dmem_wdata := io.dmem.wdata
}