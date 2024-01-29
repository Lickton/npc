package npc

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

// AnyFlatSpec is the default and recommended ScalaTest style for unit testing.
// ChiselScalatestTester provides testdriver functionality and integration (like 
//        signal value assertions) within the context of a ScalaTest environment.
class BasicTest extends AnyFlatSpec with ChiselScalatestTester {
    behavior of "passThrough"
    
    // test 1
    it should "pass through 0" in {
        test(new passThrough) { c =>
            c.io.in.poke(0.U)
            c.clock.step()
            c.io.out.expect(0.U)
        }
    }

    // test 2
    it should "pass all 4bit number" in {
        test(new passThrough) { c => 
            for (i <- 0 until 16) {
                c.io.in.poke(i.U)
                c.clock.step()
                c.io.out.expect(i.U)
            }
        }
    }
}
