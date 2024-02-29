import chisel3._
import chisel3.util._

import Control._

class InstFetch(width : Int) extends Module {
    val io = IO(new Bundle {
        val imem = new RomIO(width)
        val pc   = Output(UInt(width.W))
        val inst = Output(UInt(width.W))
        // decode
        val pc_sel = Input(UInt(2.W))
        // alu
        val calc_out = Input(UInt(width.W))
        val comp_out = Input(Bool())
    })

    val pc = RegInit("h80000000".U(32.W))
    val dnpc = io.calc_out
    val snpc = pc + 4.U
    
    pc := MuxLookup(io.pc_sel, 0.U) (
        Seq (
            PC_4  -> (snpc),
            PC_UC -> (dnpc),
            PC_BR -> (Mux(io.comp_out, dnpc, snpc))
        )
    )

    io.imem.ren  := true.B
    io.imem.raddr := pc

    io.pc := pc
    io.inst := io.imem.rdata
}