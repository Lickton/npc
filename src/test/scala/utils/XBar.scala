package utils

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class XBarTest extends AnyFlatSpec with ChiselScalatestTester with Matchers {
  behavior of "Addr2Dev"

  it should "divide to SRAM" in {
    test(new Addr2Dev(32)) { c =>
      c.io.addr.poke("h80000000".U)
      c.io.devID.expect("b00".U)
      c.io.addr.poke("h80000fff".U)
      c.io.devID.expect("b00".U)
    }
  }

  it should "divide to Accelerator" in {
    test(new Addr2Dev(32)) { c =>
      c.io.addr.poke("ha0000000".U)
      c.io.devID.expect("b01".U)
      c.io.addr.poke("ha0000fff".U)
      c.io.devID.expect("b01".U)
    }
  }

  behavior of "XBar"

  it should "work well in signal flow control" in {
    test(new XBar(32, 2)) { c =>
        c.in.read.arvalid.poke(true.B)
        c.in.read.araddr.poke("h80000000".U)
        c.in.read.rready.poke(true.B)
        c.out(0).read.rvalid.poke(true.B)
        c.out(0).read.rdata.poke("hffee00ff".U)
        c.out(0).read.rresp.poke(0.U)
        c.out(1).read.rvalid.poke(true.B)
        c.out(1).read.rdata.poke("heeff00ee".U)
        c.out(1).read.rresp.poke(0.U)
        c.in.read.rvalid.expect(true.B)
        c.in.read.rdata.expect("hffee00ff".U)
        c.in.read.rresp.expect(0.U)
    }
  }
}
