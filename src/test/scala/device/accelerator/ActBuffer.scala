package device.accelerator

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import interface._

class BuffUnitTest extends AnyFlatSpec with ChiselScalatestTester with Matchers {
    behavior of "ActBuffer"

    it should "Save data" in {
        test(new ActBuffer(8, 3, 16, 0, 13 * 26)) { dut =>
            var data = Seq(
                1.U, 1.U, 1.U, 1.U,
                1.U, 1.U, 1.U, 1.U,
                1.U
            )
            dut.io.in.valid.poke(true.B)
            dut.io.fetch.poke(true.B)
            dut.io.in.bits.zip(data).map { case (port, data) => port.poke(data) }
            dut.clock.step(26)
            dut.io.fetch.poke(false.B)
            dut.io.out.ready.poke(true.B)
            dut.clock.step(40)
        }
    }
}