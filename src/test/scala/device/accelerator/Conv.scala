package device.accelerator

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ConvUnitTest extends AnyFlatSpec with ChiselScalatestTester with Matchers {
  behavior of "PE"

  it should "kernel 4x4" in {
    test(new PE(width = 8, kernSize = 2)) { dut =>
      var data = Seq(1.S, 1.S, 1.S, 1.S)
      dut.io.ready.poke(true.B)
      dut.io.activa.zip(data).map { case (activa, data) => activa.poke(data) }
      dut.clock.step()
	    data = Seq(2.S, 2.S, 2.S, 2.S)
      dut.io.kernel.zip(data).map { case (kernel, data) => kernel.poke(data) }
      dut.io.enable.poke(true.B)
      dut.clock.step()
      dut.io.out.expect(8.S)
    }
  }

  behavior of "Conv"
  it should "kernel 1x1, activation 1x1" in {
    var arrayNum = 1
    var flattenSize = 1
    test(new Conv(width = 8, kernSize = 1, arrayNum = 1)) { dut =>
      dut.io.data.activa.zipWithIndex.foreach { case (port, i) => port.poke(1.S) }

      for (i <- 0 until arrayNum) {
        dut.io.data.pe_sel.poke(i.U)
        dut.io.data.kernel.zipWithIndex.foreach { case (port, i) => port.poke(1.S) }
        dut.clock.step()
      }

      dut.io.data.enable.poke(true.B)
      dut.io.res.ready.poke(true.B)
      dut.io.res.bits.foreach { port => 
        port.expect(1.S)
      }
    }
  }

  it should "kernel 1x1, activation 2x2" in {
    var arrayNum = 4
    var flattenSize = 1
    test(new Conv(width = 8, kernSize = 1, arrayNum = 4)) { dut =>
      dut.io.data.kernel.zipWithIndex.foreach { case (port, i) => port.poke(1.S) }
      dut.io.data.activa.zipWithIndex.foreach { case (port, i) => port.poke(1.S) }

      for (i <- 0 until arrayNum) {
        println("Now poke " + i)
        dut.io.data.pe_sel.poke(i.U)
        dut.clock.step()
      }

      dut.io.data.enable.poke(true.B)
      dut.io.res.ready.poke(true.B)
      dut.io.res.valid.expect(true.B)
      dut.clock.step()
      dut.io.res.bits.foreach { port => 
        port.expect(1.S)
      }
    }
  }

  it should "kernel 3x3, activation 4x4" in {
    var arrayNum = 4
    var flattenSize = 9
    test(new Conv(width = 8, kernSize = 3, arrayNum = 4)) { dut =>
      dut.io.data.activa.zipWithIndex.foreach { case (port, i) => port.poke(1.S) }

      dut.io.data.kernel.zipWithIndex.foreach { case (port, i) => port.poke(1.S) }
      for (i <- 0 until arrayNum) {
        dut.io.data.pe_sel.poke(i.U)
        dut.clock.step(2)
      }

      dut.io.data.enable.poke(true.B)
      dut.io.res.ready.poke(true.B)
      dut.clock.step()
      dut.io.res.valid.expect(true.B)
      dut.io.res.bits.foreach { port => 
        port.expect(9.S)
      }
    }
  }
}