package device.accelerator

import chisel3._
import chisel3.util._
import interface._

/**
 * A buffer module that stores data in a synchronous read/write memory.
 *
 * @param width    The bit width of the data elements stored in the buffer.
 * @param size     The size of the buffer, i.e., the number of data elements it can store.
 * @param kernSize The size of the kernel.
 * @param arrayNum The number of arrays in the buffer.
 */
class Buffer(width: Int, size: Int, kernSize: Int, arrayNum: Int) extends Module {
    val io = IO(new Bundle {
        val dmem = Flipped(new RamIO(Vec(arrayNum, SInt(width.W)), width))
        val rvalid = Output(Bool())
    })

    val buffer = SyncReadMem(size, Vec(arrayNum, SInt(width.W)))
    io.rvalid := false.B

    when(io.dmem.ren) {
        io.dmem.rdata := buffer.read(io.dmem.raddr)
        io.rvalid := true.B
    }

    when(io.dmem.wen) {
        io.rvalid := false.B
        buffer.write(io.dmem.waddr, io.dmem.wdata)
    }
}
