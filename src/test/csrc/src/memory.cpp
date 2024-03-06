#include <memory.h>
#include <utils.h>

/*----- Device Variables -----*/
/*--- UPTIME ---*/
static uint64_t boot_time;
/*--- SERIAL ---*/

static uint8_t pmem[CONFIG_MSIZE];

uint8_t* guest_to_host(paddr_t paddr) { return pmem + paddr - CONFIG_MBASE; }
paddr_t host_to_guest(uint8_t* haddr) { return haddr - pmem + CONFIG_MBASE; }

static word_t pmem_read(paddr_t addr, uint32_t mask) {
    word_t ret = host_read(guest_to_host(addr), mask);
    return ret;
}

static void pmem_write(paddr_t addr, uint32_t mask, word_t data) {
#ifdef WMEM_TRACE
    printf("Write memory addr %08x, %08x\n", addr, data & mask);
#endif
    host_write(guest_to_host(addr), mask, data);
}

static void out_of_bound(paddr_t addr) {
    Log("Addr %08x out of bound", addr);
    assert(0);
}

void init_mem() {
    memset(pmem, rand(), CONFIG_MSIZE);
    boot_time = get_time();
    // Log("Boot_time = %lu", boot_time);
    Log("physical memory area [0x%08x, 0x%08x]", PMEM_LEFT, PMEM_RIGHT);
}

extern "C" int  read_imem(bool ren, uint32_t addr) {
    if (!ren || addr == 0) return 0;
    if (!in_pmem(addr)) out_of_bound(addr);
    return pmem_read(addr, 0xffffffff);
}

extern "C" int  read_dmem(bool ren, uint32_t addr) {
    if (!ren) return 0;
    if (addr == RTC_ADDR || addr == RTC_ADDR + 4) {
#ifdef RMEM_TRACE
    printf("Read  memory addr %08x\n", addr);
#endif
        uint64_t curr_time = get_time();
        uint64_t uptime = curr_time - boot_time;
        // Log("up_time[0] = %u, up_time[1] = %u", (uint32_t)uptime, (uint32_t)(uptime >> 32));
        return (addr == RTC_ADDR ? (uint32_t)uptime : (uint32_t)(uptime >> 32));
    }
    if (!in_pmem(addr)) out_of_bound(addr);
    return pmem_read(addr, 0xffffffff);
}

extern "C" void write_dmem(uint32_t addr, uint32_t wmask, uint32_t wdata) {
    if (addr == SERIAL_PORT || addr == SERIAL_PORT + 4) {
        // printf("addr = %08x\n", addr);
        putchar(wdata & wmask);
        return;
    }
    if (!in_pmem(addr)) out_of_bound(addr);
    pmem_write(addr, wmask, wdata);
}