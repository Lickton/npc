import "DPI-C" function void pmem_read(input int paddr, output int rword);

module ifetch (
    // input           clk,
    // input           rst,
    input  [31:0]   addr,

    output [31:0]   inst
);

always@(posedge clk) begin
    pmem_read(addr, inst);
end
    
endmodule //inatruction_fetch
