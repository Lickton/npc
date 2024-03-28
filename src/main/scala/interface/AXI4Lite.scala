package interface

import chisel3._
import chisel3.util._
import firrtl.FirrtlProtos.Firrtl.Type.ClockType

// ref https://docs.amd.com/r/en-US/pg085-axi4stream-infrastructure/AXI4-Lite-Interface-Signals

object RESP {
    val OKAY   = "b00".U // 1. the success of a normal access
                         // 2. the failure of an exclusive access
                         // 3. an exclusice access to a slve that does not support exclusive access

    val EXOKAY = "b01".U // 1. the success of an exclusive access

    val SLVERR = "b10".U // 1. FIFO or buffer overrun or underrun condition
                         // 2. unsupported transfer size attempted
                         // 3. timeout condition in the slave
                         // 4. access attempted to a disabled or powered-down function

    val DECERR = "b11".U // 1. the interconnect cannot successfully decode a slave access
}

object AXI4Lite {
    def conncet(master: Master_AXI4Lite, slave: Slave_AXI4Lite): Unit = {
        /*  AXI4Lite Connect
         *   +--------+                     +--------+
         *   |        | ----- arvalid ----> |        |
         *   |        | <---- arready ----- |        |
         *   |        | ----- araddr  ----> |        |
         *   | Master |                     | Slavee |
         *   |        | <---- rvalid  ----- |        |
         *   |        | ----- rready  ----> |        |
         *   |        | <---- rdata   ----- |        |
         *   |        | <---- rresp   ----- |        |
         *   |        |                     |        |
         *   |        | ----- awvalid ----> |        |
         *   |        | <---- awready ----- |        |
         *   |        | ----- awaddr  ----> |        |
         *   |        |                     |        |
         *   |        | ----- wvalid  ----> |        |
         *   |        | <---- wready  ----- |        |
         *   | Master | ----- wdata   ----> | Slavee |
         *   |        | ----- wstrb   ----> |        |
         *   |        |                     |        |
         *   |        | <---- bvalid  ----- |        |
         *   |        | ----- bready  ----> |        |
         *   |        | <---- bresp   ----- |        |
         *   +--------+                     +--------+
         */
        slave.read.arvalid := master.read.arvalid
        master.read.arready := slave.read.arready
        slave.read.araddr := master.read.araddr

        master.read.rvalid := slave.read.rvalid
        slave.read.rready := master.read.rready
        master.read.rdata := slave.read.rdata
        master.read.rresp := slave.read.rresp

        slave.write.awvalid := master.write.awvalid
        master.write.awready := slave.write.awready
        slave.write.awaddr := master.write.awaddr

        slave.write.wvalid := master.write.wvalid
        master.write.wready := slave.write.wready
        slave.write.wdata := master.write.wdata
        slave.write.wstrb := master.write.wstrb

        master.write.bvalid := slave.write.bvalid
        slave.write.bready := master.write.bready
        master.write.bresp := slave.write.bresp
    }

    def master_initialize(axi: Master_AXI4Lite): Unit = {
        axi.read.arvalid := false.B
        axi.read.araddr := 0.U
        axi.read.rready := false.B
        
        axi.write.awvalid := false.B
        axi.write.awaddr := 0.U
        axi.write.wvalid := false.B
        axi.write.wdata := 0.U
        axi.write.wstrb := 0.U
        axi.write.bready := false.B
    }
    
    def slave_initialize(axi: Slave_AXI4Lite): Unit = {
        axi.read.arready := false.B
        axi.read.rvalid := false.B
        axi.read.rdata := 0.U
        axi.read.rresp := 0.U
        
        axi.write.awready := false.B
        axi.write.wready := false.B
        axi.write.bvalid := false.B
        axi.write.bresp := false.B
    }

    def pass_through(call: Master_AXI4Lite, recive: Master_AXI4Lite): Unit = {
        recive.read.arvalid := call.read.arvalid
        call.read.arready := recive.read.arready
        recive.read.araddr := call.read.araddr

        call.read.rvalid := recive.read.rvalid
        recive.read.rready := call.read.rready
        call.read.rdata := recive.read.rdata
        call.read.rresp := recive.read.rresp

        recive.write.awvalid := call.write.awvalid
        call.write.awready := recive.write.awready
        recive.write.awaddr := call.write.awaddr

        recive.write.wvalid := call.write.wvalid
        call.write.wready := recive.write.wready
        recive.write.wdata := call.write.wdata
        recive.write.wstrb := call.write.wstrb

        call.write.bvalid := recive.write.bvalid
        recive.write.bready := call.write.bready
        call.write.bresp := recive.write.bresp
    }
}

class AXI4LiteRead (width: Int) extends Bundle {
    /* AXI4Lite Read Channel
     *   +--------+                     +--------+
     *   |        | ----- arvalid ----> |        |
     *   |        | <---- arready ----- |        |
     *   |        | ----- araddr  ----> |        |
     *   | Master |                     | Slavee |
     *   |        | <---- rvalid  ----- |        |
     *   |        | ----- rready  ----> |        |
     *   |        | <---- rdata   ----- |        |
     *   |        | <---- rresp   ----- |        |
     *   +--------+                     +--------+
     */

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

class AXI4LiteWrite (width: Int) extends Bundle {
    /*  AXI4Lite Write Channel
     *   +--------+                     +--------+
     *   |        | ----- awvalid ----> |        |
     *   |        | <---- awready ----- |        |
     *   |        | ----- awaddr  ----> |        |
     *   |        |                     |        |
     *   |        | ----- wvalid  ----> |        |
     *   |        | <---- wready  ----- |        |
     *   | Master | ----- wdata   ----> | Slavee |
     *   |        | ----- wstrb   ----> |        |
     *   |        |                     |        |
     *   |        | <---- bvalid  ----- |        |
     *   |        | ----- bready  ----> |        |
     *   |        | <---- bresp   ----- |        |
     *   +--------+                     +--------+
     */

    // Adress Write channel
    val awvalid = Input(Bool())
    val awready = Output(Bool())
    val awaddr  = Input(UInt(width.W))

    // Write data channel
    val wvalid  = Input(Bool())
    val wready  = Output(Bool())
    val wdata   = Input(UInt(width.W))
    val wstrb   = Input(UInt((width / 8).W))// Which byte lanes hold valid data. 
                                            // 1 bit for each 8 bits of write data bus
                                            // 0001 -> sb, 0011 -> sh, 1111 -> sw
    
    // Write response channel
    val bvalid  = Output(Bool())
    val bready  = Input(Bool())
    val bresp   = Output(UInt(2.W))
}

class Slave_AXI4Lite (width: Int) extends Bundle {
    /* As a slave, need to be Flipped when master.
     * AXI4-Lite has a fixed data bus width and all 
     * transactions are the same width as the data bus.
     * The data bus width must be, either 32-bits or 64-bits
     */
    assert(width == 32 || width == 64)

    // val aclk    = Input()
    // val aresetn = Input(Bool()) // Negative enable

    val read = new AXI4LiteRead(width)
    val write = new AXI4LiteWrite(width)
}

class Master_AXI4Lite (width: Int) extends Bundle {
    /* As a slave, need to be Flipped when master.
     * AXI4-Lite has a fixed data bus width and all 
     * transactions are the same width as the data bus.
     * The data bus width must be, either 32-bits or 64-bits
     */
    assert(width == 32 || width == 64)

    val read = Flipped(new AXI4LiteRead(width))
    val write = Flipped(new AXI4LiteWrite(width))
}