# 动态追踪技术(三)：Trace Your Functions !

Ftrace是一个设计用来帮助开发者和设计者监视内核的追踪器，可用于调试或分析延迟以及性能问题。ftrace令人印象最深刻的是作为一个function tracer，内核函数调用、耗时等情况一览无余。另外，ftrace最常见的用途是事件追踪，通过内核是成百上千的静态事件点，看到系统内核的哪些部分在运行。实际上，ftrace更是一个追踪框架，它具备丰富工具集：延迟跟踪检查、何时发生中断、任务的启用、禁用及抢占等。在ftrace的基线版本之上，还有很多第三方提供的开源工具，用于简化操作或者提供数据可视化等扩展应用。

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

![](http://p11slcnom.bkt.clouddn.com/DTrace_ftrace_1.png)

挂载之后会自动创建如下文件：

![](http://p11slcnom.bkt.clouddn.com/DTrace_ftrace_2.png)

## 三、BASIC

### 1. Function tracer

以Function tracer为例，结果存储在 **trace**，该文件类似一张报表，该表将显示 4 列信息。首先是进程信息，包括进程名和PID ；第二列是CPU；第三列是时间戳；第四列是函数信息，缺省情况下，这里将显示内核函数名以及它的上一层调用函数。

![](http://p11slcnom.bkt.clouddn.com/DTrace_ftrace_3.png)

### 2. Function graph tracer
Function graph tracer 和 function tracer 类似，但输出为函数调用图，更加容易阅读：

![](http://p11slcnom.bkt.clouddn.com/DTrace_ftrace_4.png)

![Linux ftrace tracers](http://og2061b3n.bkt.clouddn.com/DTrace_ftrace_tracers.png)

## 四、体系结构

Ftrace有两大组成部分，framework和一系列的tracer 。每个tracer完成不同的功能，它们统一由framework管理。 ftrace 的trace信息保存在ring buffer中，由framework负责管理。Framework 利用debugfs建立tracing目录，并提供了一系列的控制文件。

![Linux ftrace架构示意图](http://og2061b3n.bkt.clouddn.com/DTrace_ftrace_arch.png)

**ftrace is a dynamic tracing system.** 当你开始“ftracing”一个内核函数的时候，该函数的代码实际上就已经发生变化了。内核将在程序集中插入一些额外的指令，使得函数调用时可以随时通知追踪程序。

>WARNNING:使用ftrace追踪内核将有可能对系统性能产生影响，追踪的函数越多，开销越大。
使用者必须提前做好准备工作，生产环境必须谨慎使用。

![](http://p11slcnom.bkt.clouddn.com/DTrace_ftrace_5.png)

## 五、Useful Tools

### 1. trace-cmd
trace-cmd是一个非常有用的Ftrace命令行工具。

![](http://p11slcnom.bkt.clouddn.com/DTrace_ftrace_6.png)

在很有情况下不能使用函数追踪，需要依赖 **事件追踪** 的支持，例如：

![](http://p11slcnom.bkt.clouddn.com/DTrace_ftrace_7.png)

输出如下：

![](http://p11slcnom.bkt.clouddn.com/DTrace_ftrace_8.png)

切换路径：PID 24817 -> 15144 -> kernel -> 24817 -> 1561 -> 15114。


### 2. perf-tools
**perf-tools** 是性能调试大神Brendan Gregg开发的一个工具包，提供了很多强大的功能，例如：
iosnoop: 磁盘I/O分析详细包括延迟
iolatency: 磁盘I/O分析概要(柱状图)
execsnoop: 追踪进程exec()
opensnoop: 追踪open()系统调用，包含文件名
killsnoop: 追踪kill()信号（进程和信号详细）

代码下载：https://github.com/brendangregg/perf-tools

![](http://p11slcnom.bkt.clouddn.com/DTrace_ftrace_9.png)

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
