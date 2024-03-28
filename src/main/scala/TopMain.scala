import chisel3.stage.ChiselStage

import npc._
import device._
import utils._
import device.memory.AXI_2r1w

object TopMain extends App {
  new ChiselStage().emitVerilog(new SimTop(), Array("-td", "build/vsrc"))
  // new ChiselStage().emitVerilog(new XBar(32, 2), Array("-td", "build/vsrc"))
  // new ChiselStage().emitVerilog(new Core(32), Array("-td", "build/vsrc"))
  // new ChiselStage().emitVerilog(new InstFetch(32), Array("-td", "build/vsrc"))
  // new ChiselStage().emitVerilog(new AXI_2r1w(32), Array("-td", "build/vsrc"))
  // new ChiselStage().emitVerilog(new LoadStore(32), Array("-td", "build/vsrc"))
}