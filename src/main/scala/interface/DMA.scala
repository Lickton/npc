package interface

import chisel3._
import chisel3.util._

class DMARead(xlen: Int, size: Int) extends Bundle {
    val ar = Decoupled(UInt(32.W))
    val rd = Flipped(Decoupled(UInt(xlen.W)))
}

class DMAWrite (xlen: Int, size: Int) extends Bundle {
    val aw = Decoupled(UInt(32.W))
    val wd = Decoupled(UInt(xlen.W))
}

class DMA(xlen: Int, size: Int) extends Bundle {
    val ar = Decoupled(UInt(32.W))
    val rd = Flipped(Decoupled(UInt(xlen.W)))
    val aw = Decoupled(UInt(32.W))
    val wd = Decoupled(UInt(xlen.W))
}