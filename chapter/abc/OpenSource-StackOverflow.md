# 全栈架构技术视野：以 Stack Overflow 为例

## 摘要
> Stack Overflow 架构解析，其架构既有商业外包服务，也大量采用开源软件，可以全景式展现当代主流架构的风貌。
Stack Overflow 由 Jeff Atwood 和 Joel Spolsky 这两个非常著名的 Blogger 在 2008 年创建。

![](http://riboseyim-qiniu.riboseyim.com/StackOverflow-2.png)

>As of April 2014, Stack Overflow has over 4,000,000 registered users[19]and more than 10,000,000 questions,[20]with 10,000,000 questions celebrated[21]in late August 2015. Based on the type oftagsassigned to questions, the top eight most discussed topics on the site are:Java,JavaScript,C#,PHP,Android,jQuery,PythonandHTML。——wiki

# 总体架构

Stack Overflow 可以分解为八个切面：互联网、负载均衡、web层、服务层、缓存、推送、搜索、数据库。

#### First Rule:Everything is redundant
两个数据中心：纽约和科罗拉多，冗余且持续备份。其它所有关键组件都尽可能贯彻冗余原则。

![全景视图](http://riboseyim-qiniu.riboseyim.com/StackOverflow-1.png)

#### 物理架构
- 4 台 Microsoft SQL Server 服务器（其中 2 台使用了新的硬件）
- 11 台 IIS Web 服务器（新的硬件）
- 2 台 Redis 服务器（新的硬件）
- 3 台标签引擎服务器（其中 2 台使用了新的硬件）
- 3 台 Elasticsearch 服务器（同上）
- 4 台 HAProxy 负载均衡服务器（添加了 2 台，用于支持 CloudFlare）
- 2 台网络设备（Nexus 5596 核心 + 2232TM Fabric Extender，升级到 10Gbps 带宽）
- 2 台 Fortinet 800C 防火墙（取代了 Cisco 5525-X ASAs）
- 2 台 Cisco ASR-1001 路由器（取代了 Cisco 3945 路由器）
- 2 台 Cisco ASR-1001-x 路由器

#### 逻辑架构
The Internets 互联网
DNS服务：外包CloudFlare + 自建DNS
其实外包DNS服务应该已经可以满足服务，不过出于保险起见，还是有一套自建的DNS Server。

Load Balancers 负载均衡
HAProxy 1.5.15 on CentOS 7
支持TLS (SSL)流量。关注HAProxy 1.7,它即将支持HTTP/2。

引入开源架构之后，就必须持续关注、跟进社区的发展动态。吃着碗里的，看着锅里的，永远不能停。

- Web Tier Web层
- IIS 8.5, ASP.Net MVC 5.2.3, and .Net 4.6.1
- Service Tier 服务层
- IIS, ASP.Net MVC 5.2.3, .Net 4.6.1, and HTTP.SYS
- Cache 缓存
- Redis

L1级别：HTTP 缓存
L2级别：L1级别缓存失败之后，通过Redis获取数据
L1&L2都是无法命中的情况下，会从数据库查询，并更新到缓存和Redis。

缓存更新：基于发布／订阅模型，利用这个机制来清除其他服务上的 L1 缓存，用来保持 web 服务器上的缓存一致性。(另外Redis实例的CPU都很低，不到2%，这点很惊人。)
![](http://riboseyim-qiniu.riboseyim.com/SO-Architecture-Redis-Utilization.png)

#### Push推送
开源库：NetGrain
使用 [Websocket](http://www.ruanyifeng.com/blog/2017/05/websocket.html) 向用户推送实时的更新内容，比如顶部栏中的通知、投票数、新导航数、新的答案和评论。在高峰时刻，大约有 50 万个并发的  [Websocket](http://www.ruanyifeng.com/blog/2017/05/websocket.html)  连接，这可是一大堆浏览器。

一个有趣的事实：其中一些浏览器已经打开超过 18 个月了。Someone should go check if those developers are still alive！！

问题：临时端口、负载均衡上的文件句柄耗尽，都是非常有趣的问题，我们稍后会提到它们。

#### Search搜索
Elasticsearch集群，每个ES集群都有3个Node
什么不用Solr？我们需要在整个网络中进行搜索（同时有多个索引），在我们进行决策的时候 Solr 还不支持这种场景。

还没有使用 2.x 版本的原因，是因为2.x 版本中类型（types）有了很大的变化，这意味着想要升级的话我们得重新索引所有内容。

没有足够的时间来制定需求变更和迁移的计划。

#### Database数据库

- SQLServer

>Our usage of SQL is pretty simple. Simple is fast.

数据库中只有一个存储过程，而且我打算把这个最后残留的存储过程也干掉，换成代码。

#### 监控系统
- Opserver：轻量级监控系统，基于 asp.net MVC 框架，可监控：Servers
- SQL clusters/instances
- redis
- elastic search
- exception logs
- haproxy

![](http://riboseyim-qiniu.riboseyim.com/SO-Architecture-Opserver-HAProxy.png)

![](http://riboseyim-qiniu.riboseyim.com/SO-Architecture-Opserver-DBTier.png)

![](http://riboseyim-qiniu.riboseyim.com/SO-Architecture-Opserver-WebTier.png)

数据库 CPU 利用率非常低

# 硬件部分

有人对硬件感兴趣吗？好吧，我感兴趣，这篇博客就是关于这个话题，所以，我赢了。如果你不关系硬件，那么可以走开并关闭浏览器了。还在这儿吗？真棒。
假如你的网页访问非常非常慢，在这种情况下，你应该考虑采购一些新的硬件。

我曾今反复重申过多次：性能是一个重要组件。
特别是当你的代码必须在最快的硬件上运行，硬件的关系则越为重大。正如任何其它的平台，Stack Overflow的架构是分层的。硬件对我们来说属于基础层，它有自己的屋子，在很多情况下，对我们来说，它的许多关键组件是不可控的。。。就像运行在别人的服务器。它也伴随着直接和间接的成本。但是，这些不是本篇文章的重点，这方面的对比将于稍后报告。目前来说，我希望能提供一份详细的，关于我们基础设施的清单，用于大家参考和比较。

服务器照片。有时是裸设备。
这个网页可以加载得更快，但是我不能自禁。（言归正传）
在这个系列报告中我将提供大量数字和规格说明。
当我说“我们的SQL Server CPU利用率接近5-10%，” 好吧，这非常棒。
但是，5-10% 的什么？ 这时我们需要一个参考值。这份硬件清单可以回答这些问题，并且座位与其它平台比较的依据，利用率对比如何，容量对比如何，等等。

### How We Do Hardware
免责声明：我不是一个人干的。

George Beech (@GABeech) 是我的主要搭档，盘点管控Stack使用的硬件。
我们小心地规范每一台服务器，以使它符合设计意图。 我们不会只管下订单、分派任务。在这个过程中我们也不会自己单独完成；你必须知道将来这些硬件需要运行什么东西，才能做出合适的选择。我们将和开发工程师或者其他的可靠性工程师一道，为运行在盒子上的应用选择最佳方案。我们也关注在整个系统中什么才是最好的。每一台服务器都不是孤岛。如何将它嵌入到总体的架构中去，确实需要好好考量。哪些服务可以全平台共享？数据中心？日志系统？管理更少的事情，或者至少做到更少的差异，这件事本身就具有内在的价值。

当我们盘点硬件的时候，我们列出了很多规则来帮助我们厘清哪些是需要提供的。
我还从没有真正写下这些心里面的检查表，简短来说：

- 这是一个升级或降级的问题吗？（我们购买一个更大的机器，或者一些更小的？
- 我们需要／希望做到什么程度的冗余? (多少预留空间和故障恢复能力?)
- **存储:**
- 服务器／应用需要挂在磁盘吗？(我们是否需要Spinny操作系统驱动?)
- 如果是，需要多少？（多大的网络带宽？有多少小文件？是否需要固态硬盘？）
- 如果是SSD（固态硬盘），是否写负载？（我们讨论 Intel S3500/3700s? P360x? P3700s?)
- 我们需要多少SSD容量? (是否可以采用同时搭载HDD（机械硬盘）的双轮方案?)
- 数据是否需要完全缓存？（相比没有电容器的SSD，哪一种更便宜，哪种更合适？）
- 将来存储是否需要扩展? (我们采用1U/10-bay 服务器, 或者一个 2U/26-bay 服务器?)
- 这是一个数据仓库的场景设定吗？（我们是否考虑3.5’’驱动器？如果是，每个2U主板上是12个还是16个驱动器？）
- 对于3.5’’的后板来说，存储平衡在在处理器上是否能达到 120W TDP 的限制?
- 我们是否需要直接显示磁盘？（控制器是否需要支持pass-through?)
- **内存:**
- 它需要多少内存？（我们必须买什么？）
- 它将会使用多少内存？（我们最好买什么？）
- 我们是否认为它稍后需要更多的内存？（我们应该搭配那种内存频率？）
- 它是一个内存消耗型应用程序吗？（我们是否想要达到最大主频？）
- 它是一个高并发的应用程序吗？（一定空间的情况下，我们是否想要通过更多的DIMM来分摊内存？）
- **CPU:**
- 我们希望采用哪种类型的处理器？（我们需要CPU自己供电还是独立电源？）
- 它是高并发的应用程序吗？（我们希望采用更少、更快的内核？或者，采用数量更多，更慢的内核？）
- 以下哪种情况？是否存在大量的二级和三级缓存竞争？（为了提高性能，我们是否需要一个巨大的三级缓存？）
- 应用瓶颈主要是单一内核吗？（我们是否采用最大主频？）
- 如果是这样的话，同时需要支持多少进程数？（这里我们希望采用哪种引擎？）
- **网络:**
- 我们是否需要增加 10Gb 网络连接？（此处是否为透传设备，例如一个负载均衡器？）
-  我们需要怎样的出／入流量均衡策略？（哪个CPU内核负责计算均衡权重？）

- **冗余:**
- 我们在数据缓存中心是否也需要服务器？
- 我们是否需要在同等数量的情况下，接受更低的冗余要求？
- 我们是否需要一个电源线？不。我们不需要。

现在，让我们来看看服务网站的都有哪些硬件，它们位于纽约 （New York）QTS 数据中心。实际上，它位于新泽西（New Jersey)，但是让我们保持这个约定。为什么我们称之为NY数据中心？因为我们不想重命名所有以NY-开头的服务器。（What ?!…）我将记录在下面的清单上，丹佛的情况，在规格和冗余级别上略有差别。

Hide Pictures (in case you’re using this as a hardware reference list later)

### Stack Overflow & Stack Exchange 站点服务器

#### 纽约数据中心
**全局选项**
先说明一些全局配置，在下面每台服务器的介绍里就不重复了：
- 除非有特殊需要，不包含操作系统驱动。大多数服务器使用一对250 或者 500 GB SATA HDD 硬盘 ，用于操作系统，通常是 RAID 1。我们不担心启动时间问题，所有物理服务器，启动时间中的大部分不依赖驱动的速度（例如，检查768GB内存）。
- 所有服务器通过2个或以上10Gb网络链路连接，通过双活LACP协议。
- 所有服务器运行在208V 单相功率电源 (经由2个PSU ，来自2个PDU-双电源).
- 在纽约的所有服务器由缆线臂，在丹佛的服务器则没有（主要依靠本地工程师）。
- 所有服务器都有一个iDRAC连接 (经由管理网络) 和一个KVM连接。

**网络**
- 2x Cisco Nexus 5596UP 核心交换机 (96 SFP+ 端口，每个端口 10 Gbps)
- 10x Cisco Nexus 2232TM Fabric Extenders (2 per rack - each has 32 BASE-T ports each at 10Gbps + 8 SFP+ 10Gbps 上联链路)
- 2x Fortinet 800C 防火墙
- 2x Cisco ASR-1001 路由器
- 2x Cisco ASR-1001-x 路由器
- 6x Cisco 2960S-48TS-L 网管交换机 (1 Per Rack - 48 1Gbps ports + 4 SFP 1Gbps)
- 1x Dell DMPU4032 KVM
- 7x Dell DAV2216 KVM Aggregators (1–2 per rack - each uplinks to the DPMU4032)

原作者备注: 每个 FEX 到核心 拥有 80 Gbps 上联带宽 ，核心通过一个160 Gbps端口通道与它们连接。由于最近的一些工程，我们位于丹佛数据中心的硬件会更新一些。所有4 台路由器的型号是 ASR-1001-x 和 双核 Cisco Nexus 56128P,每个都拥有96 SFP+ 10Gbps 端口 和 8 QSFP+ 40Gbps 端口。这些节省下来的端口，可以用于未来扩展，我们可以为核心绑定4x 40Gbps链接，替代每个 16x10Gbps端口的方案，正如我们在纽约做的那样。这些就是纽约的网络设备情况:

![SO-Hardware-Network-NewYork-Fiber-Small.jpg](http://riboseyim-qiniu.riboseyim.com/SO-Hardware-Network-NewYork-Fiber-Small.jpg)
![SO-Hardware-Network-NewYork-Fortinet-Small.jpg](http://riboseyim-qiniu.riboseyim.com/SO-Hardware-Network-NewYork-Fortinet-Small.jpg)
![SO-Hardware-Network-NewYork-Rack-Small.jpg](http://riboseyim-qiniu.riboseyim.com/SO-Hardware-Network-NewYork-Rack-Small.jpg)

#### 丹佛数据中心
这里需要提到的是Mark Henderson, 我们网站的可靠性工程师之一，专程到纽约数据中心为我的这份报告拿到了一些高分辨率的照片。
![SO-Hardware-Network-Denver-Installed-Small.jpg](http://riboseyim-qiniu.riboseyim.com/SO-Hardware-Network-Denver-Installed-Small.jpg)
![SO-Hardware-Network-Denver-Racked-Small.jpg](http://riboseyim-qiniu.riboseyim.com/SO-Hardware-Network-Denver-Racked-Small.jpg)
![SO-Hardware-Network-Denver-Raw-Small.jpg](http://riboseyim-qiniu.riboseyim.com/SO-Hardware-Network-Denver-Raw-Small.jpg)

**SQL Servers (Stack Overflow 集群)**
- 2 Dell R720xd 服务器，每台配置如下:
- 双 E5-2697v2 处理器 (每个 12 核 @2.7–3.5GHz)
- 384 GB of RAM (24x 16 GB DIMMs)
- 1x Intel P3608 4 TB NVMe PCIe SSD (RAID 0, 2块卡上两个控制器)
- 24x Intel 710 200 GB SATA SSDs (RAID 10)
- 双 10 Gbps 网络 (Intel X540/I350 NDC)
**SQL Servers (Stack Exchange 及其它业务集群)**
- 2 Dell R730xd Servers, each with:
- 双 E5-2667v3 处理器 (每个8 核 @3.2–3.6GHz)
- 768 GB of RAM (24x 32 GB DIMMs)
- 3x Intel P3700 2 TB NVMe PCIe SSD (RAID 0)
- 24x 10K Spinny 1.2 TB SATA HDDs (RAID 10)
- 双 10 Gbps 网络 (Intel X540/I350 NDC)
原作者备注: 丹佛的SQL硬件在规格上相同，对应纽约部分这里只有一个 SQL 服务器
这是二月份为纽约的SQL Server 升级PCIe SSD的情形：

![SO-Hardware-SQL-Front-Small.jpg](http://riboseyim-qiniu.riboseyim.com/SO-Hardware-SQL-Front-Small.jpg)
![SO-Hardware-SQL-Inside-Small.jpg](http://riboseyim-qiniu.riboseyim.com/SO-Hardware-SQL-Inside-Small.jpg)
![SO-Hardware-SQL-SSDs-Small.jpg](http://riboseyim-qiniu.riboseyim.com/SO-Hardware-SQL-SSDs-Small.jpg)
![SO-Hardware-SQL-Top-Small.jpg](http://riboseyim-qiniu.riboseyim.com/SO-Hardware-SQL-Top-Small.jpg)

**Web 服务器**
- 11 Dell R630 服务器，每台配置如下:
- 双 E5-2690v3 处理器 (每个12 核 @2.6–3.5GHz)
- 64 GB of RAM (8x 8 GB DIMMs)
- 2x Intel 320 300GB SATA SSDs (RAID 1)
- 双 10 Gbps 网络 (Intel X540/I350 NDC)

**应用服务器 (Workers)**
- 2 Dell R630 服务器, 每台配置如下:
- 双 E5-2643 v3 处理器(每个 6 核 @3.4–3.7GHz)
- 64 GB of RAM (8x 8 GB DIMMs)
- 1 Dell R620 服务器,配置如下:
- 双 E5-2667 处理器 (每个6 核 @2.9–3.5GHz)
- 32 GB of RAM (8x 4 GB DIMMs)
- 2x Intel 320 300GB SATA SSDs (RAID 1)
- 双 10 Gbps 网络 (Intel X540/I350 NDC)

![SO-Hardware-Web-Tier-Back-Small.jpg](http://riboseyim-qiniu.riboseyim.com/SO-Hardware-Web-Tier-Back-Small.jpg)
![SO-Hardware-Web-Tier-Front-Small.jpg](http://riboseyim-qiniu.riboseyim.com/SO-Hardware-Web-Tier-Front-Small.jpg)
![SO-Hardware-Web-Tier-Front2-Small.jpg](http://riboseyim-qiniu.riboseyim.com/SO-Hardware-Web-Tier-Front2-Small.jpg)
![SO-Hardware-Web-Tier-Unboxed-Small.jpg](http://riboseyim-qiniu.riboseyim.com/SO-Hardware-Web-Tier-Unboxed-Small.jpg)

原作者备注: NY-SERVICE03 目前仍然是一台 R620, 但是现在并没有足够老到以至于需要更换。它会在今年晚些时候升级。

**Redis 服务器 (缓存)**
- 2 Dell R630 服务器, 每台配置如下:
- 双 E5-2687W v3 处理器 (每个10 核 @3.1–3.5GHz)
- 256 GB of RAM (16x 16 GB DIMMs)
- 2x Intel 520 240GB SATA SSDs (RAID 1)
- 双 10 Gbps 网络 (Intel X540/I350 NDC)

**Elasticsearch 服务器 (检索)**
- 3 Dell R620 服务器, 每台配置如下:
- 双 E5-2680 处理器 (每个8 核 @2.7–3.5GHz)
- 192 GB of RAM (12x 16 GB DIMMs)
- 2x Intel S3500 800GB SATA SSDs (RAID 1)
- 双 10 Gbps 网络 (Intel X540/I350 NDC)

**HAProxy 服务器 (负载均衡器)**
- 2 Dell R620 服务器 (流量来源CloudFlare), 每台配置如下:
- 双 E5-2637 v2 处理器 (每个4 核 @3.5–3.8GHz)
- 192 GB of RAM (12x 16 GB DIMMs)
- 6x Seagate Constellation 7200RPM 1TB SATA HDDs (RAID 10) (日志)
- 双 10 Gbps 网络 (Intel X540/I350 NDC) - (DMZ)内网流量
- 双 10 Gbps 网络 (Intel X540) - 外网流量
- 2 Dell R620 服务器 (直达流量), 每台配置如下:
- 双 E5-2650 处理器 (每个 8 核 @2.0–2.8GHz each)
- 64 GB of RAM (4x 16 GB DIMMs)
- 2x Seagate Constellation 7200RPM 1TB SATA HDDs (RAID 10) (日志)
- 双 10 Gbps 网络 (Intel X540/I350 NDC) - (DMZ) 外网流量
- 双 10 Gbps 网络 (Intel X540) - 外网流量

原作者备注: 这些服务器是不同时期采购的，因此规格上略有差异。并且，2台CloudFlare负载均衡器因为安装了memcached,拥有更多内存（我们现在已经不运行该组件）。这些服务，redis, 检索,和负载均衡器在stack都是基于1U 服务器。
这是纽约的情况：

![SO-Hardware-Redis-Inside-Small.jpg](http://riboseyim-qiniu.riboseyim.com/SO-Hardware-Redis-Inside-Small.jpg)
![SO-Hardware-Service-Inside-Small.jpg](http://riboseyim-qiniu.riboseyim.com/SO-Hardware-Service-Inside-Small.jpg)
![SO-Hardware-Service-Rear-Small.jpg](http://riboseyim-qiniu.riboseyim.com/SO-Hardware-Service-Rear-Small.jpg)
![SO-Hardware-Service-Redis-Search-Front-Small.jpg](http://riboseyim-qiniu.riboseyim.com/SO-Hardware-Redis-Search-Front-Small.jpg)

### 其它服务器
我们还有一些其他的服务器并不直接或间接服务于网站的流量。
它们负责处理一些相关业务（例如，域名控制器，少量用于应用验证，跑在虚拟机上），或者一些次要的采购用于监控，日志存储，备份等等。既然已经表示未来会做一系列的报告，我把一切有趣的“后台”服务器也列出来。使我可以将更多的服务器拿出来和你分享，有人不喜欢的吗？

**VM 服务器 (VMWare, 当前)**
- 2 Dell FX2s Blade Chassis, each with 2 of 4 blades populated
- 4 Dell FC630 Blade Servers (2 per chassis), each with:
- 双 E5-2698 v3 处理器 (每个16 核 @2.3–3.6GHz)
- 768 GB of RAM (24x 32 GB DIMMs)
- 2x 16GB SD Cards (Hypervisor - no local storage)
- 双 4x 10 Gbps 网络 (FX IOAs - BASET)
- 1 EqualLogic PS6210X iSCSI SAN
- 24x Dell 10K RPM 1.2TB SAS HDDs (RAID10)
- 双 10Gb 网络 (10-BASET)
- 1 EqualLogic PS6110X iSCSI SAN
- 24x Dell 10K RPM 900GB SAS HDDs (RAID10)
- 双 10Gb 网络 (SFP+)

![SO-Hardware-VMs-Blades-Small.jpg](http://riboseyim-qiniu.riboseyim.com/SO-Hardware-VMs-Blades-Small.jpg)
![SO-Hardware-VMs-Blades2-Small.jpg](http://riboseyim-qiniu.riboseyim.com/SO-Hardware-VMs-Blades2-Small.jpg)
![SO-Hardware-VMs-Front-Small.jpg](http://riboseyim-qiniu.riboseyim.com/SO-Hardware-VMs-Front-Small.jpg)
![SO-Hardware-VMs-Rear-Small.jpg](http://riboseyim-qiniu.riboseyim.com/SO-Hardware-VMs-Rear-Small.jpg)

在一些场景下，还有几台重要的服务器不是虚拟机。这些系统后台任务，帮助我们通过日志追踪排查问题，存储大量的数据等等。

**机器学习服务器 (Providence)**
这些服务器99%的时间是空闲的，但是每晚承担了大量的处理工作：刷新Providence。它们也可以通过内部数据中心的方式，用来测试基于海量数据的新算法。
- 2 Dell R620 服务器, 每台配置如下:
- 双 E5-2697 v2 处理器 (每个 12 核 @2.7–3.5GHz)
- 384 GB of RAM (24x 16 GB DIMMs)
- 4x Intel 530 480GB SATA SSDs (RAID 10)
- 双 10 Gbps 网络 (Intel X540/I350 NDC)

译者注：Providence，应为项目代号。Providence通过分析流量日志，给网站的访问用户打标签 (类似“web开发者” 或者 “使用Java技术栈”)  。详细可以查阅[https://kevinmontrose.com/2015/01/27/providence-machine-learning-at-stack-exchange/]

**机器学习服务器－Redis (Still Providence)**
这是一个为 Providence服务的redis数据集。它们通常是一台主用，一台备用，还有一个实例是用于测试，如最新版的ML算法。当它不用做Q&A站点时，这些数据会服务于职位招聘的边栏广告。
- 3 Dell R720xd 服务器,每台配置如下:
- Dual E5-2650 v2 Processors (8 cores @2.6–3.4GHz each)
- 384 GB of RAM (24x 16 GB DIMMs)
- 4x Samsung 840 Pro 480 GB SATA SSDs (RAID 10)
- Dual 10 Gbps network (Intel X540/I350 NDC)

**日志服务器(各种日志）**
我们的 Logstash 集群 (使用 Elasticsearch 存储) ，数据来源于，任何地方。
我们曾计划将HTTP日志复制一份到这些服务器，但是由于影响性能的问题而没有实现。尽管如此，我们还是将所有的网络设备日志，syslog，Windows和Linux系统日志存在这里，所以我们能够建立建立一个网络的全局视图，或者快速地排查问题。当告警发生的时候，它也被用作Bosun的一个数据源。这个集群总计使用的存储是 6x12x4 = 288 TB。
- 6 Dell R720xd 服务器, 每台配置如下:
- Dual E5-2660 v2 Processors (10 cores @2.2–3.0GHz each)
- 192 GB of RAM (12x 16 GB DIMMs)
- 12x 7200 RPM Spinny 4 TB SATA HDDs (RAID 0 x3 - 4 drives per)
- 双 10 Gbps 网络 (Intel X540/I350 NDC)

**SQL Server－HTTP日志**
在这些服务器，我们将访问负载均衡器的单独HTTP请求，存储到SQL数据库(来源于HAProxy syslog）。我们只记录少数高级别的请求，类似URL，查询，UserAgent,SQL执行时间，Redis，等等。在这里的数据，每天将进入一个集群的Columnstore 索引。我们借助这些数据排查用户的问题，发现僵尸网络，等等。

- 1 Dell R730xd 服务器，配置如下:
- 双 E5-2660 v3 处理器 (每个10 核 @2.6–3.3GHz)
- 256 GB of RAM (16x 16 GB DIMMs)
- 2x Intel P3600 2 TB NVMe PCIe SSD (RAID 0)
- 16x Seagate ST6000NM0024 7200RPM Spinny 6 TB SATA HDDs (RAID 10)
- 双 10 Gbps 网络 (Intel X540/I350 NDC)

**SQL Server  - 开发**
我们喜欢尽可能多地模拟生产环境，类似SQL匹配，额，至少是它过去常常发生的那样。们一直以来这购买升级生产处理器。我们会将升级这些服务器，采用2U 解决方案，在今年晚些升级Stack Overflow 集群的时候一起做。
- 1 Dell R620 服务器，配置如下:
- 双 E5-2620 处理器 (每个6核 @2.0–2.5GHz)
- 384 GB of RAM (24x 16 GB DIMMs)
- 8x Intel S3700 800 GB SATA SSDs (RAID 10)
- 双 10 Gbps 网络 (Intel X540/I350 NDC)

![SO-Hardware-Racks-Small.jpg](http://riboseyim-qiniu.riboseyim.com/SO-Hardware-Racks-Small.jpg)

![SO-Hardware-Racks2-Small.jpg](http://riboseyim-qiniu.riboseyim.com/SO-Hardware-Racks2-Small.jpg)
