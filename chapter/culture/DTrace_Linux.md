## 开源故事：DTrace 软件许可证演变简史

#### News:dtrace dropped the CDDL and switched to the GPL!

根据 2月14日 Brendan Gregg 在 Twitter 上推送的消息：Oracle 已经将 DTrace 模块的开源许可证从 CDDL 切换到 GPL，预计最快到 2018 年底 Linux kernel 就可以发布一个可用的 /usr/sbin/dtrace ，底层基于 bcc 和 eBPF 。

>Good news from Oracle: DTrace is GPL'd (thank you!). I'd guess by the end of 2018 we'll have a working /usr/sbin/dtrace on Linux for running D scripts (using libbcc+eBPF on the backend)

关于动态追踪技术（Dynamic Tracing），我们在之前的文章已经有所介绍，[动态追踪技术(一)：DTrace 导论](https://riboseyim.github.io/2016/11/26/DTrace/) 。DTrace 是动态追踪技术的鼻祖，源自 Solaris 操作系统，提供了高级性能分析和调试功能（ advanced performance analysis and troubleshooting tool ）。

Oracle 收购 SUN 公司之后推出了 Oracle Linux DTrace （基于 Oracle 企业级内核  Unbreakable Enterprise Kernel ，UEK)，针对性地发展完善了一系列探针如 syscall, profile, sdt, proc, sched 和 USDT，受制于诸多原因一直没有进入 Linux kernel 代码树，其中最大的一个障碍是许可授权问题：DTrace 源代码采用 CDDL 许可证，不兼容 Linux kernel 使用的 GPLv2 许可证，无法直接移植。

```c
 commit e1744f50ee9bc1978d41db7cc93bcf30687853e6
 Author: Tomas Jedlicka <tomas.jedlicka@oracle.com>
 Date: Tue Aug 1 09:15:44 2017 -0400

 dtrace: Integrate DTrace Modules into kernel proper

 This changeset integrates DTrace module sources into the main kernel
 source tree under the GPLv2 license. Sources have been moved to
 appropriate locations in the kernel tree.
```

#### CDDL vs GPL
DTrace 与 OpenSolaris 一样之前是基于 CDDL 许可证而不是 Linux kernel 使用的 GPL 许可证，二者的区别是什么呢？

![](http://omb2onfvy.bkt.clouddn.com/Software-license-classification.png)

GNU通用公共许可协议（GNU General Public License，简称 GNU GPL、GPL，港台地区翻译为“GNU通用公共授权条款”）是广泛使用的免费软件许可证，最初由GNU项目的自由软件基金会（FSF）的理查德·斯托曼（Richard Matthew Stallman）撰写。 

一般的版权概念（“copyright”），从不授予用户任何权利（除了使用的权利），更多的是限制性规定，例如复制、修改、分发等，也可以包括一些法律允许的行为，比如逆向工程。GPL 则代表了知识产权制度的左翼阵营（“copyleft”），它授予被许可人以下权利（或称“自由”）：
- 以任何目的运行此程序的自由；
- 再复制的自由；
- 修改程序并公开发布改进版的自由（前提是能得到源代码）。

GPL 及其它 Copyleft 协议授予了被许可人（几乎是任何人）以非常广泛的自由，同时利用版权法设计了“传染机制”：GPL明确规定，任何源码的衍生产品，如果对外发布，都必须保持同样的许可证。这就是说，任何人只要发布基于某个GPL软件的修改版本，他就必须公开源码，并且同意他人可以自由地复制和分发，否则原始作者可以根据版权法起诉 。

DTrace 的 CDDL 许可证继承自 Sun Microsystems 。通用开发与发行许可证（Common Development and Distribution License，简称CDDL） 是一个由 Sun 提出的授权协议，它以 Mozilla 公共许可证（MPL）1.1版本为基础。基于 CDDL 许可证的项目主要有：OpenSolaris (含 DTrace 和 ZFS) 、NetBeans IDE 、GlassFish 等。

>Like the MPL, the CDDL is a weak copyleft license in-between GPL license and BSD/MIT permissive licenses, requiring only source code files under CDDL to remain under CDDL.

鉴于 GPL 许可模式下很难通过开源软件直接盈利，因此也有很多类似 CDDL 的开源协议倾向于支持商业开发，授予厂商更大的决定权。CDDL 最大的特点是源代码和可执行文件允许采用不同的许可证。例如一般不存在 GPL 模式下存在的 “社区”版本，而是由厂商提供一些免费版本供开发者在非生产环境下使用，同时附上 CDDL 许可的源代码，开发者可以自行编译和部署；更重要的是，厂商只对付费客户提供安全补丁修复和维护版本的源代码。

**综上所述**，基于 CDDL 许可证的 DTrace 你只可以使用但不允许围绕代码进行修改，或者添加其他跟踪点。Paul Fox 个人贡献的dtrace4linux 项目就试图移植 Sun DTrace 到 Linux 的 ，但是受限于许可证只能做到附加产品（add-on）, 外部人员很难直接参与进来，事实上 CDDL 许可证的项目外部贡献最多一般不会超过 10% 。

#### Future
在此之前，Linux 已经拥有 SystemTap 和动态探针（dprobes），DTrace 是基于整个系统的全局跟踪、调试、分析工具。Linux kernel 的创建者显然不喜欢一个“复杂”的系统（large system）, 他们倾向于将跟踪、分析和探测划分为彼此独立的小单元。许多开发者为此发明了各种钩子（hooks）以及整合某些特定探针（probes）的便利工具，例如 kprobes , uprobes, markers 等。 dtrace for linux 正式进入 Linux kernel 之后，有望将相关技术整合成一个更强大的体系，这一点非常令人期待。


## 里程碑：Linux 合并 BPF

2016年11月，Linux 4.9-rc1发布，正式合并了一项重要特性：BPF追踪（Timed sampling）。

系统性能领域的国际导师Brendan Gregg，感动得都快哭了，当即在Twitter上表示这是一个重要的里程碑!
他随后又写了一篇长文[《DTrace for Linux 2016》](http://www.brendangregg.com/blog/2016-10-27/dtrace-for-linux-2016.html)，以示庆祝。

>As a long time DTrace user and expert, this is an exciting milestone!
--Brendan Gregg

Linux 合并了BPF而已嘛，跟DTrace这个劳什子有什么关系呢？

DTrace 是动态追踪技术的鼻祖，源自 Solaris 操作系统，提供了高级性能分析和调试功能，它的源代码采用 CDDL 许可证，不兼容 Linux 内核使用的 GPLv2 许可证，无法直接移植。当然，江湖上还有另外一种说法，Linux之所以一直没有原生支持DTrace,是因为Linus 觉得这玩意没什么必要。Anyway,随着 BPF跟踪的最后主要功能合并到 Linux 4.9-rc1，Linux 现在有了类似 DTrace 的高级分析和调试功能。

Linux 这次合并的BPF（The Berkeley Packet Filter ），来自于加州大学伯克利分校（这所大学很有意思，以后还要反复提到）。BPF，顾名思义，最早只是一个纯粹的封包过滤器，后来在很多牛人的参与下，进行了扩展，得到了一个所谓的 eBPF，可以作为某种更加通用的内核虚拟机。通过这种机制，我们其实可以在 Linux 中构建类似 DTrace 那种常驻内核的动态追踪虚拟机。

>Linux 没有 DTrace（名字），但现在有了 DTrace（功能）

## 扩展阅读：动态追踪技术
- [操作系统原理 | How Linux Works（一）：How the Linux Kernel Boots](https://riboseyim.github.io/2017/05/29/Linux-Works/)
- [操作系统原理 | How Linux Works（二）：User Space & RAM](https://riboseyim.github.io/2017/05/29/Linux-Works/)
- [操作系统原理 | How Linux Works（三）：Memory](https://riboseyim.github.io/2017/12/11/Linux-Works-Memory/)
- [动态追踪技术(一)：DTrace 导论](https://riboseyim.github.io/2016/11/26/DTrace/)
- [动态追踪技术(二)：strace+gdb 溯源 Nginx 内存溢出异常 ](https://mp.weixin.qq.com/s?__biz=MjM5MTY1MjQ3Nw==&mid=2651939588&idx=1&sn=35f71c5f88d1edf23cb2efc812ab8e6c&chksm=bd578c168a20050041c08618281691f0111f61c789097a69095933057618637fc54817815921#rd)
- [动态追踪技术(三)：Tracing Your Kernel Function!](https://riboseyim.github.io/2017/04/17/DTrace_FTrace/)
- [动态追踪技术(四)：基于 Linux bcc/BPF 实现 Go 程序动态追踪](https://riboseyim.github.io/2017/06/27/DTrace_bcc/)
- [动态追踪技术(五)：Welcome DTrace for Linux](https://riboseyim.github.io/2018/02/16/DTrace-Linux/)

## 参考文献
- [DTRACE FOR LINUX; ORACLE DOES THE RIGHT THING | February 14, 2018 | MARK J. WIELAARD](https://gnu.wildebeest.org/blog/mjw/2018/02/14/dtrace-for-linux-oracle-does-the-right-thing/)
- [Oracle Linux DTrace](http://www.oracle.com/technetwork/server-storage/linux/downloads/linux-dtrace-2800968.html)
