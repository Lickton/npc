import chisel3._
import chisel3.util._
import Instructions._

object Control {
    val Y = true.B
    val N = false.B

    // PC select
    val PC_4  = 0.U(2.W) // normal + 4
    val PC_UC = 1.U(2.W) // from ALU calc_out and PC value to rd
    val PC_BR = 3.U(2.W) // from ALU calc_out and comp_out

    // in1 select
    val A_XXX = 0.U(2.W)
    val A_PC  = 1.U(2.W)
    val A_REG = 2.U(2.W)

    // in2 select
    val B_XXX = 0.U(2.W)
    val B_IMM = 1.U(2.W)
    val B_REG = 2.U(2.W)

    // Branch type
    val BR_XXX = 0.U(3.W)
    val BR_LTU = 1.U(3.W)
    val BR_LT  = 2.U(3.W)
    val BR_EQ  = 3.U(3.W)
    val BR_GEU = 4.U(3.W)
    val BR_GE  = 5.U(3.W)
    val BR_NE  = 6.U(3.W)

    // Store type
    val ST_XXX = 0.U(2.W)
    val ST_SW  = 1.U(2.W)
    val ST_SH  = 2.U(2.W)
    val ST_SB  = 3.U(2.W)

    // Load type
    val LD_XXX = 0.U(3.W)
    val LD_LW = 1.U(3.W)
    val LD_LH = 2.U(3.W)
    val LD_LB = 3.U(3.W)
    val LD_LHU = 4.U(3.W)
    val LD_LBU = 5.U(3.W)

    // Write back type
    val WB_XXX = 0.U(2.W)
    val WB_REG = 1.U(2.W)
    val WB_MEM = 2.U(2.W)
    val WB_PC  = 3.U(2.W)
}

class ControlSignals (width : Int) extends Bundle {
    val inst = Input(UInt(width.W))
    val pc_sel = Output(UInt(2.W))
    val rs1_sel = Output(UInt(2.W))
    val rs2_sel = Output(UInt(2.W))
    val rs1_addr = Output(UInt(5.W))
    val rs2_addr = Output(UInt(5.W))
    val rd_addr = Output(UInt(5.W))
    val imm  = Output(UInt(width.W))
    val alu_op = Output(UInt(4.W))
    val br_op = Output(UInt(3.W))
    val st_sel = Output(UInt(2.W))
    val ld_sel = Output(UInt(3.W))
    val wb_sel = Output(UInt(2.W))
    val wb_en = Output(Bool())
    val break = Output(Bool())
}

import Alu._
import Control._

class Decode (width : Int) extends Module {
    val io = IO(new ControlSignals(width))

    val inst = io.inst

    val immI = Cat(Fill(20, inst(31)), inst(31, 20))
    val immS = Cat(Fill(20, inst(31)), inst(31, 25), inst(11, 7))
    val immB = Cat(Fill(20, inst(31)), inst(7), inst(30, 25), inst(11, 8), 0.U(1.W))
    val immU = Cat(inst(31, 12), Fill(12, 0.U))
    val immJ = Cat(Fill(12, inst(31)), inst(31), inst(19, 12), inst(20), inst(30, 21), 0.U(1.W))

    io.rs1_addr := inst(19, 15)
    io.rs2_addr := inst(24, 20)
    io.rd_addr  := inst(11, 7)
    
    // +--------+---------+---------+-----+--------+-------+--------+--------+--------+-------+--------+
    // |   0    |    1    |    2    |  3  |   4    |   5   |   6    |   7    |   8    |   9   |   10   |
    // +--------+---------+---------+-----+--------+-------+--------+--------+--------+-------+--------+
    // | pc_sel | rs1_sel | rs2_sel | imm | alu_op | br_op | st_sel | ld_sel | wb_sel | wb_en | ebreak |
    // +--------+---------+---------+-----+--------+-------+--------+--------+--------+-------+--------+
    val default = List(PC_4, A_XXX, B_XXX, 0.U , ALU_XXX, BR_XXX, ST_XXX, LD_XXX, WB_XXX, N, Y)
    val map = Array(
        lui   -> List(PC_4 , A_XXX, B_IMM, immU, ALU_ADD, BR_XXX, ST_XXX, LD_XXX, WB_REG, Y, N),
        auipc -> List(PC_4 , A_PC , B_IMM, immU, ALU_ADD, BR_XXX, ST_XXX, LD_XXX, WB_REG, Y, N),
        jal   -> List(PC_UC, A_PC , B_IMM, immJ, ALU_ADD, BR_XXX, ST_XXX, LD_XXX, WB_PC , Y, N),
        jalr  -> List(PC_UC, A_REG, B_IMM, immI, ALU_ADD, BR_XXX, ST_XXX, LD_XXX, WB_PC , Y, N),
        beq   -> List(PC_BR, A_REG, B_REG, immB, ALU_XXX, BR_EQ , ST_XXX, LD_XXX, WB_PC , Y, N),
        bne   -> List(PC_BR, A_REG, B_REG, immB, ALU_XXX, BR_NE , ST_XXX, LD_XXX, WB_PC , Y, N),
        blt   -> List(PC_BR, A_REG, B_REG, immB, ALU_XXX, BR_LT , ST_XXX, LD_XXX, WB_PC , Y, N),
        bge   -> List(PC_BR, A_REG, B_REG, immB, ALU_XXX, BR_GE , ST_XXX, LD_XXX, WB_PC , Y, N),
        bltu  -> List(PC_BR, A_REG, B_REG, immB, ALU_XXX, BR_LTU, ST_XXX, LD_XXX, WB_PC , Y, N),
        bgeu  -> List(PC_BR, A_REG, B_REG, immB, ALU_XXX, BR_GEU, ST_XXX, LD_XXX, WB_PC , Y, N),
        lb    -> List(PC_4 , A_REG, B_IMM, immI, ALU_ADD, BR_XXX, ST_XXX, LD_LB , WB_REG, Y, N),
        lh    -> List(PC_4 , A_REG, B_IMM, immI, ALU_ADD, BR_XXX, ST_XXX, LD_LH , WB_REG, Y, N),
        lw    -> List(PC_4 , A_REG, B_IMM, immI, ALU_ADD, BR_XXX, ST_XXX, LD_LW , WB_REG, Y, N),
        lbu   -> List(PC_4 , A_REG, B_IMM, immI, ALU_ADD, BR_XXX, ST_XXX, LD_LBU, WB_REG, Y, N),
        lhu   -> List(PC_4 , A_REG, B_IMM, immI, ALU_ADD, BR_XXX, ST_XXX, LD_LHU, WB_REG, Y, N),
        sb    -> List(PC_4 , A_REG, B_IMM, immS, ALU_ADD, BR_XXX, ST_SB , LD_XXX, WB_MEM, Y, N),
        sh    -> List(PC_4 , A_REG, B_IMM, immS, ALU_ADD, BR_XXX, ST_SH , LD_XXX, WB_MEM, Y, N),
        sw    -> List(PC_4 , A_REG, B_IMM, immS, ALU_ADD, BR_XXX, ST_SW , LD_XXX, WB_MEM, Y, N),
        addi  -> List(PC_4 , A_REG, B_IMM, immI, ALU_ADD, BR_XXX, ST_XXX, LD_XXX, WB_REG, Y, N),
        slti  -> List(PC_4 , A_REG, B_IMM, immI, ALU_SLT, BR_XXX, ST_XXX, LD_XXX, WB_REG, Y, N),
        sltiu -> List(PC_4 , A_REG, B_IMM, immI, ALU_SLTU,BR_XXX, ST_XXX, LD_XXX, WB_REG, Y, N),
        xori  -> List(PC_4 , A_REG, B_IMM, immI, ALU_XOR, BR_XXX, ST_XXX, LD_XXX, WB_REG, Y, N),
        ori   -> List(PC_4 , A_REG, B_IMM, immI, ALU_OR , BR_XXX, ST_XXX, LD_XXX, WB_REG, Y, N),
        andi  -> List(PC_4 , A_REG, B_IMM, immI, ALU_AND, BR_XXX, ST_XXX, LD_XXX, WB_REG, Y, N),
        slli  -> List(PC_4 , A_REG, B_IMM, immI, ALU_SLL, BR_XXX, ST_XXX, LD_XXX, WB_REG, Y, N),
        srli  -> List(PC_4 , A_REG, B_IMM, immI, ALU_SRL, BR_XXX, ST_XXX, LD_XXX, WB_REG, Y, N),
        srai  -> List(PC_4 , A_REG, B_IMM, immI, ALU_SRA, BR_XXX, ST_XXX, LD_XXX, WB_REG, Y, N),
        add   -> List(PC_4 , A_REG, B_REG, 0.U , ALU_ADD, BR_XXX, ST_XXX, LD_XXX, WB_REG, Y, N),
        sub   -> List(PC_4 , A_REG, B_REG, 0.U , ALU_SUB, BR_XXX, ST_XXX, LD_XXX, WB_REG, Y, N),
        sll   -> List(PC_4 , A_REG, B_REG, 0.U , ALU_SLL, BR_XXX, ST_XXX, LD_XXX, WB_REG, Y, N),
        slt   -> List(PC_4 , A_REG, B_REG, 0.U , ALU_SLT, BR_XXX, ST_XXX, LD_XXX, WB_REG, Y, N),
        sltu  -> List(PC_4 , A_REG, B_REG, 0.U , ALU_SLTU,BR_XXX, ST_XXX, LD_XXX, WB_REG, Y, N),
        xor   -> List(PC_4 , A_REG, B_REG, 0.U , ALU_XOR, BR_XXX, ST_XXX, LD_XXX, WB_REG, Y, N),
        srl   -> List(PC_4 , A_REG, B_REG, 0.U , ALU_SRL, BR_XXX, ST_XXX, LD_XXX, WB_REG, Y, N),
        sra   -> List(PC_4 , A_REG, B_REG, 0.U , ALU_SRA, BR_XXX, ST_XXX, LD_XXX, WB_REG, Y, N),
        or    -> List(PC_4 , A_REG, B_REG, 0.U , ALU_OR , BR_XXX, ST_XXX, LD_XXX, WB_REG, Y, N),
        and   -> List(PC_4 , A_REG, B_REG, 0.U , ALU_AND, BR_XXX, ST_XXX, LD_XXX, WB_REG, Y, N),
        ebreak-> List(PC_4 , A_XXX, B_XXX, 0.U , ALU_XXX, BR_XXX, ST_XXX, LD_XXX, WB_XXX, N, Y)
    )
    val ctrlSignals = ListLookup(inst, default, map)

    io.pc_sel  := ctrlSignals(0)
    io.rs1_sel := ctrlSignals(1)
    io.rs2_sel := ctrlSignals(2)
    io.imm     := ctrlSignals(3)
    io.alu_op  := ctrlSignals(4)
    io.br_op   := ctrlSignals(5)
    io.st_sel  := ctrlSignals(6)
    io.ld_sel  := ctrlSignals(7)
    io.wb_sel  := ctrlSignals(8)
    io.wb_en   := ctrlSignals(9)
    io.break   := ctrlSignals(10)
}