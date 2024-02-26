import chisel3._
import chisel3.util._
import chisel3.experimental._
import chisel3.util.HasBlackBoxResource

class RomIO (width : Int) extends Bundle {
    val ren = Output(Bool())
    val raddr = Output(UInt(width.W))
    val rdata = Input(UInt(width.W))
}

class RamIO (width : Int) extends RomIO (width) {
    val wen   = Output(Bool())
    val waddr = Output(UInt(width.W))
    val wdata = Output(UInt(width.W))
    val wmask = Output(UInt(width.W))
}

class ram_2r1w (width : Int) extends BlackBox with HasBlackBoxResource {
    val io = IO(new Bundle {
        val clock      = Input(Clock())
        
        val imem_ren   = Input(Bool())
        val imem_raddr = Input(UInt(width.W))
        val imem_rdata = Output(UInt(width.W))
        val dmem_ren   = Input(Bool())
        val dmem_raddr = Input(UInt(width.W))
        val dmem_rdata = Output(UInt(width.W))
        val dmem_wen   = Input(Bool())
        val dmem_waddr = Input(UInt(width.W))
        val dmem_wmask = Input(UInt(width.W))
        val dmem_wdata = Input(UInt(width.W))
    })

    addResource("ram_2r1w.v")
}

class Ram_2r1w (width : Int) extends Module {
    val io = IO(new Bundle {
        val imem = Flipped(new RomIO(width))
        val dmem = Flipped(new RamIO(width))
    })
    
    val ram = Module(new ram_2r1w(width))

    ram.io.clock := clock

    ram.io.imem_ren := io.imem.ren
    ram.io.imem_raddr := io.imem.raddr
    io.imem.rdata := ram.io.imem_rdata

    ram.io.dmem_ren := io.dmem.ren
    ram.io.dmem_raddr := io.dmem.raddr
    io.dmem.rdata := ram.io.dmem_rdata

    ram.io.dmem_wen := io.dmem.wen
    ram.io.dmem_waddr := io.dmem.waddr
    ram.io.dmem_wmask := io.dmem.wmask
    ram.io.dmem_wdata := io.dmem.wdata
}