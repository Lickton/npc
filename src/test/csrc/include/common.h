#ifndef __COMMON_H_
#define __COMMON_H_

#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <unistd.h>
#include <assert.h>
#include <config.h>
#include <utils.h>
#include <string.h>

#define ARRLEN(arr) (int)(sizeof(arr) / sizeof(arr[0]))

typedef uint32_t word_t;
typedef uint32_t paddr_t;
typedef word_t   vaddr_t;

#endif