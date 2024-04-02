package device.accelerator

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class SystolicArrayTest extends AnyFlatSpec with ChiselScalatestTester with Matchers {
  behavior of "PE"

  it should "pass the in signal" in {
    test(new PE(8)) { c =>
      c.io.inAct.poke(1.S)
      c.io.inWt.poke(2.S)
      c.io.outReady.poke(false.B)
      c.io.preOut.poke(8.S)
      
      c.io.passAct.expect(0.S)
      c.io.passWt.expect(0.S)
      c.io.outAct.expect(0.S)
      
      c.clock.step()
      
      c.io.passAct.expect(1.S)
      c.io.passWt.expect(2.S)
      c.io.outAct.expect(0.S)
    }
  }

  it should "calculate the number" in {
    test(new PE(8)) { c =>
      c.io.inAct.poke(1.S)
      c.io.inWt.poke(2.S)
      c.io.outReady.poke(false.B)
      c.io.preOut.poke(10.S)

      c.clock.step()

      c.io.preOut.poke(5.S)
      c.io.outReady.poke(true.B)

      c.io.outAct.expect(2.S)

      c.clock.step()

      c.io.outAct.expect(5.S)
    }
  }

  behavior of "SystolicArray"

  it should "calculate 2x2 Matrix" in {
    test(new SystolicArray(8, 2, 2)) { c => 
      c.io.outValid.poke(false.B)

      val tap0 = Seq(1.S, 0.S)
      c.io.inWt.zip(tap0).map { case (inWt, tap) => inWt.poke(tap) }
      c.io.inAct.zip(tap0).map { case (inAct, tap) => inAct.poke(tap) }

      c.clock.step()

      val tap1 = Seq(2.S, 3.S)
      c.io.inWt.zip(tap1).map { case (inWt, tap) => inWt.poke(tap) }
      c.io.inAct.zip(tap1).map { case (inAct, tap) => inAct.poke(tap) }

      c.clock.step()

      val tap2 = Seq(0.S, 4.S)
      c.io.inWt.zip(tap2).map { case (inWt, tap) => inWt.poke(tap) }
      c.io.inAct.zip(tap2).map { case (inAct, tap) => inAct.poke(tap) }

      c.clock.step()

      c.clock.step()

      c.io.outValid.poke(true.B)

      c.io.outAct(0).expect(5.S)
      c.io.outAct(1).expect(11.S)

      c.clock.step()

      c.io.outAct(0).expect(11.S)
      c.io.outAct(1).expect(25.S)
    }
  }
}
