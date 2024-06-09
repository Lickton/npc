#ifndef __CONFIG_H__
#define __CONFIG_H__

/*----- Memory config -----*/
#define CONFIG_MBASE 0x80000000
#define CONFIG_MSIZE 0x8000000

/*----- Device config -----*/
#define DEVICE_ON
#define SERIAL_ON
#define DEVICE_BASE 0xa0000000
#define MMIO_BASE 0xa0000000
#define SERIAL_PORT     (DEVICE_BASE + 0x00003f8)
#define KDB_ADDR        (DEVICE_BASE + 0x0000060)
#define RTC_ADDR        (DEVICE_BASE + 0x0000048)
#define VGACTL_ADDR     (DEVICE_BASE + 0x0000100)
#define FB_ADDR         (MMIO_BASE   + 0x1000000)
#define FBCTL_ADDR      (MMIO_BASE   + 0x1100000)

/*----- Trace config -----*/
#define VCD_TRACE
// #define INST_TRACE
// #define RMEM_TRACE
// #define WMEM_TRACE

#endif