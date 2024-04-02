import chisel3.stage.ChiselStage

import npc._
import device._
import utils._
import device._
import device.accelerator.SystolicArray

object TopMain extends App {
  new ChiselStage().emitVerilog(new SimTop(), Array("-td", "build/vsrc"))
  // new ChiselStage().emitVerilog(new SystolicArray(8, 3, 3), Array("-td", "build/vsrc"))
}