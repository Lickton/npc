module ram_2r1w (
    input       clock,
    
    input       imem_ren,
    input [31:0]imem_raddr,
    output[31:0]imem_rdata
    // input       dmem_ren
    // input [31:0]dmem_raddr,
    // output[31:0]dmem_rdata,
    // input       dmem_wen,
    // input [31:0]dmem_waddr,
    // input [31:0]dmem_wmask,
    // input [31:0]dmem_wdata,
);
    
    import "DPI-C" function int  read_imem(input int addr);
    // import "DPI-C" function int  read_dmem(input int addr);
    // import "DPI-C" function void write_dmem(input int addr, input int wmask, input int wdata);

    reg [31:0] imem_rdata_reg;
    // reg [31:0] dmem_wdata_reg;

    always @(posedge clock) begin
        if (imem_ren) imem_rdata_reg <= read_imem(imem_raddr);
        // if (dmem_ren) dmem_rdata_reg <= read_dmem(dmem_raddr);
        // if (dmem_wen) write_dmem(dmem_waddr, dmem_wmask, dmem);
    end

    assign imem_rdata = imem_rdata_reg;
    // assign dmem_rdata = dmem_rdata_reg;

endmodule //ram_2r1w
