package device.accelerator

import chisel3._
import chisel3.util._

object Max {
  def max4(a: SInt, b: SInt, c: SInt, d: SInt): SInt = {
    Mux(a >= b && a >= c && a >= d, a,
      Mux(b >= c && b >= d, b,
        Mux(c >= d, c, d)))
  }
}

class Pooling(xlen: Int, size: Int) extends Module {
    val io = IO(new Bundle {
        val enable = Input(Bool())
        val choice = Input(UInt(1.W))
        val bits = Input(Vec(size, SInt(xlen.W)))
        val output_size = Output(UInt(4.W))
        val outBits = Output(Vec(size, SInt(xlen.W)))
    })

    io.output_size := Mux(io.enable, (size / 4).U, size.U)
    io.outBits := io.bits

    when (io.choice === 0.U) {
        for (i <- 0 until 4) {
            io.outBits(i) := Max.max4(io.bits(4*i), io.bits((4*i)+1), io.bits((4*i)+2), io.bits((4*i)+3))
        }
    } .elsewhen(io.choice === 1.U) {
        for (i <- 0 until 4) {
            io.outBits(i) := ((io.bits(4*i) + io.bits((4*i)+1) + io.bits((4*i)+2) + io.bits((4*i)+3)).asUInt >> 2.U).asSInt
        }
    }
}