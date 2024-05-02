package device.accelerator

import chisel3._
import chisel3.util._

class PE(xlen: Int, kernSize: Int) extends Module {
    def flattenSize = kernSize * kernSize
    val io = IO(new Bundle {
        val enable    = Input(Bool())
        val ready     = Input(Bool())
        val activa    = Input(Vec(flattenSize, SInt(xlen.W)))
        val kernel    = Input(Vec(flattenSize, SInt(xlen.W)))
        val outValid  = Output(Bool())
        val out       = Output(SInt(xlen.W))
    })

    val activa = Reg(Vec(flattenSize, SInt(xlen.W)))
    activa := Mux(io.ready, io.activa, activa)

    io.out := 0.S
    io.outValid := false.B

    when (io.enable) {
        val tmp = Wire(Vec(flattenSize, SInt(xlen.W)))
        for (i <- 0 until flattenSize) {
            tmp(i) := activa(i) * io.kernel(i)
        }

        io.out := tmp.reduceTree(_ + _)
        io.outValid := true.B
    }
}

class Conv(xlen: Int, kernel: Int, size: Int) extends Module {
    def flat = kernel * kernel
    val io = IO(new Bundle {
        val in = Vec(2, Flipped(Decoupled(Input(Vec(flat, UInt(xlen.W))))))
        val out = Decoupled(Vec(size, SInt(xlen.W)))
        val kern = Flipped(Decoupled(Input(Vec(flat, UInt(xlen.W)))))
        val en = Vec(2, (Input(Bool())))
        val idx = Input(Vec(2, UInt(log2Ceil(size).W)))
    })

    io.in(0).ready := true.B
    io.in(1).ready := true.B
    io.kern.ready := true.B
    io.out.valid := false.B

    val kern = Reg(Vec(flat, UInt(xlen.W)))
    kern := Mux(io.kern.valid, io.kern.bits, kern)

    val PEs = Seq.fill(size)(Module(new PE(xlen, kernel)))
    for (i <- 0 until size) {
        PEs(i).io.ready := io.idx(i % 2) === i.U
        PEs(i).io.activa := io.in(i % 2).bits.map(_.asSInt)
        PEs(i).io.kernel := kern.map(_.asSInt)
        PEs(i).io.enable := io.en(0) && io.en(1)
    }

    io.out.bits := VecInit(Seq.fill(size)(0.S))
    for (i <- 0 until size) {
        io.out.bits(i) := PEs(i).io.out
    }
    
    io.out.valid := PEs.map(_.io.outValid).reduce(_ && _)
    // io.out.valid := reduceTree(PEs.map(_.io.outValid)){ _ && _ }
}