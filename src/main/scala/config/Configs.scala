package config

import chisel3._

object Configs {
    // Register
    val REGISTER_NUM    = 32
    val REGISTER_WIDRH  = 5

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