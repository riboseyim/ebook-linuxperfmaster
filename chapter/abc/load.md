# Linux 性能诊断:单机负载评估

#### 负载诊断流程
- 观察load average (平均负载)
- 观察CPU、I/O是否存在瓶颈

从load avgerage等总括性的数据着手，参考CPU使用率和I/O等待时间等具体的数字，从而自顶向下快速排查各进程状态。

![perf-flow](http://upload-images.jianshu.io/upload_images/1037849-acc425a500206316.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


## 概念：什么是负载?

负载可以分为两大部分：CPU负载、I/O 负载 。

#### load average

```C
uptime
top
cat /proc/loadavg
```

```C
load average:0.65, 1.49, 1.76  （负载很低）
load average:3.49, 3.67, 3.75  （负载一般）
load average:33.20, 18.39, 15.21 (负载高)
load average:70.25, 80.50, 95.38 (负载非常高，需要干预)

load average:7.89, 11.42, 13.42  (当前负载趋于下降)
load average:17.89, 13.28, 4.45 (当前负载趋于上升)
```

依次时过去1分钟，5分钟，15分钟内，单位时间的等待任务数，也就是表示平均有多少任务正处于等待状态。在load average较高的情况下，就说明等待运行的任务较多，因此轮到该任务运行的等待时间就会出现较大延迟，即反映了此时负载较高。

#### linux内核的进程调度器（Process Scheduler）
负责决定任务运行的优先级，以及让任务等待或使之重新开始等核心工作。调度器划分并管理进程（Process）的状态。例如：

**等待分配CPU资源的状态**

**等待磁盘输入输出完毕的状态**

![process-flow.png](http://upload-images.jianshu.io/upload_images/1037849-dd82c356bdda926d.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

**进程描述符的状态**

|**进程状态**|PS Stat|***说明***|
|------|-----------------|
|TASK_RUNNING|R(Run)|运行状态。只要CPU空闲，随时都可以开始。|
|TASK_INTERRUPTIBLE|S(Sleep)|可中断的等待状态。例如系统睡眠或来自于用户输入的等待等。|
|TASK_UNINTERRUPTIBLE|D(Disk Sleep)|不可中断的等待状态。主要为短时间恢复时的等待状态。例如磁盘输入输出的等待|
|TASK_STOPPED||响应暂停信号而运行中断的状态。直到恢复（Resume）前都不会被调度|
|TASK_ZOMBIE|Z(Zombie)|僵死状态。虽然子进程已经终止（exit）,但父进程尚未执行wait(),因此该进程的资源没有被系统释放|

- TASK_RUNNING正在运行
- TASK_RUNNING （等待状态，加权换算）
- TASK_INTERRUPTIBLE（等待状态，加权换算）
- TASK_UNINTERRUPTIBLE（等待状态，不加权换算）

load average 表示“等待进程的平均数”，除了“TASK\_RUNNING正在运行”，其它三个都是等待状态。TASK\_INTERRUPTIBLE不被换算。即只换算“虽然需要即刻运行处理，但是无论如何都必须等待”。

load average所描述的负载就是：需要运行处理，但又必需等待队列前的进程处理完成的进程个数。具体来说：要么等待授予CPU运行权限，要么等待磁盘I/O完成。

- 内核函数：kernei/timer.c的calc-load();
- 调用周期：每次计时器中断（centos为4ms）

#### CPU 还是 I/O  ?

load average的数字只是表示等待的任务数，仅根据load average并不能判断具体是CPU负载高还是I/O负载高。

**CPU密集型程序**

**I/O密集型程序**

#### Sar (System Activity Reporter)

CPU使用率和I/O等待时间都是在不断变化的，可以通过sar 命令来确认这些指标。该工具包含在sysstat软件包内。

```C
$ sar
Linux    04/17/16        _x86\_64_        (24 CPU)
00:00:01        CPU     %user     %nice   %system   %iowait    %steal     %idle
00:10:02        all      1.26      0.00      0.55      0.00      0.00     98.19
00:20:01        all      1.58      0.00      1.04      0.00      0.00     97.38
00:30:01        all      1.23      0.00      0.56      0.00      0.00     98.21
00:40:01        all      1.59      0.00      1.01      0.00      0.00     97.40
00:50:01        all      1.35      0.00      0.59      0.00      0.00     98.06
01:00:01        all      1.63      0.00      1.10      0.00      0.00     97.27
01:10:01        all      1.22      0.00      0.54      0.00      0.00     98.24
01:20:01        all      1.68      0.00      1.06      0.00      0.00     97.25
01:30:01        all      1.23      0.00      0.54      0.00      0.00     98.23
```

```C
$ sar 1 10
18:54:58    %usr    %sys    %wio   %idle
18:54:59      18       3       0      79
18:55:00      46      14       0      40
18:55:01      38      13       0      49
18:55:02      17       4       0      79
18:55:03      11       4       0      85
18:55:04      12       5       0      83
18:55:05      20       4       0      76
18:55:06      22       3       0      75
18:55:07      21       4       0      75
18:55:08      17       4       0      79
```

输出参数:
- %user:用户(一般的应用软件运作模式)CPU资源
- %system:系统（内核运作）占用CPU资源
- %iowait:I/O等待率。


## 进程详细
```C
$ ps auxw
test 1551 0.2 0.1 6452 4776 ? S 19:25 0:00  Test.pl CTP00004.PRS00034 1 300
test 1553 2.6 0.4 18196 16424 ? S 19:25 0:00  /Test.pl 00001.PRS00034
test 1555 2.6 0.4 18168 16396 ? S 19:25 0:00  /Test.pl 00002.PRS00034
test 1557 2.8 0.4 18132 16432 ? S 19:25 0:00  /Test.pl 00004.PRS00034
test 1606 0.0 0.0 50060 916 ? Sl 19:25 0:00 /bin/PingTest -f CTP00004
test 1612 2.5 0.4 18156 16452 ? S 19:25 0:00  /Test.pl 00014.PRS00034
test 1629 2.1 0.4 18416 16696 ? S 19:25 0:00  /Test.pl 00015.PRS00034
test 2253 2.7 0.3 12868 11160 ? R 19:25 0:00  -w mrtg MRTG\_00027.cfg log
test 2254 3.6 0.3 12864 11184 ? S 19:25 0:00  -w mrtg MRTG\_00028.cfg log
test 2261 2.4 0.2 12640 11004 ? S 19:25 0:00  -w mrtg MRTG\_00030.cfg log
```

输出参数：
- %CPU：该进程的CPU使用率
- %memb：物理内存百分比
- VSZ、RSS：虚拟／物理内存
- STAT：进程状态（非常重要）
- TIME：CPU占用时间

#### SWAP吞吐
```C
$sar -W
17:20:01     pswpin/s pswpout/s
17:30:01         0.00      0.00
17:40:01         0.00      0.00
17:50:01         0.00      0.00
18:00:01         0.00      0.00
18:10:01         0.00      0.00
18:20:01         0.00      0.00
18:30:01         0.00      0.00
18:40:01         0.00      0.00
18:50:02         0.00      0.00
19:00:01         0.00      0.00
19:10:02         0.00      0.00
Average:         0.00      0.00
```
输出参数：
- pswpin/s:每秒系统换入的页面数
- pswpout/s:每秒系统换出的页面数

**发生频繁的交换时，服务器的吞吐量性能会大幅下降。**

#### vmstat(Report Virtual Memory Statistics)

实时确认CPU使用率及实际的I/O等待时间
```C
$ vmstat
 kthr      memory            page            disk          faults      cpu
 r b w   swap  free  re  mf pi po fr de sr s2 s2 s2 s2   in   sy   cs us sy id
 0 0 0 45411448 17973032 140 1470 13 41 33 0 0 -0 -0 -0 -0 2753 313459 4984 16 3 81
```

#### 解决方案
优化的真正工作是“找出系统瓶颈并加以解决”，我们所能做的就是“充分发挥硬／软件本来的性能，解决可能存在的问题”。例如，同样是I/O问题，我们可以通过增加内存来缓解，也可以调整调度方案来优化（时间换空间），但是更多的情况是，优化应用程序的I/O算法效果更佳。

最后，重温一句经典格言
>别臆断，请监控

## 扩展阅读：Linux 操作系统
- [《Linus Torvalds:Just for Fun》](http://riboseyim.github.io/2016/04/24/LinusTorvalds/)
- [Linux 常用命令一百条](http://riboseyim.github.io/2017/04/26/Linux-Commands/)
- [Linux 性能诊断:负载评估](https://riboseyim.github.io/2017/12/11/Linux-Perf-Load/)
- [Linux 性能诊断:快速检查单(Netflix版)](https://riboseyim.github.io/2017/12/11/Linux-Perf-Netflix/)
- [Linux 性能诊断：荐书|《图解性能优化》](https://riboseyim.github.io/2017/10/24/Linux-Perf-Picture/)
- [Linux 性能诊断：Web应用性能优化](https://riboseyim.github.io/2017/10/24/Linux-Perf-Wan/)
- [操作系统原理 | How Linux Works（一）：How the Linux Kernel Boots](http://riboseyim.github.io/2017/05/29/Linux-Works/)
- [操作系统原理 | How Linux Works（二）：User Space & RAM](http://riboseyim.github.io/2017/05/29/Linux-Works/)
- [操作系统原理 | How Linux Works（三）：Memory](https://riboseyim.github.io/2017/12/11/Linux-Works-Memory/)
