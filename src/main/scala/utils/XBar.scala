package utils

import chisel3._
import chisel3.util._

import interface._
import interface.RESP
import npc.Instructions
import os.write

class Addr2Dev (width: Int) extends Module {
    val io = IO(new Bundle {
        val addr = Input(UInt(width.W))
        val devID  = Output(UInt(2.W))
    })

    val lut = List(
        ("h80000000".U, "h8000000".U, "b00".U), // SRAM
        ("ha0000000".U, "h8000000".U, "b01".U), // CNN Accelerator
    )

    io.devID := "b00".U
    for ((start, size, id) <- lut) {
        when(io.addr >= start && io.addr < (start + size)) {
            io.devID := id
        }
    }
}

class XBarIO (width: Int, n: Int) extends Bundle {
    val in = new Slave_AXI4Lite(width)
    val out = Vec(n, new Master_AXI4Lite(width))
}

class XBar (width: Int, n: Int) extends Module {
    /*  AXI4Lite Cross Bar
     *   +--------+                     +--------+                     +--------+
     *   |        | ----- arvalid ----> |        | ----- arvalid ----> |        |
     *   |        | <---- arready ----- |        | <---- arready ----- |        |
     *   |        | ----- araddr  ----> |        | ----- araddr  ----> |        |
     *   | Master |                     |  XBar  |                     | Slavee |
     *   |        | <---- rvalid  ----- |        | <---- rvalid  ----- |        |
     *   |        | ----- rready  ----> |        | ----- rready  ----> |        |
     *   |        | <---- rdata   ----- |        | <---- rdata   ----- |        |
     *   |        | <---- rresp   ----- |        | <---- rresp   ----- |        |
     *   |        |                     |        |                     |        |
     *   |        | ----- awvalid ----> |        | ----- awvalid ----> |        |
     *   |        | <---- awready ----- |        | <---- awready ----- |        |
     *   |        | ----- awaddr  ----> |        | ----- awaddr  ----> |        |
     *   |        |                     |        |                     |        |
     *   |        | ----- wvalid  ----> |        | ----- wvalid  ----> |        |
     *   |        | <---- wready  ----- |        | <---- wready  ----- |        |
     *   | Master | ----- wdata   ----> |  XBar  | ----- wdata   ----> | Slavee |
     *   |        | ----- wstrb   ----> |        | ----- wstrb   ----> |        |
     *   |        |                     |        |                     |        |
     *   |        | <---- bvalid  ----- |        | <---- bvalid  ----- |        |
     *   |        | ----- bready  ----> |        | ----- bready  ----> |        |
     *   |        | <---- bresp   ----- |        | <---- bresp   ----- |        |
     *   +--------+                     +--------+                     +--------+
     */
    val io = IO(new XBarIO(width, n))

    val in = io.in
    val out = io.out

    AXI4Lite.slave_initialize(in)

    for (i <- 0 until n) {
    AXI4Lite.master_initialize(out(i))
    }

    in.read.arready := in.read.arvalid
    in.write.awready := in.write.awvalid

    val addr2Dev = Module(new Addr2Dev(width))
    
    val addr = Mux(in.read.arvalid || in.write.wvalid, in.read.araddr, 0.U)
    addr2Dev.io.addr := addr

    val devID = addr2Dev.io.devID

    /*11 in.read.arready     <- out(0).read.arready */
    out(devID).read.arvalid := in.read.arvalid
    out(devID).read.araddr := Mux(out(devID).read.arready, in.read.araddr, 0.U)

    out(devID).read.rready := out(devID).read.rvalid
    in.read.rvalid := out(devID).read.rvalid
    in.read.rdata := Mux(in.read.rready && out(devID).read.rvalid, out(devID).read.rdata, 0.U)
    in.read.rresp := Mux(in.read.rready && out(devID).read.rvalid, out(devID).read.rresp, 0.U)

    out(devID).write.awvalid := in.write.awvalid
    out(devID).write.awaddr := Mux(out(devID).write.awready, in.write.awaddr, 0.U)

    out(devID).write.wvalid := in.write.wvalid
    in.write.wready := out(devID).write.wready
    val wdata = Mux(in.write.wvalid, in.write.wdata, 0.U)
    val wstrb = Mux(in.write.wvalid, in.write.wstrb, 0.U)
    out(devID).write.wdata := Mux(in.write.wvalid && out(devID).write.wready, wdata, 0.U)
    out(devID).write.wstrb := Mux(in.write.wvalid && out(devID).write.wready, wstrb, 0.U)

    in.write.bvalid := out(devID).write.bvalid
    out(devID).write.bready := in.write.bready
    val bresp = Mux(out(devID).write.bvalid, out(devID).write.bresp, 0.U)
    in.write.bresp := Mux(in.write.bready, bresp, 0.U)
}