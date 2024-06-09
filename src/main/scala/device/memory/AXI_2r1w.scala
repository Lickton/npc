package device.memory

import chisel3._
import chisel3.util._
import chisel3.experimental._
import chisel3.util.HasBlackBoxResource

import interface._

class AXI_2r1w (width: Int) extends Module {
    val io = IO(new Bundle {
        val imem = new Slave_AXI4Lite(width)
        val dmem = new Slave_AXI4Lite(width)
        val dma_read = Flipped(new DMARead(8, 9))
        val dma_write = Flipped(new DMAWrite(8, 16))
    })
    
    val ram = Module(new ram_2r1w(width))
    val imem = io.imem
    val dmem = io.dmem

    ram.io.clock := clock
    ram.io.imem_ren := false.B
    ram.io.imem_raddr := 0.U
    ram.io.dmem_ren := false.B
    ram.io.dmem_raddr := 0.U
    ram.io.dmem_wen := false.B
    ram.io.dmem_waddr := 0.U
    ram.io.dmem_wdata := 0.U
    ram.io.dmem_wstrb := 0.U

    /*****************/
    /*** AXI-Lite ****/
    /*****************/
    AXI4Lite.slave_initialize(imem)
    AXI4Lite.slave_initialize(dmem)

    when (imem.read.arvalid) {
        ram.io.imem_ren := imem.read.araddr =/= 0.U
        imem.read.arready := true.B
        ram.io.imem_raddr := imem.read.araddr

        imem.read.rvalid := true.B
        imem.read.rdata := Mux(imem.read.rready, ram.io.imem_rdata, 0.U)
        imem.read.rresp := Mux(imem.read.rready, RESP.OKAY, 0.U)
    }

    when (dmem.read.arvalid) {
        ram.io.dmem_ren := dmem.read.araddr =/= 0.U
        dmem.read.arready := true.B
        ram.io.dmem_raddr := dmem.read.araddr

        dmem.read.rvalid := true.B
        dmem.read.rdata := Mux(dmem.read.rready, ram.io.dmem_rdata, 0.U)
        dmem.read.rresp := Mux(dmem.read.rready, RESP.OKAY, 0.U)
    }

    when (dmem.write.awvalid && dmem.write.wvalid) {
        ram.io.dmem_wen := dmem.write.awaddr =/= 0.U
        dmem.write.awready := true.B
        ram.io.dmem_waddr := dmem.write.awaddr

        dmem.write.wready := true.B
        ram.io.dmem_wdata := dmem.write.wdata
        ram.io.dmem_wstrb := dmem.write.wstrb

        dmem.write.bvalid := true.B
        dmem.write.bresp := Mux(dmem.write.bready, RESP.OKAY, 0.U)
    }

    /*****************/
    /****** DMA ******/
    /*****************/
    val read = io.dma_read
    val write = io.dma_write
    val addr = RegInit(0.U(32.W))

    read.ar.ready := true.B
    read.rd.valid := false.B
    read.rd.bits := 0.U
    when (read.ar.valid) {
        val count = RegInit(0.U(4.W))
        addr := read.ar.bits
        ram.io.dmem_ren := true.B
        when (count < 9.U) {
            ram.io.dmem_raddr := addr + count
            read.rd.bits := ram.io.dmem_rdata
            count := count + 1.U
        }
    }

    write.aw.ready := true.B
    write.wd.ready := true.B
    when (write.aw.valid) {
        val count = RegInit(0.U(4.W))
        addr := write.aw.bits
        ram.io.dmem_wen := true.B
        when (count < 16.U) {
            ram.io.dmem_waddr := addr + count
            ram.io.dmem_wstrb := "b0001".U
            ram.io.dmem_wdata := write.wd.bits
            count := count + 1.U
        }
    }
}