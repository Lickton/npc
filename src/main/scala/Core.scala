import chisel3._
import chisel3.util._
import chisel3.util.experimental._

import Control._

class Core (width : Int) extends Module {
    val io = IO(new Bundle {
        val imem = new RomIO(width)
        val dmem = new RamIO(width)
        val break = Output(Bool())
    })

    val fetch = Module(new InstFetch(width))
    fetch.io.imem <> io.imem

    val decode = Module(new Decode(width))
    decode.io.inst := fetch.io.inst
    fetch.io.pc_sel := decode.io.pc_sel
    io.break := decode.io.break
    
    val rf = Module(new RegFile(width))
    rf.io.rs1_addr := decode.io.rs1_addr
    rf.io.rs2_addr := decode.io.rs2_addr
    rf.io.rd_addr := decode.io.rd_addr
    rf.io.rd_en := Mux(decode.io.wb_en, (decode.io.wb_sel === WB_REG) || (decode.io.pc_sel === PC_UC), false.B)

    val execution = Module(new Execution(width))
    execution.io.alu_op := decode.io.alu_op
    execution.io.br_op := decode.io.br_op
    execution.io.in1 := MuxLookup(decode.io.rs1_sel, 0.U) (Seq(A_XXX -> (0.U), A_PC -> (fetch.io.pc), A_REG -> (rf.io.rs1_data)))
    execution.io.in2 := MuxLookup(decode.io.rs2_sel, 0.U) (Seq(B_XXX -> (0.U), B_IMM -> (decode.io.imm), B_REG -> (rf.io.rs2_data)))

    // Access Memory
    io.dmem.ren := (decode.io.ld_sel =/= LD_XXX)
    io.dmem.raddr := execution.io.calc_out
    val rdata = io.dmem.rdata
    val ld_data = MuxLookup(decode.io.ld_sel, 0.U) (
        Seq (
            LD_LB  -> (Cat(Fill(24, rdata(7)), rdata(7, 0))),
            LD_LH  -> (Cat(Fill(16, rdata(15)), rdata(15, 0))),
            LD_LW  -> (rdata),
            LD_LBU -> (Cat(Fill(24, 0.U), rdata(7, 0))),
            LD_LHU -> (Cat(Fill(16, 0.U), rdata(15, 0)))
        )
    )

    fetch.io.comp_out := execution.io.comp_out
    fetch.io.calc_out := Mux(decode.io.pc_sel === PC_BR, decode.io.imm + fetch.io.pc, execution.io.calc_out)

    rf.io.rd_data :=
        Mux(
            decode.io.ld_sel =/= LD_XXX,
            ld_data,
            Mux(
                decode.io.pc_sel === PC_UC,
                fetch.io.pc + 4.U,
                Mux(
                    decode.io.wb_sel === WB_REG,
                    execution.io.calc_out,
                    0.U
                )
            )
        )
    
    io.dmem.wen := (decode.io.wb_sel === WB_MEM)
    io.dmem.waddr := execution.io.calc_out
    io.dmem.wdata := rf.io.rs2_data
    io.dmem.wmask := MuxLookup(decode.io.st_sel, 0.U) (
        Seq(
            ST_SB -> ("h000000ff".U),
            ST_SH -> ("h0000ffff".U),
            ST_SW -> ("hffffffff".U)
        )
    )
}