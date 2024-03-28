package npc

import chisel3._
import chisel3.util._

import interface._
import utils._

import Control._

class LoadStoreBundle (width : Int) extends Bundle {
    val st_sel = Input(UInt(3.W))
    val waddr  = Input(UInt(width.W))
    val wdata  = Input(UInt(width.W))
    val ld_sel = Input(UInt(3.W))
    val raddr  = Input(UInt(width.W))
    val rdata  = Output(UInt(width.W))
}

class LoadStore (width : Int) extends Module {
    val io = IO(new Bundle {
        val signals = new LoadStoreBundle(width)
        val axi = new Master_AXI4Lite(width)
    })
    val axi = io.axi

    val isStore = io.signals.st_sel =/= 0.U
    val isLoad  = io.signals.ld_sel =/= 0.U

    val wstrb = MuxLookup(io.signals.st_sel, 0.U) (
        Seq (
            ST_SB -> ("b0001".U),
            ST_SH -> ("b0011".U),
            ST_SW -> ("b1111".U)
        )
    )

    /*****************/
    /*** AXI-Lite ****/
    /*****************/
    // Address Write channel
    axi.write.awvalid := isStore
    axi.write.awaddr := Mux(axi.write.awready, io.signals.waddr, 0.U)
    
    // Write data channel
    axi.write.wvalid := isStore
    axi.write.wdata := Mux(axi.write.wready, io.signals.wdata, 0.U)
    axi.write.wstrb := Mux(axi.write.wready, wstrb, 0.U)

    // Write response channel
    axi.write.bready := isStore
    val bresp = Mux(axi.write.bvalid, axi.write.bresp, 0.U)
    assert(bresp === RESP.OKAY)

    // Address Read channel
    axi.read.arvalid := isLoad

    // /* 1 axi.read.arready  <- dmem.read.arready */
    // /* 2 axi.read.araddr   <- axi.read.arready */
    // /* 3 dmem.read.araddr  <- axi.read.araddr */
    // /*12 dmem.read.arready <- in.read.arready */
    axi.read.araddr := Mux(axi.read.arready, io.signals.raddr, 0.U)
    
    // Read data channel
    axi.read.rready := isLoad
    val rdata = Mux(axi.read.rvalid, axi.read.rdata, 0.U)
    val rresp = Mux(axi.read.rvalid, axi.read.rresp, 0.U)
    assert(rresp === RESP.OKAY)

    io.signals.rdata := MuxLookup(io.signals.ld_sel, 0.U) (
        Seq (
            LD_LB  -> (Cat(Fill(24, rdata(7)), rdata(7, 0))),
            LD_LH  -> (Cat(Fill(16, rdata(15)), rdata(15, 0))),
            LD_LW  -> (rdata),
            LD_LBU -> (Cat(Fill(24, 0.U), rdata(7, 0))),
            LD_LHU -> (Cat(Fill(16, 0.U), rdata(15, 0)))
        )
    )
}