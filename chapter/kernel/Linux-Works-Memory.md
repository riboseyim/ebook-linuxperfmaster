# How Linux Works(三): 内存管理

#### 摘要
- 经典内存异常：Out of Memory (OOM) Killer
- 我的内存利用率为什么特别高？
- Linux 内存的分类
- Linux 内存的计算
- Linux 进程的内存
- Linux 应用内存分配

内存是计算机中与CPU进行沟通的桥梁，用于暂时存放CPU中的运算数据。Linux 内核的内存管理机制设计得非常精妙，对于 Linux 内核的性能有很大影响。在早期的 Unix 系统中，fork 启动新进程时，由于从父进程往子进程复制内存信息需要消耗一定的时间，因此启动多个进程时存在性能瓶颈。现在的 Linux 内核则通过“写时复制（copy-on-write）”等机制提高了创建进程的效率；也正是因为这个原因，关于 Linux 内存分配、计算、空闲判断有一些特别的地方需要注意。

#### 内存异常： Out of Memory (OOM) Killer

```C
$ dmesg | tail
[1880957.563400] Out of memory: Kill process 18694 (perl) score 246 or sacrifice child
```

最常见的内存管理异常就是 Out of memory 问题。通常是因为某个应用程序大量请求内存导致系统内存不足造成的，触发 Linux 内核里的 [Out of Memory (OOM) killer](https://linux-mm.org/OOM_Killer)，OOM killer 会杀掉某个进程以释放内存留给系统内核用。它实际上算一种保护机制，不致于让系统立刻崩溃，有些壮士断腕的意思。

内核检测到内存利用不足，就会选择并杀掉某个“bad”进程。如何判断和选择一个 “bad” 进程呢? 算法和思路其实非常朴素（简单）：最 bad 的那个进程就是那个占用内存最多的进程。内核源代码详见 linux/mm/oom_kill.c 。


#### 我的内存利用率为什么特别高？

- 内存利用率（概括）: free
- 内存利用率（进程）: top

![](http://riboseyim-qiniu.riboseyim.com/Linux-Perf-Memory-Normal.png)


```C
内存空闲率 = (Total - Used) / Total = (7982M - 7503M) / 7983M X 100 % = 6 %

“真实的” 内存空闲率 =  (free + shared + buffers + cached)/ Total = 5860 M / 7983M X 100 % = 73.4 %

```

实际情况是系统运行正常、不存在内存不足的情况，对于应用程序来说，“真实的” 内存空闲率是 73.4% 。如果要回答这个问题，必须了解内存管理的基础 —— 物理地址空间和逻辑地址空间。

按照用途，内存可以划分为“内核内存”和“用户内存”（用户进程及磁盘高速缓存），包括内核本身在内，程序在访问物理内存时，并不直接指定物理地址，而是指定逻辑地址。CPU 上搭载的硬件 MMU （Memory Management Unit）会参照物理-逻辑地址对应关系表实现对映射后物理地址上的数据访问。x86 架构中逻辑地址空间限制在 4GB ，在 x86_64 架构中则没有此限制。

#### Linux 内存的分类
用户内存的分类有两组概念比较重要：匿名内存和File-backed内存；Active 和 Inactive 。它们的区别如下：

- 匿名内存：用来存储用户进程**计算过程中间的数据**，与物理磁盘的文件没有关系；
- File-backed内存：用作磁盘高速缓存，其物理内存与物理磁盘上的文件是对应的；
- Active : 刚被使用过的数据的内存空间；
- Inactive : 包含有长时间未被使用过的数据的内存空间；

**Shmem**（shared memory）指的就是 tmpfs 所使用的内存 —— 一个基于内存的文件系统，提供可以接近零延迟的快速存储区域。Linux 可以将空闲内存用于缓存，并且在应用程序需要的时候收回。**“­/+ buffers/cache”**: 提供了关于内存利用率更加准确的数值。**buffers**: buffer cache,用于块设备I/O ; **cached**: page cache, 用于文件系统。例如：

```C
# free
			  total        used        free      shared  buff/cache   available
Mem:        1012720      168756       23576       52024      820388      754520
Swap:        262140          88      262052
# mkdir /mnt/ramdisk
# mount -t tmpfs -o size=512m tmpfs /mnt/ramdisk
# vi /etc/fstab
# tmpfs       /mnt/ramdisk tmpfs   nodev,nosuid,noexec,nodiratime,size=1024M   0 0
```

- 内存利用率（详细）：cat /proc/meminfo

```C
$ cat /proc/meminfo
MemTotal:        8174352 kB
MemFree:          376952 kB
Buffers:          527412 kB
Cached:          5178924 kB
SwapCached:           60 kB
Active:          3061760 kB
Inactive:        4066588 kB
Active(anon):    1112780 kB
Inactive(anon):   314156 kB
Active(file):    1948980 kB
Inactive(file):  3752432 kB
Unevictable:        6724 kB
Mlocked:            6724 kB
SwapTotal:      16779884 kB
SwapFree:       16777400 kB
Dirty:               376 kB
Writeback:             0 kB
AnonPages:       1428844 kB
Mapped:            64632 kB
Shmem:               644 kB
Slab:             557384 kB
SReclaimable:     338272 kB
SUnreclaim:       219112 kB
KernelStack:        4024 kB
PageTables:        12440 kB
NFS_Unstable:          0 kB
Bounce:                0 kB
WritebackTmp:          0 kB
CommitLimit:    20867060 kB
Committed_AS:    2406484 kB
VmallocTotal:   34359738367 kB
VmallocUsed:      111536 kB
VmallocChunk:   34359455060 kB
HugePages_Total:       0
HugePages_Free:        0
HugePages_Rsvd:        0
HugePages_Surp:        0
Hugepagesize:       2048 kB
DirectMap4k:        6384 kB
DirectMap2M:     2080768 kB
DirectMap1G:     6291456 kB
```

#### Linux 内存的计算

各类内存的计算公式如下：
>Shmem = 磁盘高速缓存（buffers/cached）- Filed-backed内存（file）
>		   = 匿名内存（anon）- AnonPages
>用户内存 = Active(file) + Inactive(file) + Active(anon) + Inactive(anon) + Unevictable
>		   = buffers + cached + AnonPages

>内核内存 = Memtotal - (MemFree + Active + Inactive + Unevictable)

```C
$ cat /proc/meminfo | grep Active
Active:          3065880 kB
Active(anon):    1116748 kB
Active(file):    1949132 kB
-bash-4.3$
-bash-4.3$
-bash-4.3$ cat /proc/meminfo | grep InActive
-bash-4.3$
-bash-4.3$ cat /proc/meminfo | grep Inactive
Inactive:        4067224 kB
Inactive(anon):   314156 kB
Inactive(file):  3753068 kB
-bash-4.3$
-bash-4.3$
-bash-4.3$ cat /proc/meminfo | grep anon
Active(anon):    1120720 kB
Inactive(anon):   314156 kB
-bash-4.3$
-bash-4.3$ cat /proc/meminfo | grep file
Active(file):    1949236 kB
Inactive(file):  3753096 kB
-bash-4.3$
```

#### Linux 进程的内存

```C
-bash-4.3$ ps -e -o 'pid,comm,args,pcpu,rsz,vsz,stime,user,uid' | grep slview |  sort -nrk5
30029 java            /slview/jdk150/jdk1.5.0_06/  2.5 1337496 2678836 Dec07 slview 54322
31574 bash            -bash                        0.0  2028  70592 17:08 slview   54322
23398 crond           crond                        0.0  1688 102180 16:10 slview   54322
 1123 crond           crond                        0.0  1688 102180 Dec10 slview   54322
28252 crond           crond                        0.0  1596 102028 16:45 slview   54322
```

执行“ps aux” 后输出的各进程的 **RSS** (resident set size), 表示进程占用内存的大小，单位是KB。 需要注意的是，RSS 值实际上是基于 pmap 命令，表示“该进程**正在使用的**物理内存的总和”。pmap 提供了进程的内存映射，也可以支持多个进程的内存状态显示（pmap  pid1 pid2 pid3）。与 ldd 命令类似，pmap 命令可以查看到程序调用的路径。如果查看一个已经运行，但是又不知道程序路径的程序，使用pmap更快捷。

```C
$ pmap -x 30029
30029:   /slview/jdk150/jdk1.5.0_06/bin/java -com.apache.Test
Address           Kbytes     RSS   Dirty Mode   Mapping
0000000008048000      60      48       0 r-x--  java
0000000008057000       8       8       8 rwx--  java
0000000009f1d000   23184   23140   23140 rwx--    [ anon ]
000000004d1f1000     108      96       0 r-x--  ld-2.5.so
000000004d20c000       4       4       4 r-x--  ld-2.5.so
000000004d20d000       4       4       4 rwx--  ld-2.5.so
000000004d214000    1356     548       0 r-x--  libc-2.5.so
000000004d367000       8       8       8 r-x--  libc-2.5.so
00007f581e51d000      16      16       0 r--s-  huanan-product-2.6.1-snapshots.jar
00007f581e521000      24      24       0 r--s-  dt.jar
00007f581e527000      36      36       0 r--s-  gnome-java-bridge.jar
00007f581e530000      32      32       8 rw-s-  13228
00007f581e538000       4       4       4 rw---    [ anon ]
00007f581e539000       4       4       0 r----    [ anon ]
00007f581e53a000       8       8       8 rw---    [ anon ]
00007fffe9eb7000      84      32      32 rw---    [ stack ]
00007fffe9fff000       4       4       0 r-x--    [ anon ]
ffffffffff600000       4       0       0 r-x--    [ anon ]
(部分省略)
----------------  ------  ------  ------
total kB         2484196   36180   26880
```

**/proc/PID/status** 支持的选项有：
- VmData: data段大小
- VmExe: text段大小
- Vmlib: 共享库的使用量
- VmRSS: 物理内存使用量
- VmSwap: 交换空间的使用量

```C
$ cat /proc/30029/status
Name:	java
State:	S (sleeping)
Tgid:	30029
Pid:	30029
PPid:	29983
TracerPid:	0
Uid:	54322	54322	54322	54322
Gid:	54323	54323	54323	54323
FDSize:	8192
Groups:	10 54323
VmPeak:	 2754032 kB
VmSize:	 2678836 kB
VmLck:	       0 kB
VmHWM:	 1337912 kB
VmRSS:	 1337512 kB
VmData:	 2575692 kB
VmStk:	    1012 kB
VmExe:	      60 kB
VmLib:	  101564 kB
VmPTE:	    3048 kB
Threads:	98
SigQ:	0/63825
SigPnd:	0000000000000000
ShdPnd:	0000000000000000
SigBlk:	0000000000000004
SigIgn:	0000000000000001
SigCgt:	1000000180005cce
CapInh:	0000000000000000
CapPrm:	0000000000000000
CapEff:	0000000000000000
CapBnd:	ffffffffffffffff
Cpus_allowed:	ffffffff
Cpus_allowed_list:	0-31
Mems_allowed:	00000000,
Mems_allowed_list:	0
voluntary_ctxt_switches:	12468
nonvoluntary_ctxt_switches:	19
```

#### Linux 应用内存分配

![](http://riboseyim-qiniu.riboseyim.com/Linux-Memory-Application.png)

类似 Java 之类的虚拟机应用程序可以设置内存参数，例如:
Xms128m JVM初始分配的堆内存
Xmx512m JVM最大允许分配的堆内存
XX:PermSize=64M JVM初始分配的非堆内存
XX:MaxPermSize=128M JVM最大允许分配的非堆内存

如果该应用需要较大的内存空间，可以调整为 -Xmx1024m、-Xmx2048m 以保障应用程序的运行性能，XX:MaxPermSize 设置过小会导致内存溢出，java.lang.OutOfMemoryError: PermGen space。但是 **需要特别注意** 的是：Xmx 绝对不能超过最大物理内存，或者说需要保留一定的剩余内存空间，否则将有可能导致其它进程因为没有可用内存而阻塞，甚至无法登陆机器 。

正如摔跤游戏一样，内存管理的法则就是让进程在 **留有余地** 的前提下搏杀。

## 扩展阅读：Linux 操作系统
- [《Linus Torvalds:Just for Fun》](https://riboseyim.github.io/2016/04/24/LinusTorvalds/)
- [Linux 常用命令一百条](https://riboseyim.github.io/2017/04/26/Linux-Commands/)
- [Linux 性能诊断：负载评估](https://riboseyim.github.io/2017/12/11/Linux-Perf-Load/)
- [Linux 性能诊断：快速检查单(Netflix版)](https://riboseyim.github.io/2017/12/11/Linux-Perf-Netflix/)
- [Linux 性能诊断：荐书|《图解性能优化》](https://riboseyim.github.io/2017/10/24/Linux-Perf-Picture/)
- [Linux 性能诊断：Web应用性能优化](https://riboseyim.github.io/2017/10/24/Linux-Perf-Wan/)
- [操作系统原理 | How Linux Works（一）：How the Linux Kernel Boots](http://riboseyim.github.io/2017/05/29/Linux-Works/)
- [操作系统原理 | How Linux Works（二）：User Space & RAM](http://riboseyim.github.io/2017/05/29/Linux-Works/)
- [操作系统原理 | How Linux Works（三）：Memory](https://riboseyim.github.io/2017/12/11/Linux-Works-Memory/)

## 参考文献
- [Linux Ate my RAM](https://www.linuxatemyram.com/)
- [理解和配置 Linux 下的 OOM Killer |@vpsee](https://www.vpsee.com/2013/10/how-to-configure-the-linux-oom-killer/)
- [RiboseYim's Blog | How Linux Works(三): Memroy](https://riboseyim.github.io/2017/12/11/Linux-Works-Memory/)
