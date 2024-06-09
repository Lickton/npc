import chisel3.stage.ChiselStage

import npc._
import device._
import device.accelerator._
import utils._

object TopMain extends App {
  // new ChiselStage().emitVerilog(new SimTop(), Array("-td", "build/vsrc"))
  // new ChiselStage().emitVerilog(new Conv(8, 3, 16), Array("-td", "build/vsrc"))
  // new ChiselStage().emitVerilog(new ReLU(8, 16), Array("-td", "build/vsrc"))
  new ChiselStage().emitVerilog(new Pooling(8, 16), Array("-td", "build/vsrc"))
}