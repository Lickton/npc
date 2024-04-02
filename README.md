## RISC-V CPU - NPC

This repositories is for [ysyx project](https://ysyx.oscc.cc/docs/) and my graduation project.

## Details

- Write in Chisel
- Use verilator for simulation
- Support RV32E instruction Set
- Write a CrossBar for Multi-devices communication (1-in, N-out)
- Support AXI4 Lite protocol
- (TODO) Support for CNN accelerator
- (TODO) 5-stages pinpeline

### Project Structure

```
src
├── main
│   ├── resources
│   │   └── ram_2r1w.v            // Black Box memory access with DPI-C
│   └── scala
│       ├── device                // Devices
│       │   ├── accelerator
│       │   │   ├── CNNCore.scala
│       │   │   └── Conv.scala
│       │   ├── memory
│       │   │   ├── AXI_2r1w.scala
│       │   │   └── Ram_2r1w.scala
│       │   └── UART
│       ├── interface             // protocols
│       │   ├── AXI4Lite.scala
│       │   └── normal.scala
│       ├── npc                   // RISC-V Processing Core
│       │   ├── Core.scala
│       │   ├── Decode.scala
│       │   ├── Execution.scala
│       │   ├── InstFetch.scala
│       │   ├── Instructions.scala
│       │   ├── LoadStore.scala
│       │   └── RegFile.scala
│       ├── SimTop.scala          // Simulation Top, connect Core and devices
│       ├── TopMain.scala         // Generate Verilog Code, used by verilator
│       └── utils                 // useful tools
│           └── XBar.scala
└── test
    ├── csrc                      // C++ Files, for simulation
    │   ├── include
    │   │   ├── common.h          // Common include
    │   │   ├── config.h          // Configs and Trace Control, like VCD
    │   │   ├── cpu.h             // Call verilator api to run simulation
    │   │   ├── difftest.h        // Not completed so far
    │   │   ├── memory.h          // Memory access via DPI-C，memory related functions
    │   │   ├── reg.h             // Registers realted, print registers
    │   │   └── utils.h           // Debug macros, learning from nemu
    │   ├── main.cpp
    │   └── src
    │       ├── cpu.cpp            
    │       ├── memory.cpp
    │       ├── monitor
    │       │   ├── monitor.cpp   // Loading RV32E programs to run
    │       │   └── sdb.cpp       // Simple debugger, like single step and more
    │       ├── reg.cpp
    │       └── utils
    │           ├── state.cpp     // Definition for NPC's state
    │           └── timer.cpp     // Statistics of times
    └── scala                     // Chisel tests
        ├── device
        │   └── accelerator
        ├── npc
        └── utils
            └── XBar.scala
```

### XBar Design

Recive a address and decide which device will be transfered to, only access to data memory (from load stroe unit) is recived.
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

## Requirement

- sbt, scala build tool and scala
- normal build utils (make, g++)
- verilator version == 5.014 (from fedora's dnf)