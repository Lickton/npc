TOP ?= SimTop
BUILD_DIR = ./build

VERILATOR := $(shell which verilator)
CXX = $(shell which g++)
SCALA := $(shell which sbt)

VERILATOR_FLAGS = --cc --trace --build -Mdir $(BUILD_DIR)/verilator 

include filelist.mk

LIB_OBJ := $(patsubst %.cpp,build/object/%.o,$(notdir $(LIB_CPP)))

all: verilog lib build

run:
	./$(BUILD_DIR)/$(TOP) $(NPCFLAGS)

build:
	$(CXX) $(CPP_FILES) $(LIB_STATIC) $(CFLAGS) -o $(BUILD_DIR)/$(TOP)

lib: $(LIB_OBJ)
	verilator $(VSRC) $(VERILATOR_FLAGS)
	ar rsc $(LIB_STATIC) $(LIB_OBJ)

build/object/%.o: /usr/share/verilator/include/%.cpp
	@$(info Target: $@)
	$(CXX) -c $< -o $@ $(CFLAGS)

verilog:
	@mkdir -p $(BUILD_DIR)
	@mkdir -p $(BUILD_DIR)/object
	sbt run

clean:
	-rm -rf $(BUILD_DIR)
	-rm -f *.vcd

.PHONY: verilog lib build clean