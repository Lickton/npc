package config

import chisel3._
import chisel3.util._

object Configs {
    // Register
    val REG_WIDRH  = 5

    // Address
    val ADDR_WIDTH      = 32
    val ADDR_BYTE_WIDTH = ADDR_WIDTH / 8

    // Date
    val DATA_WIDTH      = 32
    val DATA_WIDTH_H    = 16
    val DATA_WIDTH_B    = 8

    // Instructions
    val INST_WIDTH      = 32
    val INST_BYTE_WIDTH = INST_WIDTH / 8

    // program
    val START_ADDR      = "h80000000"
}

// object Opcode extends ChiselEnum {
//     val load  = Value(0x03.U) // i "load"  -> 000_0011
//     val imm   = Value(0x13.U) // i "imm"   -> 001_0011
//     val auipc = Value(0x17.U) // u "auipc" -> 001_0111
//     val store = Value(0x23.U) // s "store" -> 010_0011
//     val reg   = Value(0x33.U) // r "reg"   -> 011_0011
//     val lui   = Value(0x37.U) // u "lui"   -> 011_0111
//     val br    = Value(0x63.U) // b "br"    -> 110_0011
//     val jalr  = Value(0x67.U) // i "jalr"  -> 110_0111
//     val jal   = Value(0x6F.U) // j "jal"   -> 110_1111
// }

object Func extends ChiselEnum {
    val NOP, ADD, SUB, OR, AND, XOR, LSHIFT, RSHIFT = Value
}