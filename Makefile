MODULE = $(basename $(notdir $(shell find csrc/. -name ".cpp")))

COLOR_RED   = \033[31m
COLOR_GREEN = \033[32m
COLOR_NONE  = \033[0m

VERILOG_DIR  = $(NPC_HOME)/vsrc
SIM_CPP_DIR  = $(NPC_HOME)/csrc
OUTPUT_DIR   = $(NPC_HOME)/obj_dir

V_MODULE      = $(addprefix V, $(MODULE))

VERILOG_FILE = $(NPC_HOME)/vsrc/$(MODULE).v
SIM_CPP_FILE = $(NPC_HOME)/csrc/$(MODULE).cpp

SIM_CC = verilator
SCALA_CC = sbt

CFLAGS = --cc
CFLAGS += --trace
CFLAGS += --exe
CFLAGS += --build
CFLAGS += -j 4

verilog:
	$(SCALA_CC) run

sim: $(SIM_CPP_DIR)/$(MODULE).cpp $(VERILOG_DIR)/$(MODULE).v
	@printf "\n$(COLOR_GREEN)===START Simulation with wave===$(COLOR_NONE)\n\n"
	$(SIM_CC) $(CFLAGS) $(VERILOG_FILE) $(SIM_CPP_FILE)
	$(OUTPUT_DIR)/$(V_MODULE)
	$(shell git add .)
	$(shell git commit -m "sim $(MODULE) in RTL")
	gtkwave $(OUTPUT_DIR)/$(MODULE).vcd

all: verilog sim
	gtkwave $(WAVE_FILE)

clean:
	@rm -f $(VERILOG_DIR)/*
	@rm -rf $(OUTPUT_DIR)

clean_test:
	@rm -rf test_run_dir
