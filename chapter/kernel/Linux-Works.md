# How Linux Works

## (一)How the Linux Kernel Boots
1. The machine’s BIOS or boot firmware loads and runs a boot loader.(Boot Loader 是在操作系统内核运行之前运行的一段小程序，它严重地依赖于硬件而实现)
2. The boot loader finds the kernel image on disk, loads it into memory, and starts it. （选择内核镜像，加载到内存空间，为最终调用操作系统内核准备好正确的环境。）
3.  The kernel initializes the devices and its drivers.（初始化硬件设备及其驱动程序）
4.  The kernel mounts the root filesystem.（挂载根目录。根目录指文件系统的最上一级目录，它是相对子目录来说的；它如同一棵大树的“根”一般，所有的树杈以它为起点）
5.  The kernel starts a program called init with a process ID of 1. This point is the user space start.（内核启动一个初始化程序，从这里开始虚拟内存开始划分出使用者空间，与内核空间（Kernel space）对应）
6.  init sets the rest of the system processes in motion
7.  At some point, init starts a process allowing you to log in, usually at the end or near the end of the boot.

<!--more-->

#### Startup Messages
有两种方式可以查看内核引导和运行诊断信息：
1. 查看内核系统日志文件。文件路径： /var/log/kern.log
2.  执行dmesg命令
```bash
[root@li1437-101 ~]# dmesg
[    0.000000] Linux version 4.9.7-x86_64-linode80 (maker@build) (gcc version 4.7.2 (Debian 4.7.2-5) ) #2 SMP Thu Feb 2 15:43:55 EST 2017
[    0.000000] Command line: root=/dev/sda console=tty1 console=ttyS0 ro  devtmpfs.mount=1
[    0.000000] x86/fpu: Supporting XSAVE feature 0x001: 'x87 floating point registers'
[    0.000000] x86/fpu: Supporting XSAVE feature 0x002: 'SSE registers'
[    0.000000] x86/fpu: Supporting XSAVE feature 0x004: 'AVX registers'
[    0.000000] x86/fpu: xstate_offset[2]:  576, xstate_sizes[2]:  256
[    0.000000] x86/fpu: Enabled xstate features 0x7, context size is 832 bytes, using 'standard' format.
[    0.000000] x86/fpu: Using 'eager' FPU context switches.
[    0.000000] e820: BIOS-provided physical RAM map:
…….

[    0.000000] NX (Execute Disable) protection: active
[    0.000000] SMBIOS 2.8 present.
[    0.000000] DMI: QEMU Standard PC (i440FX + PIIX, 1996), BIOS rel-1.9.1-0-gb3ef39f-prebuilt.qemu-project.org 04/01/2014
[    0.000000] Hypervisor detected: KVM

……
[    0.371925] raid6: sse2x1   gen()  7490 MB/s
[    0.428689] raid6: sse2x1   xor()  5953 MB/s
[    0.485463] raid6: sse2x2   gen()  9289 MB/s
[    0.542230] raid6: sse2x2   xor()  6754 MB/s
[    0.599013] raid6: sse2x4   gen() 10954 MB/s
[    0.656189] raid6: sse2x4   xor()  5522 MB/s
[    0.656943] raid6: using algorithm sse2x4 gen() 10954 MB/s
[    0.657588] raid6: .... xor() 5522 MB/s, rmw enabled
……
[    1.053697] Netfilter messages via NETLINK v0.30.
[    1.054471] nfnl_acct: registering with nfnetlink.
[    1.055332] nf_conntrack version 0.5.0 (8192 buckets, 32768 max)
[    1.056324] ctnetlink v0.93: registering with nfnetlink.
[    1.057335] nf_tables: (c) 2007-2009 Patrick McHardy <kaber@trash.net>
[    1.058393] nf_tables_compat: (c) 2012 Pablo Neira Ayuso <pablo@netfilter.org>
[    1.059599] xt_time: kernel timezone is -0000
[    1.060296] ip_set: protocol 6
[    1.060791] IPVS: Registered protocols (TCP, UDP, SCTP, AH, ESP)
[    1.061940] IPVS: Connection hash table configured (size=4096, memory=64Kbytes)
[    1.063162] IPVS: Creating netns size=2104 id=0
[    1.064139] IPVS: ipvs loaded.
……
[    1.744221] systemd[1]: Detected virtualization kvm.
[    1.745058] systemd[1]: Detected architecture x86-64.
[    1.747402] systemd[1]: Set hostname to <localhost.localdomain>.
[    1.834328] tsc: Refined TSC clocksource calibration: 2800.119 MHz
[    1.835512] clocksource: tsc: mask: 0xffffffffffffffff max_cycles: 0x285cb16f950, max_idle_ns: 440795333193 ns
[    1.843476] systemd[1]: Created slice Root Slice.
[    1.844251] systemd[1]: Starting Root Slice.
[    1.845835] systemd[1]: Created slice System Slice.
[    1.846631] systemd[1]: Starting System Slice.
[    1.848257] systemd[1]: Listening on udev Kernel Socket.
[    1.849119] systemd[1]: Starting udev Kernel Socket.
[    2.014715] EXT4-fs (sda): re-mounted. Opts: (null)
[    2.038202] systemd-journald[2010]: Received request to flush runtime journal from PID 1
[    2.241341] audit: type=1305 audit(1488188850.897:2): audit_pid=2215 old=0 auid=4294967295 ses=4294967295 res=1
[    2.287758] Adding 262140k swap on /dev/sdb.  Priority:-1 extents:1 across:262140k FS
[    2.905177] IPVS: Creating netns size=2104 id=1
[    2.954613] IPv6: ADDRCONF(NETDEV_UP): eth0: link is not ready
[    2.955987] 8021q: adding VLAN 0 to HW filter on device eth0
[    8.009765] random: crng init done
```

在故障排查中，dmesg信息需要首先查看,例如输出最近10条系统信息,
可以查看到引起性能问题的错误。
```bash
$ dmesg | tail
[1880957.563150] perl invoked oom-killer: gfp_mask=0x280da, order=0, oom_score_adj=0
[...]
[1880957.563400] Out of memory: Kill process 18694 (perl) score 246 or sacrifice child
[1880957.563408] Killed process 18694 (perl) total-vm:1972392kB, anon-rss:1953348kB, file-r
ss:0kB
[2320864.954447] TCP: Possible SYN flooding on port 7001. Dropping request. Check SNMP cou
nters.
```

#### Kernel initialization and Boot Options
在启动时，Linux内核初始化的顺序如下：
1. CPU inspection （检查CPU）
2.  Memory inspection （检查内存）
3.  Device bus discovery （发现设备总线）
4.  Device discovery （发现设备）
5.  Auxiliary kernel subsystem setup(networking, and so on) （辅助内核子系统启动，例如网络等）
6.  Root filesystem mount （挂载根目录）
7.  User space start （用户空间启动）

![root filesystem](http://og2061b3n.bkt.clouddn.com/Linux_kernel_root_filesystem.png)

### Kernel Parameters

文件/proc/cmdline记录了系统内核启动参数：
```bash
[root@li1437-101 ~]# cat /proc/cmdline
root=/dev/sda console=tty1 console=ttyS0 ro  devtmpfs.mount=1
```
查看运行级别：
```bash
[root@li1437-101 ~]# who -r
		 run-level 3  2017-02-27 09:47
[root@li1437-101 ~]#
```

## How User Space Starts
用户空间启动顺序:
1. init
2. 必要的低层服务例如：udevd 和 syslog
3. 网络配置
4. 中高层服务例如 ：cron , printing
5. 登录提示、图形界面及其它高层次应用

### 天字第一号进程
init（initialization的简写）是 Unix 和 类Unix 系统中用来产生其它所有进程的程序。它以守护进程的方式存在，其进程号为1。Linux系统在开机时加载Linux内核后，便由Linux内核加载init程序，由init程序完成余下的开机过程，比如加载运行级别，加载服务，引导Shell/图形化界面等等。
```bash
[root@li1437-101 ~]# ps -ef | grep init
root         1     0  0 Feb27 ?        00:03:05 /sbin/init
root     28683 28663  0 02:44 pts/0    00:00:00 grep --color=auto init
```

```bash
// Mac OS
bash-3.2$ ps -ef | grep init
	0   243     1   0 15 517  ??         0:00.74 /System/Library/CoreServices/CrashReporterSupportHelper server-init
	0   533     1   0 15 517  ??         0:02.07 /System/Library/CoreServices/SubmitDiagInfo server-init
  501 52150     1   0 日01下午 ??         0:15.49 /usr/libexec/secinitd
	0 69864     1   0 11:35上午 ??         0:00.20 /usr/libexec/secinitd
	0 72830     1   0  1:51下午 ??         0:00.19 /usr/libexec/secinitd
Darwin ACA80166.ipt.aol.com 16.5.0 Darwin Kernel Version 16.5.0: Fri Mar  3 16:52:33 PST 2017; root:xnu-3789.51.2~3/RELEASE_X86_64 x86_64
bash-3.2$
```

在Linux发行版中，init有三种主要的实现形式：
1. **System V init**: 传统的
2. **systemd**: 所有主流Linux发行版中的标准init
3. **Upstart**: Ubuntu

Android 和 BSD （运行存放于'/etc/rc'的初始化 shell 脚本）也有它们自己的init版本，一些发行版也将System V init 修改为类似BSD风格的实现。目前大部分Linux发行版都已采用新的systemd替代System V和Upstart，但systemd向下兼容System V。

**System V init**: 存在一个启动序列，同一时间只能启动一个任务，这种架构下，很容易解决依赖问题，但是性能方面要受一些影响。
**systemd is goal oriented.** : 针对System V init的不足，systemd所有的服务都并发启动。systemd时基于目标的，需要定义要实现的目标，以及它的依赖项。systemd 将所有过程都抽象为一个配置单元，即 unit。可以认为一个服务是一个配置单元；一个挂载点是一个配置单元。

![systemd Unit target Tree](http://og2061b3n.bkt.clouddn.com/Linux_kernel_systemd_UnitTree.png)

**Upstart is reactionary.**:Upstart是基于事件的，Upstart的事件驱动模型允许它以异步方式对生成的事件作出回应。

![System V init Vs UpStart Vs Systemd](http://og2061b3n.bkt.clouddn.com/Linux_kernel_systemd_upstart_sysV.png)

## (三) The Initial RAM filesystem

Linux内核不能通过访问PC BIOS 或者 EFI接口从磁盘获取数据，所以为了mount它的root filesystem, 对于底层存储需要驱动程序支持。解决方案是在内核运行之前，由boot loader加载驱动模块及工具到内存。在启动时，内核读取相关模块到一个临时的RAM filesystem(initramfs),挂载在／根目录,initramsfs允许内核为真正的root filesystem加载必要的驱动模块。
最后，再挂载真正的root filesystem、启动init。

Linux在很多场景下都需要创建一个基于内存的文件系统，提供一个可以接近零延迟的快速存储区域。目前有两类主要的RAM磁盘可用，她们个有优劣：ramfs和tmpfs。(注意：创建之前使用 **free** 命令查看未使用的RAM)

```bash
# free
              total        used        free      shared  buff/cache   available
Mem:        1012720      168756       23576       52024      820388      754520
Swap:        262140          88      262052


# mkdir /mnt/ramdisk
# mount -t tmpfs -o size=512m tmpfs /mnt/ramdisk
# vi /etc/fstab
#tmpfs       /mnt/ramdisk tmpfs   nodev,nosuid,noexec,nodiratime,size=1024M   0 0
```

## 参考文献

1. [linfo.org:Root Filesystem Definition](http://www.linfo.org/root_filesystem.html)
2. [阮一峰：Systemd 入门教程：命令篇](http://www.ruanyifeng.com/blog/2016/03/systemd-tutorial-commands.html)
3. [阮一峰：Systemd 入门教程：实战篇](http://www.ruanyifeng.com/blog/2016/03/systemd-tutorial-part-two.html)
4. [IBM developerworks:浅析 Linux 初始化 init 系统，第 3 部分: Systemd](https://www.ibm.com/developerworks/cn/linux/1407_liuming_init3/)
5. [维基百科：Systemd](https://zh.wikipedia.org/wiki/Systemd)
6. [differences between ramfs and tmpfs](http://www.jamescoyle.net/knowladge/951-the-difference-between-a-tmpfs-and-ramfs-ram-disk)
