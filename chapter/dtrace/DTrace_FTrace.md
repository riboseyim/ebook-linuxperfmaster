# 动态追踪技术(三)：Linux Ftrace

Ftrace是一个设计用来帮助开发者和设计者监视内核的追踪器，可用于调试或分析延迟以及性能问题。ftrace令人印象最深刻的是作为一个function tracer，内核函数调用、耗时等情况一览无余。另外，ftrace最常见的用途是事件追踪，通过内核是成百上千的静态事件点，看到系统内核的哪些部分在运行。实际上，ftrace更是一个追踪框架，它具备丰富工具集：延迟跟踪检查、何时发生中断、任务的启用、禁用及抢占等。在ftrace的基线版本之上，还有很多第三方提供的开源工具，用于简化操作或者提供数据可视化等扩展应用。

<!--more-->

## 一、Introduction

Developer(s):	Steven Rostedt(RedHat) and others
Initial release: October 9, 2008;
Operating system:	Linux （merged into the Linux kernel mainline in kernel version 2.6.27）
Type:	Kernel extension
License: GNU GPL
Website: [www.kernel.org/doc/Documentation/trace](www.kernel.org/doc/Documentation/trace)

## 二、ABC

在使用ftrace之前，需要确认调试目录是否已经挂载，默认目录：**/sys/kernel/debug/** 。

debugfs是Linux内核中一种特殊的文件系统，非常易用、基于RAM，专门设计用于调试。（since version 2.6.10-rc3，https://en.wikipedia.org/wiki/Debugfs)。

```bash
mount -t debugfs none /sys/kernel/debug
或者指定到自己的目录
mkdir /debug
mount -t debugfs nodev /debug
```

挂载之后会自动创建如下文件：
```bash
/sys/kernel/debug# ls -lrt
drwxr-xr-x.  2 root root 0 12月 28 17:24 x86
drwxr-xr-x.  3 root root 0 12月 28 17:24 boot_params
drwxr-xr-x. 34 root root 0 12月 28 17:24 bdi
-r--r--r--.  1 root root 0 12月 28 17:24 gpio
drwxr-xr-x.  3 root root 0 12月 28 17:24 usb
drwxr-xr-x.  4 root root 0 12月 28 17:24 xen
drwxr-xr-x.  6 root root 0 12月 28 17:24 tracing
drwxr-xr-x.  2 root root 0 12月 28 17:24 extfrag
drwxr-xr-x.  2 root root 0 12月 28 17:24 dynamic_debug
drwxr-xr-x.  2 root root 0 12月 28 17:24 hid
-rw-r--r--.  1 root root 0 12月 28 17:24 sched_features
drwxr-xr-x.  2 root root 0 12月 28 17:24 mce
drwxr-xr-x.  2 root root 0 12月 28 17:24 kprobes
-r--r--r--.  1 root root 0 12月 28 17:24 vmmemctl
/sys/kernel/debug/tracing# ls -lrt
-rw-r--r--.  1 root root 0 12月 28 17:24 tracing_thresh
-rw-r--r--.  1 root root 0 12月 28 17:24 tracing_on
-rw-r--r--.  1 root root 0 12月 28 17:24 tracing_max_latency
-rw-r--r--.  1 root root 0 12月 28 17:24 tracing_enabled
-rw-r--r--.  1 root root 0 12月 28 17:24 tracing_cpumask
drwxr-xr-x.  2 root root 0 12月 28 17:24 trace_stat
-r--r--r--.  1 root root 0 12月 28 17:24 trace_pipe
-rw-r--r--.  1 root root 0 12月 28 17:24 trace_options
--w--w----.  1 root root 0 12月 28 17:24 trace_marker
-rw-r--r--.  1 root root 0 12月 28 17:24 trace_clock
-rw-r--r--.  1 root root 0 12月 28 17:24 trace
-rw-r--r--.  1 root root 0 12月 28 17:24 sysprof_sample_period
-r--r--r--.  1 root root 0 12月 28 17:24 set_graph_function
-rw-r--r--.  1 root root 0 12月 28 17:24 set_ftrace_pid
-rw-r--r--.  1 root root 0 12月 28 17:24 set_ftrace_notrace
-r--r--r--.  1 root root 0 12月 28 17:24 saved_cmdlines
-r--r--r--.  1 root root 0 12月 28 17:24 README
drwxr-xr-x.  2 root root 0 12月 28 17:24 options
-rw-r--r--.  1 root root 0 12月 28 17:24 function_profile_enabled
-r--r--r--.  1 root root 0 12月 28 17:24 dyn_ftrace_total_info
-rw-r--r--.  1 root root 0 12月 28 17:24 buffer_size_kb
-r--r--r--.  1 root root 0 12月 28 17:24 available_tracers
-r--r--r--.  1 root root 0 12月 28 17:24 available_filter_functions
-rw-r--r--.  1 root root 0 12月 28 17:24 set_event
-r--r--r--.  1 root root 0 12月 28 17:24 printk_formats
drwxr-xr-x. 34 root root 0 12月 28 17:24 per_cpu
drwxr-xr-x. 24 root root 0 12月 28 17:24 events
-r--r--r--.  1 root root 0 12月 28 17:24 available_events
-r--r--r--.  1 root root 0 12月 28 17:24 kprobe_profile
-rw-r--r--.  1 root root 0 12月 28 17:24 kprobe_events
-r--r--r--.  1 root root 0 12月 28 17:24 stack_trace
-rw-r--r--.  1 root root 0 12月 28 17:24 stack_max_size
-rw-r--r--.  1 root root 0 5月  31 11:50 current_tracer
-rwxr-xr-x.  1 root root 0 5月  31 11:57 set_ftrace_filter
```

## 三、BASIC

### 1. Function tracer

以Function tracer为例，结果存储在 **trace**，该文件类似一张报表，该表将显示 4 列信息。首先是进程信息，包括进程名和PID ；第二列是CPU；第三列是时间戳；第四列是函数信息，缺省情况下，这里将显示内核函数名以及它的上一层调用函数。

```go
cd /sys/kernel/debug/tracing
echo function > current_tracer
cat trace

# tracer: function
#
#  TASK-PID   CPU#  TIMESTAMP        FUNCTION
#   |  |       |  
gmond-6684  [004] 13285965.088308: _spin_lock <-hrtimer_interrupt
gmond-6684  [004] 13285965.088308: ktime_get_update_offsets <-hrtimer_interrupt
gmond-6684  [004] 13285965.088309: __run_hrtimer <-hrtimer_interrupt
gmond-6684  [004] 13285965.088309: __remove_hrtimer <-__run_hrtimer
gmond-6684  [004] 13285965.088309: tick_sched_timer <-__run_hrtimer
gmond-6684  [004] 13285965.088309: ktime_get <-tick_sched_timer
gmond-6684  [004] 13285965.088310: tick_do_update_jiffies64 <-tick_sched_timer
gmond-6684  [004] 13285965.088310: update_process_times <-tick_sched_timer
```

### 2. Function graph tracer
Function graph tracer 和 function tracer 类似，但输出为函数调用图，更加容易阅读：
```go
# tracer: function_graph
#
#     TIME        CPU  DURATION                  FUNCTION CALLS
#      |          |     |   |                     |   |   |   |
 21)   ==========> |
 21)               |  smp_apic_timer_interrupt() {
 31)   ==========> |
 31)               |  smp_apic_timer_interrupt() {
 8)               |  smp_apic_timer_interrupt() {
 11)   2.598 us    |    native_apic_mem_write();
 18)   3.106 us    |    native_apic_mem_write();
 30)   ==========> |
 30)               |  smp_apic_timer_interrupt() {
  3)   3.590 us    |    native_apic_mem_write();
 22)   2.944 us    |    native_apic_mem_write();
  7)   3.392 us    |    native_apic_mem_write();
 17)   ==========> |
 17)               |  smp_apic_timer_interrupt() {
 27)   ==========> |
 27)               |  smp_apic_timer_interrupt() {
 16)   ==========> |
 16)               |  smp_apic_timer_interrupt() {
```

![Linux ftrace tracers](http://og2061b3n.bkt.clouddn.com/DTrace_ftrace_tracers.png)

## 四、体系结构

Ftrace有两大组成部分，framework和一系列的tracer 。每个tracer完成不同的功能，它们统一由framework管理。 ftrace 的trace信息保存在ring buffer中，由framework负责管理。Framework 利用debugfs建立tracing目录，并提供了一系列的控制文件。

![Linux ftrace架构示意图](http://og2061b3n.bkt.clouddn.com/DTrace_ftrace_arch.png)

**ftrace is a dynamic tracing system.** 当你开始“ftracing”一个内核函数的时候，该函数的代码实际上就已经发生变化了。内核将在程序集中插入一些额外的指令，使得函数调用时可以随时通知追踪程序。

>WARNNING:使用ftrace追踪内核将有可能对系统性能产生影响，追踪的函数越多，开销越大。
使用者必须提前做好准备工作，生产环境必须谨慎使用。

```bash
#cat available_tracers //查看支持的tracers
blk kmemtrace function_graph wakeup_rt wakeup function sysprof sched_switch initcall nop
```

## 五、Useful Tools

### 1. trace-cmd
trace-cmd是一个非常有用的Ftrace命令行工具。
```bash
sudo apt-get install trace-cmd
或者
git clone git://git.kernel.org/pub/scm/linux/kernel/git/rostedt/trace-cmd.git
```
使用方法：
```bash

sudo trace-cmd record --help #help
sudo trace-cmd record -p function -P 123456 #record for PID
sudo trace-cmd record -p function -l do_page_fault #record for function
  plugin 'function'
Hit Ctrl^C to stop recording
```
trace.dat
```go
$ sudo trace-cmd report
          chrome-15144 [000] 11446.466121: function:             do_page_fault
          chrome-15144 [000] 11446.467910: function:             do_page_fault
          chrome-15144 [000] 11446.469174: function:             do_page_fault
          chrome-15144 [000] 11446.474225: function:             do_page_fault
          chrome-15144 [000] 11446.474386: function:             do_page_fault
          chrome-15144 [000] 11446.478768: function:             do_page_fault
 CompositorTileW-15154 [001] 11446.480172: function:             do_page_fault
          chrome-1830  [003] 11446.486696: function:             do_page_fault
 CompositorTileW-15154 [001] 11446.488983: function:             do_page_fault
 CompositorTileW-15154 [001] 11446.489034: function:             do_page_fault
 CompositorTileW-15154 [001] 11446.489045: function:             do_page_fault
```

在很有情况下不能使用函数追踪，需要依赖 **事件追踪** 的支持，例如：

```bash
# cat available_events  //查看支持的事件类型
power:power_start
power:power_frequency
power:power_end

sched:sched_kthread_stop
sched:sched_kthread_stop_ret
sched:sched_wait_task
sched:sched_wakeup
sched:sched_wakeup_new
sched:sched_switch
sched:sched_migrate_task
sched:sched_process_free
sched:sched_process_exit
sched:sched_process_wait
sched:sched_process_fork
sched:sched_stat_wait
sched:sched_stat_sleep
sched:sched_stat_iowait
sched:sched_stat_runtime

sudo trace-cmd record -e sched:sched_switch
sudo trace-cmd report
```
输出如下：
```bash
 16169.624862:   Chrome_ChildIOT:24817 [112] S ==> chrome:15144 [120]
 16169.624992:   chrome:15144 [120] S ==> swapper/3:0 [120]
 16169.625202:   swapper/3:0 [120] R ==> Chrome_ChildIOT:24817 [112]
 16169.625251:   Chrome_ChildIOT:24817 [112] R ==> chrome:1561 [112]
 16169.625437:   chrome:1561 [112] S ==> chrome:15144 [120]
 ```
切换路径：PID 24817 -> 15144 -> kernel -> 24817 -> 1561 -> 15114。


### 2. perf-tools
**perf-tools** 是性能调试大神Brendan Gregg开发的一个工具包，提供了很多强大的功能，例如：
iosnoop: 磁盘I/O分析详细包括延迟
iolatency: 磁盘I/O分析概要(柱状图)
execsnoop: 追踪进程exec()
opensnoop: 追踪open()系统调用，包含文件名
killsnoop: 追踪kill()信号（进程和信号详细）

代码下载：https://github.com/brendangregg/perf-tools

```bash
# ./execsnoop  //显示新进程和参数：
Tracing exec()s. Ctrl-C to end.
   PID   PPID ARGS
 22898  22004 man ls
 22905  22898 preconv -e UTF-8
 22908  22898 pager -s
 22907  22898 nroff -mandoc -rLL=164n -rLT=164n -Tutf8
 22906  22898 tbl
 22911  22910 locale charmap
 22912  22907 groff -mtty-char -Tutf8 -mandoc -rLL=164n -rLT=164n
 22913  22912 troff -mtty-char -mandoc -rLL=164n -rLT=164n -Tutf8
 22914  22912 grotty


# ./iolatency -Q  //测量设备I/O延迟
Tracing block I/O. Output every 1 seconds. Ctrl-C to end.
  >=(ms) .. <(ms)   : I/O      |Distribution                          |
       0 -> 1       : 1913     |######################################|
       1 -> 2       : 438      |#########                             |
       2 -> 4       : 100      |##                                    |
       4 -> 8       : 145      |###                                   |
       8 -> 16      : 43       |#                                     |
      16 -> 32      : 43       |#                                     |
      32 -> 64      : 1        |#                                     |
[...]
```

## 六、可视化工具:KernelShark
KernelShark是trace-cmd的前端工具，提供了对trace.dat的可视化分析（Graph View 、List View、Simple and Advance filtering）。
![Linux ftrace KernelShark](https://static.lwn.net/images/2011/ks-success-zoom-task.png)

![Linux ftrace KernelShark Filters](http://og2061b3n.bkt.clouddn.com/DTrace_ftrace_KernelShark_Filters.png)

[>>>more about DTrace>>>>](http://riboseyim.github.io/2016/11/26/DTrace/)

## 参考文献
- [(Key)Julia Evans:ftrace: trace your kernel functions!](https://jvns.ca/blog/2017/03/19/getting-started-with-ftrace/)
- [(Key)IBM developerWorks@刘明：ftrace简介,2009](https://www.ibm.com/developerworks/cn/linux/l-cn-ftrace/)
- [Debugging the kernel using Ftrace - part 1 (Dec 2009, Steven Rostedt)](https://lwn.net/Articles/365835/)
- [Debugging the kernel using Ftrace - part 2 (Dec 2009, Steven Rostedt](https://lwn.net/Articles/366796/)
- [Secrets of the Linux function tracer (Jan 2010, Steven Rostedt)](https://lwn.net/Articles/370423/)
- [trace-cmd: A front-end for Ftrace (Oct 2010, Steven Rostedt)](https://lwn.net/Articles/410200/)
- [Using KernelShark to analyze the real-time scheduler (2011, Steven Rostedt)](https://lwn.net/Articles/425583/)
- [Ftrace: The hidden light switch (2014, Brendan Gregg)](https://lwn.net/Articles/608497/)
- [(Key)the kernel documentation:ftrace.txt](https://www.kernel.org/doc/Documentation/trace/ftrace.txt)
- [documentation on events you can trace Documentation/events.txt]()
- [some docs on ftrace design for linux kernel devs (not as useful, but interesting)]()
- [Documentation/ftrace-design.txt]()
- [trace-cmd图形化工具：KernelShark](http://rostedt.homelinux.com/kernelshark/)
- [Youtube:ELC2011 Ftrace GUI (KernelShark)](https://www.youtube.com/watch?v=ABRtzVtUVBo)
- [(Key)StevenRostedt:ELC2011_KernelShark(quick tutorial)(PDF)](http://elinux.org/images/6/64/Elc2011_rostedt.pdf)
