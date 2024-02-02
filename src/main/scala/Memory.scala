package npc

import chisel3._
import chisel3.util._

import config.Configs._

class RomIO extends Bundle {
    /*
     * Read Only I/O signals
     *  - ren   : read enable
     *  - raddr : read address
     *  - rdata : read data from simulation environment
     **/
    val ren = Output(Bool())
    val raddr = Output(UInt(ADDR_WIDTH.W))
    val rdata = Input(UInt(DATA_WIDTH.W))
}