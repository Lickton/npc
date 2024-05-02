package device.accelerator

import chisel3._
import chisel3.util._

class ReLU(xlen: Int, size: Int) extends Module {
    val io = IO(new Bundle {
        val enable = Input(Bool())
        val in = Flipped(Decoupled(Vec(size, SInt(xlen.W))))
        val out = Decoupled(Vec(size, SInt(xlen.W)))
    })

    io.in.ready := false.B
    io.out.valid := false.B
    io.out.bits := VecInit(Seq.fill(size)(0.S))

    when (io.in.valid) {
        val out = Wire(Vec(size, SInt(xlen.W)))
        io.in.ready := true.B
        for (i <- 0 until size) {
            out(i) := Mux(io.enable, Mux(io.in.bits(i) >= 0.S, io.in.bits(i), 0.S), io.in.bits(i))
        }

        io.out.valid := true.B
        io.out.bits := out
    }
}