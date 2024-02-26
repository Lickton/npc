#include <memory.h>

void init_rand();
void init_mem();
void init_sdb();

static void welcome() {
    Log("Build time: %s, %s", __TIME__, __DATE__);
    printf("Welcome to npc\n");
}

static char *img_file = NULL;

static long load_img() {
    if (img_file == NULL) {
        Log("No image is given. Exit");
        return 0;
    }

    FILE *fp = fopen(img_file, "rb");
    assert(fp);

    fseek(fp, 0, SEEK_END);
    long size = ftell(fp);

    Log("This image is %s, size = %ld", img_file, size);

    fseek(fp, 0, SEEK_SET);
    int ret = fread(guest_to_host(CONFIG_MBASE), size, 1, fp);
    assert(ret == 1);

    fclose(fp);
    return size;
}

void init_monitor(int argc, char **argv) {
    init_rand();
    init_mem();

    img_file = argv[1];
    long img_size = load_img();

    welcome();
}