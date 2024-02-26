#ifndef __MEMORY_H__
#define __MEMORY_H__

#include <common.h>

#define PMEM_LEFT  ((paddr_t)CONFIG_MBASE)
#define PMEM_RIGHT ((paddr_t)CONFIG_MBASE + CONFIG_MSIZE - 1)

static inline word_t host_read(void *addr, uint32_t mask) {
    switch (mask) {
        case 0x000000ff: return *(uint8_t  *)addr;
        case 0x0000ffff: return *(uint16_t *)addr;
        case 0xffffffff: return *(uint32_t *)addr;
        default: assert(0);
    }
}

static inline void host_write(void *addr, uint32_t mask, word_t data) {
    switch (mask) {
        case 0x000000ff: *(uint8_t  *)addr = data; return;
        case 0x0000ffff: *(uint16_t *)addr = data; return;
        case 0xffffffff: *(uint32_t *)addr = data; return;
        default: assert(0);
    }
}

uint8_t* guest_to_host(paddr_t paddr);
paddr_t  host_to_guest(uint8_t *haddr);

static inline bool in_pmem(paddr_t addr) {
    return addr - CONFIG_MBASE < CONFIG_MSIZE;
}

extern "C" int  read_imem(bool ren, uint32_t addr);
extern "C" int  read_dmem(bool ren, uint32_t addr);
extern "C" void write_dmem(uint32_t addr, uint32_t wmask, uint32_t wdata);

#endif