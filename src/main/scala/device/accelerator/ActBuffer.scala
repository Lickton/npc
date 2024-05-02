package device.accelerator

import chisel3._
import chisel3.util._
import interface._
import scopt.Read

class ActBuffer(xlen: Int, kernel: Int, size: Int, No: Int, depth: Int) extends Module {
    require(No == 0 || No == 1)
    def flat = kernel * kernel
    val io = IO(new Bundle {
        val in = Flipped(Decoupled(Input(Vec(flat, UInt(xlen.W)))))
        val out = Decoupled(Output(Vec(flat, UInt(xlen.W))))
        val fetch = Input(Bool())
        val en = Output(Bool())
        val idx = Output(UInt(log2Ceil(size).W))
    })

    io.in.ready := false.B
    io.out.valid := false.B
    io.out.bits := VecInit(Seq.fill(flat)(0.U))
    io.en := false.B

    val Writeidx = Wire(UInt(log2Ceil(depth).W))
    val Readidx = Wire(UInt(log2Ceil(depth).W))
    Writeidx := 0.U
    Readidx := 0.U
    val i = Reg(UInt(log2Ceil(depth).W))
    val j = Reg(UInt(log2Ceil(depth).W))

    val ram = SyncReadMem(depth, Vec(flat, UInt(xlen.W)))
    when (io.in.valid && io.fetch) {
        io.in.ready := io.fetch
        Writeidx := (2.U * i) + No.U
        ram.write(Writeidx, io.in.bits)
        i := i + 1.U
    }

    when (!io.fetch && j < i && io.out.ready) {
        Readidx := (2.U * j) + No.U
        io.out.bits := ram.read(Readidx)
        j := j + 1.U
        io.out.valid := true.B
    }

    io.idx := Readidx(3, 0)
    io.en := (Readidx(3, 0) === size.U - 2.U + No.U) || ((Readidx === Writeidx) && Writeidx =/= 0.U)
}