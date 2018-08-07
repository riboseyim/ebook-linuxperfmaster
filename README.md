# Linux Perf Master

![](http://p11slcnom.bkt.clouddn.com/banner-LPM-201803.png)

## 作者：[RiboseYim](https://riboseyim.github.io/2016/05/31/AboutMe/)

[Linkedin](https://www.linkedin.com/in/riboseyim/)
[简书主页](http://www.jianshu.com/u/8cc1dba4bc96)
[知乎专栏](https://www.zhihu.com/people/riboseyim)
[开源中国](https://my.oschina.net/zijingshanke/blog)
[Telegram](https://t.me/riboseyim)
[Mail](mailto:riboseyim@gmail.com)

《The Linux Perf Master》(暂用名) 是一本关于开源软件的电子书。本书与常见的专题类书籍不同，作者以应用性能诊断入手，尝试从多个不同的维度介绍以 Linux 操作系统为核心的开源架构技术体系。全书分为以下几个部分：
- 第一部分：介绍 Linux 性能诊断的入门方法。包括资源利用评估、性能监控、性能优化等工作涉及的工具和方法论，以 Stack Overflow 为例介绍一个真实的应用系统架构组成；
- 第二部分：基础设施管理工具。介绍 Ganglia,Ntop,Graphite,Ansible,Puppet,SaltStack 等基础设施管理 & 可视化工具；
- 第三部分：操作系统工作原理。介绍 Linux 操作系统工作原理（Not only Works,But Also How），从动态追踪技术的角度理解应用程序与系统行为；
- 第四部分：分布式系统架构。介绍负载均衡技术，微服务系统及其挑战：分布式系统性能追踪平台;
- 第五部分：网络与信息安全篇。介绍木马入侵、黑客攻击、防护与检测，IPv6 、封包过滤技术和态势感知等技术发展对安全工作的挑战；介绍信息安全法律；
- 第六部分：工程管理篇。尝试跳出 IT 视野讨论人才培养，DevOps 组织、效率和工程管理方法；
- 第七部分：社区文化篇。介绍黑客文化、开源作者、开发者社区和知识产权法，“技术首先是关于人的”（Technology is first about human beings）。

### 目录
* [Chapter 1: 性能诊断入门]()
* [Linux 性能诊断：单机负载评估](chapter/abc/Linux-Perf-Load.md)
* [Linux 性能诊断：快速检查单(Netflix版)](chapter/abc/Linux-Perf-Netflix.md)
* [全栈架构技术视野：以 Stack Overflow 为例](chapter/abc/OpenSource-StackOverflow.md)
* [Chapter 2: 应用监控与可视化]()
* [应用程序的日志管理](chapter/tools/Log.md)
* [基于 Ganglia 实现计算集群性能态势感知](chapter/tools/OpenSource-Ganglia.md)
* [新一代 Ntopng 网络流量监控](chapter/tools/Network-Ntopng.md)
* [Graphite 体系结构详解](chapter/tools/Visualization-Graphite.md)
* [部署和配置管理工具简介](chapter/tools/DevOps-Deployment.md)
* [2018 Docker 用户报告 - Sysdig Edition](chapter/tools/DevOps-Container-Usage.md)
* [开源地理信息系统简史](chapter/tools/Visualization-GIS.md)
* [Chapter 3: 操作系统原理与内核追踪]()
* [How Linux Works：内核空间和启动顺序](chapter/kernel/Linux-Works.md)
* [How Linux Works：内存管理](chapter/kernel/Linux-Works-Memory.md)
* [动态追踪技术(一)：DTrace](chapter/dtrace/DTrace.md)
* [动态追踪技术(二)：基于 strace+gdb 发现 Nginx 模块性能问题](chapter/dtrace/DTrace_Strace_Gdb.md)
* [动态追踪技术(三)：Trace Your Functions!](chapter/dtrace/DTrace_FTrace.md)
* [动态追踪技术(四)：基于 Linux bcc/BPF 实现 Go 程序动态追踪](chapter/dtrace/DTrace_bcc.md)
* [DTrace 软件许可证演变简史](chapter/culture/DTrace_Linux.md)
* [Chapter 4: 大数据与分布式架构]()
* [基于 LVS 的 AAA 负载均衡架构实践](chapter/distributed/AAA.md)
* [分布式架构案例：基于 Kafka 的事件溯源型微服务](chapter/distributed/OpenSource-Kafka-Microservice.md)
* [大数据监控框架：以 LinkedIn Kafka Monitor 为例](chapter/tools/DevOps-Kafka-Monitor.md)
* [计算机远程通信协议：从 CORBA 到 gRPC](chapter/distributed/Protocol-gRPC.md)
* [分布式追踪系统体系概要](chapter/distributed/DevOps-OpenTracing.md)
* [开源分布式跟踪系统 OpenCensus](chapter/distributed/DevOps-OpenCensus.md)
* [Chapter 5: Cyber-Security|网络与信息安全篇]()
* [黑客入侵导致的性能问题](chapter/security/CyberSecurity-SSH.md)
* [基于数据分析的网络态势感知](chapter/security/Network-sFlow.md)
* [网络数据包的捕获、过滤与分析](chapter/security/Network-Pcap.md)
* [WEB 应用安全、攻击、防护和检测](chapter/security/CyberSecurity-Headers.md)
* [警惕 Wi-Fi 漏洞 KRACK](chapter/security/CyberSecurity-Headers.md)
* [Cyber-Security & IPv6](chapter/security/Protocol-IPv6.md)
* [Linux 容器安全的十重境界](chapter/security/DevOps-Container-Security.md)
* [美国网络安全立法策略](chapter/security/law.md)
* [香港警务处网络安全与科技罪案调查科](chapter/security/CyberSecurity-CSTCB.md)
* [Chapter 6: 工程管理篇]()
* [Oracle 数据库迁移与割接实践](chapter/thinking/Technology-Oracle.md)
* [PostgreSQL 数据库的时代到来了吗](chapter/distributed/OpenSource-DB-PostgreSQL.md)
* [珠海航展交通管控实践经验借鉴](chapter/thinking/Network-Traffic.md)
* [基于看板（Kanban）的管理实践](chapter/thinking/Teamwork-Kanban.md)
* [DevOps 漫谈:从作坊到工厂的寓言故事](chapter/thinking/DevOps-Phoenix.md)
* [工程师的自我修养：全英文技术学习实践](chapter/thinking/Technology-English.md)
* [Chapter 7: 社区文化篇]()
* [谁是王者：macOS vs Linux Kernels ？](chapter/culture/Linux-Win-Mac.md)
* [Linus Torvalds：The mind behind Linux](chapter/culture/Linus.md)
* [Linus Torvalds：人生在世，Just for Fun](chapter/culture/Linus_JustForFun.md)
* [IT 工程师养生指南](chapter/culture/Health.md)

## Community
更多精彩内容请扫码关注公众号,[RiboseYim's Blog:riboseyim.github.io](https://riboseyim.github.io?product=ebook&id=linuxperfmaster)
![微信公众号@睿哥杂货铺](http://o8m8ngokc.bkt.clouddn.com/ID_RiboseYim_201706.png)

### 读者交流
- **读者QQ群：338272982**
- [简书专题：《系统运维专家》](http://www.jianshu.com/c/9a817d8a67ea)
- [小密圈:@系统运维专家](http://t.xiaomiquan.com/U7qn6Qv)

![小密圈@系统运维专家](http://o8m8ngokc.bkt.clouddn.com/riboseyim_id_quanzi_ops_small.png)

### Latest Version
https://www.gitbook.com/book/riboseyim/linux-perf-master/details

### 快捷下载

- [Edition 0.4 20180714](https://pan.baidu.com/s/1C20TAKtYxXeRkTjNy43WOQ)

## Thanks
Thanks to my family and colleagues.

<hr>

## License

版权声明：自由转载-非商用-非衍生-保持署名 | [Creative Commons BY-NC-ND 4.0](https://creativecommons.org/licenses/by-nc-nd/4.0/legalcode)
