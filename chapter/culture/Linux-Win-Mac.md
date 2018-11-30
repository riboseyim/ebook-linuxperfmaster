# 谁是王者：macOS vs Linux Kernels ？

有些人可能认为 macOS 和 Linux 内核是类似的系统, 因为它们看起来可以处理类似的命令和软件。有些人甚至认为苹果的 macOS 是基于 Linux 的。事实上, 这两个内核各有特色，也都有不同寻常的历史。

#### 命令行

|操作|Linux|Mac|Windows|
|------|-------|----------|
|查看二进制文件| ldd| otool -L | ---- |
|查看网卡|ifconfig | ifconfig | ipconfig |
|查看网络路由|route -n / netstat -ar| netstat -ar | route PRINT |
|查看网络连接|netstat -an | netstat -an | netstat -an |
|动态链接库|ldconfig|update_dyld_shared_cache|----|
|hosts|/etc/hosts//private/etc/hosts|-----|
|debug|strace|dtruss|-----|

## macOS Kernel 简史
我们首先从 macOS Kernel 的历史开始讲起。1985年, 史蒂夫·乔布斯（ Steve Jobs）离开苹果公司（Apple ）并创办了一家新的计算机公司：**NeXT** 。乔布斯快速向市场推出了一款新型电脑 (搭载新操作系统) 。为了节省时间, NeXT 团队使用了 Mach kernel（ 来自卡内基·梅隆大学，是最早实现微核心操作系统的例子之一，是许多其它相似的项目的标准，在分布式与并行运算领域应用广泛) 和 部分 BSD 代码库, 创建了 NeXTSTEP 操作系统。具有讽刺意味的是，早期 Mach 只是服务于操作系统研究，曾经的开发目标甚至是取代 BSD ，目前 Mach 的研究至今似乎是停止了，但是许多商业化操作系统，特别是 Mac OS X  及其它派生系统却活得很滋润。

在商业上，NeXT 公司从来没有获得成功, 部分原因是乔布斯的花钱习惯仍然像他在苹果公司一样大手大脚。与此同时, 苹果公司多次尝试更新自己的操作系统, 但是包括与 IBM 合作在内、一直未取得成功。1997年, 苹果公司以 4 亿 2900 万美元收购了 NeXT 公司 。作为交易的一部分, 史蒂夫·乔布斯重新回到苹果, NeXTSTEP 操作系统也成为了 macOS 和 iOS 操作系统的基础。值得注意的是，NeXT  团队的另一项成果 WebObjects 后来集成到了 Mac OS X Server 和 Xcode 中。

## Linux Kernel 简史
与 macOS Kernel 不同, Linux 从一开始不是作为商业计划的一部分而创建的。相反, Linux 在 1991 年由芬兰计算机专业的学生林纳斯·托瓦兹（Linus Torvalds）创建。最初 Linux 内核代码是按照林纳斯个人的计算机规格编写, 因为他想利用自己电脑上搭载的新式 80386 处理器。林纳斯在 1991 年 8 月将他编写的操作系统内核代码发布到网络上。不久, 他收到了来自世界各地爱好者贡献的代码和建议。第二年, [Orest Zborowski ](美国黑客, 生于康涅狄格州, 在北卡罗来纳州杜克大学获得电气工程和计算机科学学位，曾在柯达和微软任职) 将 X Windows 系统移植到 Linux 中, 使其能够支持图形用户界面。

在过去的 27 年里, Linux 已经慢慢成长壮大，它不再是一个学生的业余项目。目前，Linux 运行在世界上大多数的计算设备和超级计算机上。

![](http://riboseyim-qiniu.riboseyim.com/History_macOS_Linux.png)

>他在旧版的SketchPad代码基础上进行了修改，让它运行在Macintosh上，并改名MacSketch。SketchPad通过菜单的方式选取图案和样式，Bill把它们放到屏幕下方，变成固定的调色板，并在屏幕左边另外增加一组调色板，包含了各种各样的绘图工具。后续还会不断增加新的工具，不过MacPaint早期的基本架构已经就此成形。
—— 《硅谷革命：成就苹果公司的疯狂往事》

## macOS Kernel 特色
macOS kernel 的特色官方总结为: XNU。意思是 "XNU 不是 Unix"。根据苹果的 Github 页面, XNU 是 "一个混合内核, 奠基于卡耐基·梅隆大学开发的 Mach kernel , 它的组件来自 FreeBSD 和用于编写驱动程序的 C++ API “。BSD 子系统部分的代码 "通常作为微核心系统中实现用户空间服务"。Mach 部分的代码则负责较底层的工作, 例如多任务（multitasking）、内存保护（ protected memory）、虚拟内存管理（virtual memory management）、支持内核调试（kernel debugging）和控制台 I/O 。 Mach 的虚拟内存（VM）系统也被 BSD 的开发者用于 CSRG ，并出现在 BSD 派生的系统中，如 FreeBSD ，但是无论 macOS 还是 FreeBSD 都并未保留 Mach 首倡的微核心结构，除了 macOS 继续提供微核心于内部处理通信以及应用程序直接控制之外。

>他在旧版的SketchPad代码基础上进行了修改，让它运行在Macintosh上，并改名MacSketch。SketchPad通过菜单的方式选取图案和样式，Bill把它们放到屏幕下方，变成固定的调色板，并在屏幕左边另外增加一组调色板，包含了各种各样的绘图工具。后续还会不断增加新的工具，不过MacPaint早期的基本架构已经就此成形。
———《硅谷革命：成就苹果公司的疯狂往事》

## Linux Kernel 特色
如果说 macOS 内核的特点是结合了微核心 (microkernel ，Mach) 和宏内核 (monolithic kernel ，BSD)，那么 Linux 则仅仅是一个宏内核。宏内核负责管理 CPU、内存、进程间通信、设备驱动程序、文件系统和系统服务调用。

![](http://riboseyim-qiniu.riboseyim.com/OS_Kernel_Model.png)

## 一句话总结

macOS 内核代码 (XNU) 基于两个甚至包含古老代码基的组合。另一方面, Linux 的历史更短, 它从头开始编写并且应用广泛。

>预测未来的最佳方式就是创造未来。
——  个人电脑之父 艾伦·凯(Alan Kay)

## 扩展阅读
- [《Linus Torvalds:Just for Fun》](https://riboseyim.github.io/2016/04/24/LinusTorvalds/)
- [Linux 常用命令一百条](https://riboseyim.github.io/2017/04/26/Linux-Commands/)
- [Linux 性能诊断:负载评估](https://riboseyim.github.io/2017/12/11/Linux-Perf-Load/)
- [Linux 性能诊断:快速检查单(Netflix版)](https://riboseyim.github.io/2017/12/11/Linux-Perf-Netflix/)
- [Linux 性能诊断:荐书|《图解性能优化》](https://riboseyim.github.io/2017/10/24/Linux-Perf-Picture/)
- [Linux 性能诊断:Web应用性能优化](https://riboseyim.github.io/2017/10/24/Linux-Perf-Wan/)
- [操作系统原理 | How Linux Works（一）：How the Linux Kernel Boots](https://riboseyim.github.io/2017/05/29/Linux-Works/)
- [操作系统原理 | How Linux Works（二）：User Space & RAM](https://riboseyim.github.io/2017/05/29/Linux-Works/)
- [操作系统原理 | How Linux Works（三）：Memory](https://riboseyim.github.io/2017/12/11/Linux-Works-Memory/)

## 参考文献
- [Difference Between the macOS and Linux Kernels](https://itsfoss.com/mac-linux-difference/)
- [What is Mac OS X?](http://osxbook.com/book/bonus/ancient/whatismacosx/arch_xnu.html)
- [Linkedin:Orest Zborowski](https://www.linkedin.com/in/orestzborowski/)
- [Linux Networking/A brief history of Linux Networking Kernel Development](https://en.wikibooks.org/wiki/Linux_Networking/A_brief_history_of_Linux_Networking_Kernel_Development)
