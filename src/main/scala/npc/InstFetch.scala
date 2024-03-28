package npc

import chisel3._
import chisel3.util._

import interface._

import Control._

class InstFetch(width : Int) extends Module {
    val io = IO(new Bundle {
        // val imem = new RomIO(width)
        val imem = new Master_AXI4Lite(width)
        val pc   = Output(UInt(width.W))
        val inst = Output(UInt(width.W))
        val pc_sel = Input(UInt(2.W))
        val calc_out = Input(UInt(width.W))
        val comp_out = Input(Bool())
    })

    val imem = io.imem
    val pc = RegInit("h80000000".U(32.W))

    /*****************/
    /*** AXI-Lite ****/
    /*****************/
    // imem.aclk := clock
    // imem.aresetn := reset

    imem.read.arvalid := true.B
    imem.read.araddr := Mux(imem.read.arready, pc, 0.U)
    
    imem.read.rready := true.B
    io.inst := Mux(imem.read.rvalid, imem.read.rdata, 0.U)
    val rresp = Mux(imem.read.rvalid, imem.read.rresp, 0.U)
    assert(rresp === RESP.OKAY)

    imem.write.awvalid := false.B
    imem.write.awaddr := 0.U
    imem.write.wvalid := false.B
    imem.write.wdata := 0.U
    imem.write.wstrb := 0.U
    imem.write.bready := false.B

    val dnpc = io.calc_out
    val snpc = pc + 4.U
    
    pc := MuxLookup(io.pc_sel, 0.U) (
        Seq (
            PC_4  -> (snpc),
            PC_UC -> (dnpc),
            PC_BR -> (Mux(io.comp_out, dnpc, snpc))
        )
    )

    io.pc := pc
}