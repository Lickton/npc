#ifndef __REG_H__
#define __REG_H__

#include <common.h>
#include <VSimTop.h>
#include <VSimTop___024root.h>

typedef struct {
    word_t gpr[32];
    word_t pc;
} CPU_STATE;

void reg_display();

#endif