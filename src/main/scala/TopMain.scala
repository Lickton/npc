package npc

import chisel3.stage.ChiselStage

object getVerilog extends App {
    new ChiselStage().emitVerilog(new InstFetch(), Array("-td", "vsrc"))
    new ChiselStage().emitVerilog(new Decode(), Array("-td", "vsrc"))
    new ChiselStage().emitVerilog(new Execution(), Array("-td", "vsrc"))
    new ChiselStage().emitVerilog(new RegFile(), Array("-td", "vsrc"))
    new ChiselStage().emitVerilog(new Ram_2r1w(), Array("-td", "vsrc"))
    new ChiselStage().emitVerilog(new Core(), Array("-td", "vsrc"))
}