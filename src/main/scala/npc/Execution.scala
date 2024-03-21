package npc

import chisel3._
import chisel3.util._

object Alu {
    val ALU_XXX = 0.U(4.W)
    val ALU_ADD = 1.U(4.W)
    val ALU_SUB = 2.U(4.W)
    val ALU_AND = 3.U(4.W)
    val ALU_OR  = 4.U(4.W)
    val ALU_XOR = 5.U(4.W)
    val ALU_SLT = 6.U(4.W)
    val ALU_SLL = 7.U(4.W)
    val ALU_SLTU= 8.U(4.W)
    val ALU_SRL = 9.U(4.W)
    val ALU_SRA = 10.U(4.W)
}

import Alu._
import Control._

class Execution (width : Int) extends Module {
    val io = IO(new Bundle {
        val alu_op = Input(UInt(4.W))
        val br_op = Input(UInt(3.W))
        val in1 = Input(UInt(width.W))
        val in2 = Input(UInt(width.W))
        val calc_out = Output(UInt(width.W))
        val comp_out = Output(Bool())
    })

    val in1 = io.in1
    val in2 = io.in2
    val shamt = in2(4, 0).asUInt
    val calc_out = WireInit(UInt(width.W), 0.U)
    val alu_op = io.alu_op
    val comp_out = WireInit(Bool(), true.B)

    calc_out := MuxLookup(io.alu_op, 0.U) (
        Seq (
            ALU_ADD -> (in1 + in2),
            ALU_SUB -> (in1 - in2),
            ALU_SRA -> (in1.asSInt >> shamt).asUInt,
            ALU_SRL -> (in1 >> shamt),
            ALU_SLL -> (in1 << shamt),
            ALU_SLT -> (in1.asSInt < in2.asSInt).asUInt,
            ALU_SLTU-> (in1 < in2).asUInt,
            ALU_AND -> (in1 & in2),
            ALU_OR  -> (in1 | in2),
            ALU_XOR -> (in1 ^ in2)
        )
    )

    comp_out := MuxLookup(io.br_op, true.B) (
        Seq (
            BR_EQ  -> (in1 === in2),
            BR_NE  -> (in1 =/= in2),
            BR_LT  -> (in1.asSInt <  in2.asSInt),
            BR_GE  -> (in1.asSInt >= in2.asSInt),
            BR_LTU -> (in1 <  in2),
            BR_GEU -> (in1 >= in2)
        )
    )

    io.calc_out := calc_out
    io.comp_out := comp_out
}