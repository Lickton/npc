package device.accelerator

import chisel3._
import chisel3.util._
import interface._

// class Fetcher(xlen: Int, kernel: Int) extends Module {
//     def flat = kernel * kernel
//     val io = IO(new Bundle {
//         val config = Input(Bool())
//         val addr = Input(UInt(32.W))
//         val size = Input(UInt(32.W))
//         val dma = new DMA(xlen, flat)
//         val out = Vec(2, Decoupled(Vec(flat, UInt(xlen.W))))
//         val kern = Decoupled(Output(Vec(flat, UInt(xlen.W))))
//         val fetch = Output(Bool())
//     })

//     io.dma.ar.valid := false.B
//     io.dma.ar.bits := 0.U
//     io.dma.rd.ready := false.B
//     io.dma.aw.valid := false.B
//     io.dma.aw.bits := 0.U
//     io.dma.wd.valid := 0.U
//     io.dma.wd.bits := VecInit(Seq.fill(flat)(0.U))
//     io.out(0).valid := false.B
//     io.out(0).bits := VecInit(Seq.fill(flat)(0.U))
//     io.out(1).valid := false.B
//     io.out(1).bits := VecInit(Seq.fill(flat)(0.U))
//     io.kern.valid := false.B
//     io.kern.bits := VecInit(Seq.fill(flat)(0.U))

//     val base = RegInit(0.U(32.W))
//     val idx = RegInit(0.U(1.W))
//     val count = RegInit(0.U(32.W))
//     val SIZE = RegInit(0.U(32.W))

//     base := Mux(io.config, io.addr, base)
//     count := Mux(io.config, io.size, count)
//     SIZE := Mux(io.config, io.size, SIZE)

//     io.fetch := false.B

//     when (count > 0.U && count < SIZE) {
//         io.dma.ar.valid := true.B
//         io.dma.ar.bits := base
//         io.dma.rd.ready := true.B
//         when (count === 0.U) {
//             io.kern.valid := io.dma.rd.valid
//             io.kern.bits := io.dma.rd.bits
//             count := flat.U
//         } .otherwise {
//             io.out(idx).valid := io.dma.rd.valid
//             io.out(idx).bits := io.dma.rd.bits
//             count := count + flat.U
//             idx := idx + 1.U
//         }
//         base := base + flat.U
//         io.fetch := true.B
//     }
// }

class Fetcher(xlen: Int, kernel: Int) extends Module {
    def flat = kernel * kernel
    val io = IO(new Bundle {
        val config = Input(Bool())
        val addr = Input(UInt(32.W))
        val len = Input(UInt(32.W))
        val read = new DMARead(xlen, flat)
        val out = Vec(2, Decoupled(Vec(flat, UInt(xlen.W))))
        val kern = Decoupled(Output(Vec(flat, UInt(xlen.W))))
        val fetch = Output(Bool())
    })

    io.read.ar.valid := false.B
    io.read.ar.bits := 0.U
    io.read.rd.ready := false.B
    io.out(0).valid := false.B
    io.out(0).bits := VecInit(Seq.fill(flat)(0.U))
    io.out(1).valid := false.B
    io.out(1).bits := VecInit(Seq.fill(flat)(0.U))
    io.kern.valid := false.B
    io.kern.bits := VecInit(Seq.fill(flat)(0.U))

    val start = RegInit(0.U(32.W))
    val end = RegInit(0.U(32.W))
    val idx = RegInit(0.U(1.W))
    val current = RegInit(0.U(32.W))

    start := Mux(io.config, io.addr, start)
    end := Mux(io.config, io.addr + io.len, end)

    when (current > start && current < end) {
        io.read.ar.valid := true.B
        io.read.ar.bits := current
        io.read.rd.ready := true.B
        val count = RegInit(0.U(4.W))
        val tmp = Reg(Vec(flat, UInt(xlen.W)))
        when (count < flat.U && io.read.rd.valid) {
            tmp(count) := io.read.rd.bits
            count := count + 1.U
        }.otherwise {
            when (current === start) {
                io.kern.valid := true.B
                io.kern.bits := tmp
            }.otherwise {
                io.out(idx).valid := true.B
                io.out(idx).bits := tmp
                idx := idx + 1.U
            }
            current := current + ((xlen / 8) * flat).U
        }
    } .elsewhen(current === 0.U && io.config) {
        current := start
    }

    io.fetch := current < end && current =/= 0.U
}