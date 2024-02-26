VERILATOR_ROOT = /usr/share/verilator/include

VSRC = $(BUILD_DIR)/vsrc/SimTop.v \
       $(BUILD_DIR)/vsrc/ram_2r1w.v

CSRC_DIR = src/test/csrc

LIB_STATIC = $(BUILD_DIR)/verilator/V$(TOP)__ALL.a

LIB_CPP = $(VERILATOR_ROOT)/verilated.cpp \
          $(VERILATOR_ROOT)/verilated_threads.cpp \
		  $(VERILATOR_ROOT)/verilated_vcd_c.cpp

CFLAGS = -I$(BUILD_DIR)/verilator \
         -I$(VERILATOR_ROOT) \
		 -I$(VERILATOR_ROOT)/vltstd \
		 -I$(CSRC_DIR)/include \
		 -lreadline

CPP_FILES = $(CSRC_DIR)/main.cpp \
			$(CSRC_DIR)/src/monitor/monitor.cpp \
			$(CSRC_DIR)/src/monitor/sdb.cpp \
			$(CSRC_DIR)/src/utils/timer.cpp \
			$(CSRC_DIR)/src/utils/state.cpp \
			$(CSRC_DIR)/src/cpu.cpp \
			$(CSRC_DIR)/src/memory.cpp
