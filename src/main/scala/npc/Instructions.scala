package npc

import chisel3._
import chisel3.util._

object Instructions {
  /* RV32I Base Instruction Set */
  // U-Type
  def lui     = BitPat("b?????????????????????????0110111")
  def auipc   = BitPat("b?????????????????????????0010111")
  // J-type
  def jal     = BitPat("b?????????????????????????1101111")
  // B-Type
  def beq     = BitPat("b?????????????????000?????1100011")
  def bne     = BitPat("b?????????????????001?????1100011")
  def blt     = BitPat("b?????????????????100?????1100011")
  def bge     = BitPat("b?????????????????101?????1100011")
  def bltu    = BitPat("b?????????????????110?????1100011")
  def bgeu    = BitPat("b?????????????????111?????1100011")
  // S-type
  def sb      = BitPat("b?????????????????000?????0100011")
  def sh      = BitPat("b?????????????????001?????0100011")
  def sw      = BitPat("b?????????????????010?????0100011")
  // I-type
  def jalr    = BitPat("b?????????????????000?????1100111")
  def lb      = BitPat("b?????????????????000?????0000011")
  def lh      = BitPat("b?????????????????001?????0000011")
  def lw      = BitPat("b?????????????????010?????0000011")
  def lbu     = BitPat("b?????????????????100?????0000011")
  def lhu     = BitPat("b?????????????????101?????0000011")
  def addi    = BitPat("b?????????????????000?????0010011")
  def slti    = BitPat("b?????????????????010?????0010011")
  def sltiu   = BitPat("b?????????????????011?????0010011")
  def xori    = BitPat("b?????????????????100?????0010011")
  def ori     = BitPat("b?????????????????110?????0010011")
  def andi    = BitPat("b?????????????????111?????0010011")
  // SHMT-type
  def slli    = BitPat("b0000000??????????001?????0010011")
  def srli    = BitPat("b0000000??????????101?????0010011")
  def srai    = BitPat("b0100000??????????101?????0010011")
  // R-type
  def add     = BitPat("b0000000??????????000?????0110011")
  def sub     = BitPat("b0100000??????????000?????0110011")
  def sll     = BitPat("b0000000??????????001?????0110011")
  def slt     = BitPat("b0000000??????????010?????0110011")
  def sltu    = BitPat("b0000000??????????011?????0110011")
  def xor     = BitPat("b0000000??????????100?????0110011")
  def srl     = BitPat("b0000000??????????101?????0110011")
  def sra     = BitPat("b0100000??????????101?????0110011")
  def or      = BitPat("b0000000??????????110?????0110011")
  def and     = BitPat("b0000000??????????111?????0110011")
  def ebreak  = BitPat("b00000000000100000000000001110011")
}