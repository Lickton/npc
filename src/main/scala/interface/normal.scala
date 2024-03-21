package interface

import chisel3._
class RomIO (width : Int) extends Bundle {
    val ren = Output(Bool())
    val raddr = Output(UInt(width.W))
    val rdata = Input(UInt(width.W))
}

class RamIO (width : Int) extends RomIO (width) {
    val wen   = Output(Bool())
    val waddr = Output(UInt(width.W))
    val wdata = Output(UInt(width.W))
    val wmask = Output(UInt(width.W))
}

