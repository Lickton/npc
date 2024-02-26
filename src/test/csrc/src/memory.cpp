#include <memory.h>

static uint8_t pmem[CONFIG_MSIZE];

uint8_t* guest_to_host(paddr_t paddr) { return pmem + paddr - CONFIG_MBASE; }
paddr_t host_to_guest(uint8_t* haddr) { return haddr - pmem + CONFIG_MBASE; }

static word_t pmem_read(paddr_t addr, uint32_t mask) {
    word_t ret = host_read(guest_to_host(addr), mask);
    return ret;
}

static void pmem_write(paddr_t addr, uint32_t mask, word_t data) {
    host_write(guest_to_host(addr), mask, data);
}

static void out_of_bound(paddr_t addr) {
    Log("Addr %08x out of bound", addr);
    assert(0);
}

void init_mem() {
    memset(pmem, rand(), CONFIG_MSIZE);
    Log("physical memory area [0x%08x, 0x%08x]", PMEM_LEFT, PMEM_RIGHT);
}

extern "C" int  read_imem(bool ren, uint32_t addr) {
    if (!ren || addr == 0) return 0;
    if (!in_pmem(addr)) out_of_bound(addr);
    return pmem_read(addr, 0xffffffff);
}

extern "C" int  read_dmem(bool ren, uint32_t addr) {
    if (!ren) return 0;
    if (!in_pmem(addr)) out_of_bound(addr);
    return pmem_read(addr, 0xffffffff);
}

extern "C" void write_dmem(uint32_t addr, uint32_t wmask, uint32_t wdata) {
    if (!in_pmem(addr)) out_of_bound(addr);
    pmem_write(addr, wmask, wdata);
}