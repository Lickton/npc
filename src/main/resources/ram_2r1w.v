module ram_2r1w (
    input       clock,
    
    input       imem_ren,
    input [31:0]imem_raddr,
    output[31:0]imem_rdata,
    input       dmem_ren,
    input [31:0]dmem_raddr,
    output[31:0]dmem_rdata,
    input       dmem_wen,
    input [31:0]dmem_waddr,
    input [31:0]dmem_wstrb,
    input [31:0]dmem_wdata,
);
    
    import "DPI-C" function int  read_imem(input bit ren, input int addr);
    import "DPI-C" function int  read_dmem(input bit ren, input int addr);
    import "DPI-C" function void write_dmem(input int addr, input int wstrb, input int wdata);

    assign imem_rdata = read_imem(imem_ren, imem_raddr);
    assign dmem_rdata = read_dmem(dmem_ren, dmem_raddr);

    always @(posedge clock) begin
        if (dmem_wen) write_dmem(dmem_waddr, dmem_wstrb, dmem_wdata);
    end

endmodule //ram_2r1w
