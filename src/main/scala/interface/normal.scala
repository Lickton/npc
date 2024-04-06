package interface

import chisel3._
class RomIO[T <: Data](gen: T, width: Int) extends Bundle {
    val ren = Output(Bool())
    val raddr = Output(UInt(width.W))
    val rdata = Input(gen)
}

class RamIO[T <: Data](gen: T, width: Int) extends RomIO(gen, width) {
    val wen   = Output(Bool())
    val waddr = Output(UInt(width.W))
    // val wdata = Output(UInt(width.W))
    val wdata = Output(gen)
    val wstrb = Output(UInt(4.W))
}
