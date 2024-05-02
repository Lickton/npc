package device.accelerator

import chisel3._
import chisel3.util._
import interface._

class Taker(xlen: Int, size: Int) extends Module {
    val io = IO(new Bundle {
        val config = Input(Bool())
        val in = Flipped(Decoupled(Vec(size, SInt(xlen.W))))
        val addr = Input(UInt(32.W))
        val write = new DMAWrite(xlen, size) 
    })

    io.in.ready := false.B
    io.write.aw.valid := false.B
    io.write.aw.bits := 0.U
    io.write.wd.valid := false.B
    io.write.wd.bits := 0.U

    val start = RegInit(0.U(32.W))
    start := Mux(io.config, io.addr, start)
    val current = RegInit(0.U(32.W))
    val tmp = Reg(Vec(size, UInt(xlen.W)))
    tmp := VecInit(io.in.bits.map(b => Mux(io.in.valid, b.asUInt, 0.U)))

    io.in.ready := true.B
    when (io.in.valid) {
        val count = RegInit(0.U(32.W))
        io.write.aw.valid := true.B
        io.write.aw.bits := current
        when (count < size.U) {
            io.write.wd.valid := true.B
            io.write.wd.bits := tmp(count)
            count := count + 1.U
        } .otherwise {
            count := 0.U
            io.write.wd.valid := false.B
        }
        current := current + ((xlen / 8) * size).U
    }
}