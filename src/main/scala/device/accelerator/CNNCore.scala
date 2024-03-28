package device.accelerator

import chisel3._
import interface._

class CNNCore (width: Int) extends Module {
    val io = IO(new Slave_AXI4Lite(32))
    
    AXI4Lite.slave_initialize(io)

    
}