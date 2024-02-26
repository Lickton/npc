import chisel3.stage.ChiselStage

object TopMain extends App {
  new ChiselStage().emitVerilog(new SimTop(), Array("-td", "build/vsrc"))
}