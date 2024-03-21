package interface

import chisel3._
import chisel3.util._

// ref https://docs.amd.com/r/en-US/pg085-axi4stream-infrastructure/AXI4-Lite-Interface-Signals

class AXI_Lite (width : Int) extends Bundle {
    /* AXI4-Lite has a fixed data bus width and all 
     * transactions are the same width as the data bus.
     * The data bus width must be, either 32-bits or 64-bits
     */
    assert(width == 32 || width == 64)

    val aclk    = Input(Bool())
    val aresetn = Input(Bool()) // Negative enable

    // Adress Write channel
    val awvalid = Input(Bool())
    val awready = Output(Bool())
    val awaddr  = Input(UInt(width.W))

    // Write data channel
    val wvalid  = Input(Bool())
    val wready  = Output(Bool())
    val wdata   = Input(UInt(width.W))
    val wstrb   = Input(UInt((width / 8).W))
    
    // Write response channel
    val bvalid  = Output(Bool())
    val bready  = Input(Bool())
    val bresp   = Output(UInt(2.W))

    // Address Read channel
    val arvalid = Input(Bool())
    val arready = Output(Bool())
    val araddr  = Input(UInt(width.W))

    // Read data channel
    val rvalid  = Output(Bool())
    val rready  = Input(Bool())
    val rdata   = Output(UInt(width.W))
    val rresp   = Output(UInt(2.W))
}