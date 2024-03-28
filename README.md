## RISC-V CPU for Learning

This repositories is for [ysyx project](https://ysyx.oscc.cc/docs/) and my graduation project.

## Details

- Write in Chisel
- Use verilator for simulation
- Support RV32E instruction Set
- Write a CrossBar for Multi-devices communication (1-in, N-out)
- Support AXI4 Lite protocol
- (TODO) Support for CNN accelerator
- (TODO) 5-stages pinpeline

### XBar Design

Recive a address and decide which device will be transfered
```
+--------+                     +--------+                     +--------+
|        | ----- arvalid ----> |        | ----- arvalid ----> |        |
|        | <---- arready ----- |        | <---- arready ----- |        |
|        | ----- araddr  ----> |        | ----- araddr  ----> |        |
| Master |                     |  XBar  |                     | Slavee |
|        | <---- rvalid  ----- |        | <---- rvalid  ----- |        |
|        | ----- rready  ----> |        | ----- rready  ----> |        |
|        | <---- rdata   ----- |        | <---- rdata   ----- |        |
|        | <---- rresp   ----- |        | <---- rresp   ----- |        |
|        |                     |        |                     |        |
|        | ----- awvalid ----> |        | ----- awvalid ----> |        |
|        | <---- awready ----- |        | <---- awready ----- |        |
|        | ----- awaddr  ----> |        | ----- awaddr  ----> |        |
|        |                     |        |                     |        |
|        | ----- wvalid  ----> |        | ----- wvalid  ----> |        |
|        | <---- wready  ----- |        | <---- wready  ----- |        |
| Master | ----- wdata   ----> |  XBar  | ----- wdata   ----> | Slavee |
|        | ----- wstrb   ----> |        | ----- wstrb   ----> |        |
|        |                     |        |                     |        |
|        | <---- bvalid  ----- |        | <---- bvalid  ----- |        |
|        | ----- bready  ----> |        | ----- bready  ----> |        |
|        | <---- bresp   ----- |        | <---- bresp   ----- |        |
+--------+                     +--------+                     +--------
```

## Statistics

```
github.com/AlDanial/cloc v 1.90  T=0.02 s (2084.0 files/s, 111463.3 lines/s)
-----------------------------------------------------------------------------------
Language                         files          blank        comment           code
-----------------------------------------------------------------------------------
Scala                               17            175            168            743
C++                                  8             79             27            377
C/C++ Header                         7             38             10            121
Verilog-SystemVerilog                1              5              0             22
-----------------------------------------------------------------------------------
SUM:                                33            297            205           1263
-----------------------------------------------------------------------------------
```

## Require

- sbt, scala build tool and scala
- normal build utils (make, g++)
- verilator version == 5.014 (from fedora's dnf)