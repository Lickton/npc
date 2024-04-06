import chisel3.stage.ChiselStage

import npc._
import device._
import device.accelerator._
import utils._

object TopMain extends App {
  // new ChiselStage().emitVerilog(new SimTop(), Array("-td", "build/vsrc"))
  // new ChiselStage().emitVerilog(new Core(32), Array("-td", "build/vsrc"))
  // new ChiselStage().emitVerilog(new SystolicArray(8, 16, 16), Array("-td", "build/vsrc"))
  new ChiselStage().emitVerilog(new Conv(8, 2, 4), Array("-td", "build/vsrc"))
}