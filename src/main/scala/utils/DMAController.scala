package utils

import chisel3._
import chisel3.util._

import interface._

class DMAController(xlen: Int, readWidth: Int, writeWidth: Int) extends Module {
    val io = IO(new Bundle {
        val read = new DMARead(xlen, readWidth)
        val write = new DMAWrite(xlen, writeWidth)
    })
}