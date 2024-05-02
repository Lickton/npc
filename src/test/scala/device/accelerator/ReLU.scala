package device.accelerator

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ReLUUnitTest extends AnyFlatSpec with ChiselScalatestTester with Matchers {
  behavior of "ReLU"

  // it should "pass through when not work" in {
  //   test(new ReLU(8, 4)) { dut =>
  //     var data = Seq(1.S, -1.S, 1.S, -1.S)
  //     dut.io.enable.poke(false.B)
  //     dut.io.in.zip(data).map { case (port, data) => port.poke(data) }
  //     dut.io.out.zip(data).map { case (port, data) => port.expect(data) }
  //   }
  // }

  // it should "select when not work" in {
  //   test(new ReLU(8, 4)) { dut =>
  //     var data = Seq(1.S, -1.S, 1.S, -1.S)
  //     dut.io.enable.poke(true.B)
  //     dut.io.in.zip(data).map { case (port, data) => port.poke(data) }
  //     data = Seq(1.S, 0.S, 1.S, 0.S)
  //     dut.io.out.zip(data).map { case (port, data) => port.expect(data) }
  //   }
  // }
}