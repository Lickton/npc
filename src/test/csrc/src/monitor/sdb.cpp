#include <cpu.h>
#include <readline/readline.h>
#include <readline/history.h>
#include <memory.h>

// static int is_batch_mode = false;
static int is_batch_mode = true;

bool is_number(char *arg) {
  int len = strlen(arg);
  for (int i = 0; i < len; i++) {
    if (arg[i] < '0' || arg[i] > '9')
      return false;
  }

  return true;
}

static char* rl_gets() {
  static char *line_read = NULL;

  if (line_read) {
    free(line_read);
    line_read = NULL;
  }

  line_read = readline("(npc) ");

  if (line_read && *line_read) {
    add_history(line_read);
  }

  return line_read;
}

static int cmd_c(char *args) {
  cpu_exec(-1);
  return 0;
}

static int cmd_q(char *args) {
  return -1;
}

static int cmd_help(char *args);
static int cmd_si(char *args);
// static int cmd_info(char *args);
// static int cmd_x(char *args);
// static int cmd_p(char *args);
// static int cmd_w(char *args);
// static int cmd_d(char *args);

static struct {
  const char *name;
  const char *description;
  int (*handler) (char *);
} cmd_table [] = {
  { "help", "Display information about all supported commands", cmd_help },
  { "c",    "Continue the execution of the program",            cmd_c },
  { "q",    "Exit NEMU",                                        cmd_q },
  { "si",   "si [N], defaule 1 step",                           cmd_si },
//   { "info", "info r/w, print all register or all watch point",  cmd_info },
//   { "x",    "x N EXPR, Scan the memory",                        cmd_x },
//   { "p",    "Give the value of expression",                     cmd_p },
//   { "w",    "Set a watching point",                             cmd_w },
//   { "d",    "Delete a wathcing point",                          cmd_d },
};

#define NR_CMD ARRLEN(cmd_table)

static int cmd_help(char *args) {
  /* extract the first argument */
  char *arg = strtok(NULL, " ");
  int i;

  if (arg == NULL) {
    /* no argument given */
    for (i = 0; i < NR_CMD; i ++) {
      printf("%s - %s\n", cmd_table[i].name, cmd_table[i].description);
    }
  }
  else {
    for (i = 0; i < NR_CMD; i ++) {
      if (strcmp(arg, cmd_table[i].name) == 0) {
        printf("%s - %s\n", cmd_table[i].name, cmd_table[i].description);
        return 0;
      }
    }
    printf("Unknown command '%s'\n", arg);
  }
  return 0;
}


static int cmd_si(char *args) {
  int step;
  char *arg = strtok(NULL, " ");

  if (arg == NULL) {
    step = 1;
  }
  else {
    step = atoi(arg);
    if (!is_number(arg)) {
      printf("Illegal input:\"%s\". Try \"help si\"", arg);
    }
  }

  cpu_exec(step);

  return 0;
}

void sdb_mainloop() {
    if (is_batch_mode) {
        cmd_c(NULL);
        return;
    }

    for (char *str; (str = rl_gets()) != NULL; ) {
    char *str_end = str + strlen(str);

    /* extract the first token as the command */
    char *cmd = strtok(str, " ");
    if (cmd == NULL) { continue; }

    /* treat the remaining string as the arguments,
     * which may need further parsing
     */
    char *args = cmd + strlen(cmd) + 1;
    if (args >= str_end) {
      args = NULL;
    }

    int i;
    for (i = 0; i < NR_CMD; i ++) {
      if (strcmp(cmd, cmd_table[i].name) == 0) {
        if (cmd_table[i].handler(args) < 0) { return; }
        break;
      }
    }

    if (i == NR_CMD) { printf("Unknown command '%s'\n", cmd); }
  }
}

