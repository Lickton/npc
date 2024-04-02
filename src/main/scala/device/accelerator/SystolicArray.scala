package device.accelerator

import chisel3._
import chisel3.util._
import interface.Master_AXI4Lite

class PE(width: Int = 8) extends Module {
    val io = IO(new Bundle {
        val inAct  = Input(SInt(width.W))
        val inWt   = Input(SInt(width.W))
        
        val passAct = Output(SInt(width.W))
        val passWt  = Output(SInt(width.W))

        val outReady = Input(Bool())
        val preOut = Input(SInt((2 * width).W))
        val outAct = Output(SInt((2 * width).W))
    })

    val aReg = Reg(SInt(width.W))
    val wReg = Reg(SInt(width.W))
    val rReg = Reg(SInt((2 * width).W))

    aReg := io.inAct
    wReg := io.inWt
    io.passAct := aReg
    io.passWt  := wReg

    io.outAct := 0.S
    when (io.outReady) {
        io.outAct := rReg
        rReg := io.preOut
    } .otherwise {
        rReg := rReg + io.inAct * io.inWt
    }
}

class SystolicArray(width: Int = 8, meshRow: Int, meshCol: Int) extends Module {
    val io = IO(new Bundle {
        val inAct  = Input(Vec(meshCol, SInt(width.W)))
        val inWt   = Input(Vec(meshRow, SInt(width.W)))
        val outAct = Output(Vec(meshCol, SInt((2 * width).W)))
        val outValid = Input(Bool())
    })

    val PEs: Seq[Seq[PE]] = Seq.fill(meshRow, meshCol)(Module(new PE(width)))    

    for (row <- 0 until meshRow) {
        for (col <- 0 until meshCol) {
            PEs(row)(col).io.outReady := io.outValid

            if (row == 0) {
                PEs(row)(col).io.inAct := io.inAct(col)
                io.outAct(col) := PEs(row)(col).io.outAct
            } else {
                PEs(row)(col).io.inAct := PEs(row - 1)(col).io.passAct
            }
            
            if (col == 0) {
                PEs(row)(col).io.inWt := io.inWt(row)
            } else {
                PEs(row)(col).io.inWt := PEs(row)(col - 1).io.passWt
            }

            if (row != meshRow - 1) PEs(row)(col).io.preOut := PEs(row + 1)(col).io.outAct
            else                    PEs(row)(col).io.preOut := 0.S
        }
    }
}
