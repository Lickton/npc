import chisel3.stage.ChiselStage

import npc._
import device._
import device.accelerator._
import utils._

object TopMain extends App {
  new ChiselStage().emitVerilog(new SimTop(), Array("-td", "build/vsrc"))
}