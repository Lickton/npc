package device.accelerator

import chisel3._
import interface._

/**
  * Memory Layout
  *   - 0x3000 0000 -> Base address register
  *   - 0x3000 0004 -> Input size register
  *   - 0x3000 0008 -> Input Select register
  *         - 1 -> Kernel and Activation
  *         - 2 -> Reslut
  *   - 0x3000 000c -> Need ReLU ?
  *   - 0x3a00 0000 -> Buffer address start
  *     0x3aff ffff -> Buffer address end
  */

class CNNCore (xlen: Int, kernel: Int, size: Int) extends Module {
	val io = IO(new Bundle {
		val axi = new Slave_AXI4Lite(32)
		val read = new DMARead(xlen, kernel * kernel)
		val write = new DMAWrite(xlen, size)
	})
    
    AXI4Lite.slave_initialize(io.axi)

    val fetcher = Module(new Fetcher(xlen, kernel))
    val buff0 = Module(new ActBuffer(xlen, kernel, size, 0, 512))
    val buff1 = Module(new ActBuffer(xlen, kernel, size, 1, 512))
    val conv = Module(new Conv(xlen, kernel, size))
    val relu = Module(new ReLU(xlen, size))
    val taker = Module(new Taker(xlen, size))

    val start = RegInit(0.U(32.W))
    val len = RegInit(0.U(32.W))
    val select = RegInit(0.U(4.W))
    val reluEn = RegInit(false.B)

    when (io.axi.write.awvalid && io.axi.write.wvalid) {
        io.axi.write.awready := true.B
        io.axi.write.wready := true.B
        val data = io.axi.write.wdata
        val addr = io.axi.write.awaddr
        start := Mux(addr === "h3000_0000".U, data, start)
        len := Mux(addr === "h3000_0004".U, data, len)
        select := Mux(addr === "h3000_0008".U, data, 0.U)
        reluEn := Mux(addr === "h3000_000c".U, data(0).asBool, reluEn)
    }

	fetcher.io.read <> io.read
    fetcher.io.config := (select === 1.U)
    fetcher.io.addr := Mux(select === 1.U, start, 0.U)
    fetcher.io.len := Mux(select === 1.U, len, 0.U)

    buff0.io.in <> fetcher.io.out(0)
    buff1.io.in <> fetcher.io.out(1)
	buff0.io.fetch := fetcher.io.fetch
	buff1.io.fetch := fetcher.io.fetch
    conv.io.in(0) <> buff0.io.out
    conv.io.in(1) <> buff1.io.out
    conv.io.kern <> fetcher.io.kern
    conv.io.en(0) := buff0.io.en
    conv.io.en(1) := buff1.io.en
    conv.io.idx(0) := buff0.io.idx
    conv.io.idx(1) := buff1.io.idx

	taker.io.write <> io.write
    relu.io.enable := reluEn
    relu.io.in <> conv.io.out
    taker.io.config := (select === 2.U)
	taker.io.addr := start
	taker.io.in <> relu.io.out
}