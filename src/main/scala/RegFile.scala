import chisel3._
import chisel3.util.experimental._

class RegFile (width : Int) extends Module {
    val io = IO(new Bundle {
        val rs1_addr = Input(UInt(5.W))
        val rs2_addr = Input(UInt(5.W))
        val rs1_data = Output(UInt(width.W))
        val rs2_data = Output(UInt(width.W))
        val rd_addr  = Input(UInt(5.W))
        val rd_data  = Input(UInt(width.W))
        val rd_en    = Input(Bool())
    })

    val rf = RegInit(VecInit(Seq.fill(32)(0.U(width.W))))

    when (io.rd_en && (io.rd_addr =/= 0.U)) {
        rf(io.rd_addr) := io.rd_data
    }

    io.rs1_data := Mux((io.rs1_addr === 0.U), 0.U, rf(io.rs1_addr))
    io.rs2_data := Mux((io.rs2_addr === 0.U), 0.U, rf(io.rs2_addr))
}