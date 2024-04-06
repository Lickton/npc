package device.accelerator

import chisel3._
import chisel3.util._

/**
 * This class represents a Processing Element (PE) used in a convolution operation.
 *
 * @param width    The bit width of the input and output signals.
 * @param kernSize The size of the kernel used in the convolution.
 */
class PE(width: Int, kernSize: Int) extends Module {
    /**
     * The size of the input and output vectors after flattening the kernel.
     */
    def flattenSize = kernSize * kernSize

    val io = IO(new Bundle {
        val enable    = Input(Bool())
        val ready     = Input(Bool())
        val activa    = Input(Vec(flattenSize, SInt(width.W)))
        val kernel    = Input(Vec(flattenSize, SInt(width.W)))
        val outValid  = Output(Bool())
        val out       = Output(SInt(width.W))
    })

    val activa = Reg(Vec(flattenSize, SInt(width.W)))
    activa := Mux(io.ready, io.activa, activa)

    io.out := 0.S
    io.outValid := false.B

    when (io.enable) {
        val tmp = Wire(Vec(flattenSize, SInt(width.W)))
        for (i <- 0 until flattenSize) {
            tmp(i) := activa(i) * io.kernel(i)
        }

        io.out := tmp.reduceLeft(_ + _)
        io.outValid := true.B
    }
}

class ConvIO(width: Int, kernSize: Int) extends Bundle {
    def flattenSize = kernSize * kernSize
    val activa = Input(Vec(flattenSize, SInt(width.W)))
    val kernel = Input(Vec(flattenSize, SInt(width.W)))
    val enable = Input(Bool())
    val pe_sel = Input(UInt(4.W))
}

/**
 * This class represents a Convolutional Neural Network (CNN) accelerator module.
 *
 * @param width    The bit width of the data elements.
 * @param kernSize The size of the kernel used in the convolution operation.
 * @param arrayNum The number of processing elements (PEs) in the accelerator.
 */
class Conv(width: Int, kernSize: Int, arrayNum: Int) extends Module {
    def flattenSize = kernSize * kernSize
    val io = IO(new Bundle {
        val data = new ConvIO(width, kernSize)
        val res = Decoupled(Vec(arrayNum, SInt(width.W)))
    })

    val PEs = Seq.fill(arrayNum)(Module(new PE(width, kernSize)))
    for (i <- 0 until arrayNum) {
        PEs(i).io.ready := io.data.pe_sel === i.U
        PEs(i).io.activa := io.data.activa
        PEs(i).io.kernel := io.data.kernel
        PEs(i).io.enable := io.data.enable
    }

    io.res.bits := VecInit(Seq.fill(arrayNum)(0.S))
    for (i <- 0 until arrayNum) {
        io.res.bits(i) := PEs(i).io.out
    }
    io.res.valid := PEs.map(_.io.outValid).reduce(_ && _) && io.res.ready
}