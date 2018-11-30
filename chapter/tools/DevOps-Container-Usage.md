# 2018 Docker 用户报告 - Sysdig Edition

## 摘要
- 应用排行榜
- 容器运行环境
- 容器编排器
- 容器监控

This article is part of an **Virtualization Technology** tutorial series. Make sure to check out my other articles as well:
- [2018 年度 Docker 用户报告 - Sysdig Edition ](https://riboseyim.github.io/2018/06/12/DevOps-Container-Usage/)
- [Cyber-Security: Linux 容器安全的十重境界](https://riboseyim.github.io/2017/11/12/DevOps-Container-Security/)
- [DevOps漫谈：Docker ABC](https://riboseyim.github.io/2017/08/21/DevOps-Docker/)

根据 [Sysdig](https://sysdig.com/opensource/) 发表的年度 [Docker]((https://riboseyim.github.io/2017/08/21/DevOps-Docker/) 用户报告，在容器市场 Docker 仍然是事实上的行业标准，但是其它品牌的容器运行环境正在发展；Kubernetes 仍然是容器编排领域的王者。报告的数据来源主要依据 Sysdig Monitor 和 Sysdig Secure cloud service 提供的容器使用状况的实时快照报告，它们从容器健康、性能和安全性等方面提供度量指标和可视化服务。样本集包括垂直行业和各类规模不等的大中型企业，地域覆盖北美洲、拉丁美洲、EMEA（欧洲、中东、非洲）和亚太地区。与去年一样，这份报告并不是用来代表整个容器市场。因为数据仅限于 Sysdig 客户，所以对于那些选择商业和开源解决方案的公司来说不具有代表性。但是来自 90000 个容器用户汇总数据，确实提供了了解真实生产环境容器使用状况的独特视角。

#### 容器应用排行榜

容器部署应用榜首：Java Virtual Machines (JVM)。在容器时代之前，Java 就广泛应用于企业级服务，目前两者 —— Java 和容器更加紧密地融合到了一起。

我们还看到数据库解决方案的使用在增加, 例如在容器环境中运行 [PostgreSQL]((https://riboseyim.github.io/2018/01/03/OpenSource-DB-PostgreSQL/)) 和 MongoDB 。这是一个信号, 表明在容器中部署有状态服务已经成为现实。容器的短暂性，让许多人对于在容器中运行高价值数据服务抱有怀疑态度, 但是市场回答了问题的解决方案--即为微服务设计的持久、便携和共享存储。数据显示, 客户开始转向完全由容器驱动的环境。

![](http://riboseyim-qiniu.riboseyim.com/Docker_2018_01.png)

#### 容器密度

在2017年每个主机的容器数的中位数是 10 。2018年，这个数字上升到 15，同比增长 50% 。另一方面，我们看到一个客户的单台主机上运行了 154 个容器，比我们去年观察到的最大 95 个增长了。

![](http://riboseyim-qiniu.riboseyim.com/Docker_2018_02.png)

![](http://riboseyim-qiniu.riboseyim.com/Docker_2018_03.png)

#### 容器运行环境

>Docker still reigns, but we’re seeing what might be the first signs of cracks in the dam.

事实上的容器运行环境依然是[Docker]((https://riboseyim.github.io/2017/08/21/DevOps-Docker/)。我们在 2017 年的报告中没有提及其他容器运行环境的详细信息, 因为在当时 Docker 的占有率接近 99% 。但是, 鉴于最近的一些变化： Red Hat  收购 CoreOS 的 (RKT 的制造商)，以及 Open Container Initiative (OCI) 项目 — 旨在推进容器运行环境和镜像标准化。

事实上，在过去的一年里, 客户对其他平台的使用增加了。CoreOS RKT 显著增长到 12% , Mesos containerizer 占有 4% 。LXC 也在增长, 尽管从业人员规模比例还较低。数据显示, 客户在生产环境中使用 "non-Docker" 解决方案更加便利了。

![Container runtimes: Docker leads, followed by rkt and Mesos.](http://riboseyim-qiniu.riboseyim.com/Docker_2018_04.png)

#### 容器存活周期

95% 的容器存活时间低于一周。

![](http://riboseyim-qiniu.riboseyim.com/Docker_2018_05.png)

容器和服务的生存时间是多少？ 我们观察了容器、容器镜像和服务的数量, 它们在短时间内开始并停止, 存活10秒或更短, 或者一周或更长。下图显示不同间隔内的容器百分比。 11% 的容器活了不到10秒。大部分容器（27%） 的生存期在五分钟之内。

为什么这么多的容器寿命如此之短呢？我们知道许多定制的系统都是按照需求来扩展的。容器被创建, 做他们的工作, 然后离开。例如, 一个客户为他们在 Jenkins 创建的每个作业配置一个容器，执行变更测试, 然后关闭容器。对他们来说, 类似活动每天会发生上千次。（Jenkins：一个用 Java 编写的开源持续集成工具，MIT许可证。它支持软件配置管理工具，如CVS、Subversion 和 Git 等，可以执行基于 Apache Ant 和 Apache Maven 的项目，以及任意的Shell 脚本/批处理命令。）

#### 镜像存活周期

我们还观察了容器镜像的使用时间。通过查看这些数据, 我们了解到客户在 DevOps CI/CD 流程的一部分中是如何频繁地进行新的容器更新部署的。 一小部分 -- 一个百分点--在不到10秒内更新。69% 的容器镜像在一周的跨度内更新。

![](http://riboseyim-qiniu.riboseyim.com/Docker_2018_06.png)

#### 服务存活周期

"服务的寿命是多少？" 在 Kubernetes 中, 服务抽象定义了一组提供特定函数以及如何访问它们的 Pods 。服务允许 Pods 在不影响应用程序的情况下注销和复制。例如, 一个群集可以运行一个 Node.js  JavaScript 运行时服务、MySQL 数据库服务和 NGINX 前端服务。

我们看到大多数服务(67%)生存期超过一周。少量的服务在更频繁的基础上被停止, 但是对于大多数客户来说, 目标是让应用程序 24 小时持续工作。容器和 Pods 可能会来了又走, 但是服务持续处于启动并且可用状态。

![Most container-based services stay up beyond a week.](http://riboseyim-qiniu.riboseyim.com/Docker_2018_07.png)

#### 容器编排器

>First place goes to Kubernetes, followed by Kubernetes and then Kubernetes.

例如, Mesosphere 能够在 DC/OS 环境中部署和管理 "Kubernetes-as-a-service"。可以将多个 Kubernetes 群集部署在一个 Mesosphere 群集上。

今年 Docker Swarm 的排名上升到第二位, 超过了基于 Mesos 的工具。根据 Sysdig ServiceVision 我们能自动标识出是否使用编排器, 并将逻辑基础结构对象与容器度量关联起来。在 2018 年, Kubernetes 可以确保领先地位。

1. Swarm 的进入门槛
例如, 微软使用 Kubernetes 为其 Azure Kubernetes 服务 (AKS), IBM 的云容器服务和私有云产品也是基于 Kubernetes 。即使是 Docker 和 Mesosphere 也增加支持了 Kubernetes 的功能。

2. Docker 企业版, 具有通用控制平面 (Universal Control Plane (UCP) ), 在许多操作层面上降低了启动 Swarm 的门槛。

![编排器份额： Kubernetes 和 Swarm 增长, Mesos 萎缩。](http://riboseyim-qiniu.riboseyim.com/Docker_2018_08.png)

#### 容器集群大小

>Mesos owns the big cluster game.

”集群大小对与组织选择编排器的影响是什么？“
这项研究显示基于 Mesos 的编排器, 包括 Mesos Marathon 和 Mesosphere DC/OS 降至第三位。在使用 Mesos 的地方, 部署的容器数（中位数）比 Kubernetes 环境多 50% 。鉴于 Mesos 倾向于在大规模的容器和云部署, 所以这是有意义的。因此, 虽然 Mesos 集群的数量较少, 但是 Mesos 集群通常是意味着更大的企业规模。

我们的客户, 往往是更大的企业（在私有数据中心运行 Sysdig 解决方案）采用 OpenShift 的数量比我们的 SaaS 客户数量还要多。 Rancher Labs 于 2015 年出现, 为 Docker Swarm 和 Kubernetes 提供支持。直到 2017 年, Rancher （“大农场主”）才完全兼容 Kubernetes 作为其编排器。

![Mesos clusters 50% larger than Kubernetes. Swarm 30% smaller.](http://riboseyim-qiniu.riboseyim.com/Docker_2018_09.png)


#### Kubernetes 分发版

今年我们分析了使用 Kubernetes 的“品牌”分布, 看看在使用的 Kubernetes 是开源版本, 或由特定供应商提供的软件包。我们发现开源 Kubernetes 继续占有最大的份额, 但是 OpenShift 似乎正在取得突破进展, Rancher 也占有了一些份额。

OpenShift 获得接受不应该是一个惊喜。Kubernetes 于 2014 年诞生于 Google , Red Hat 也发布了该平台的 OpenShift 分发版, 并提出了针对企业客户实现 Kubernetes 的目标。

![Open source Kubernetes most used, followed by OpenShift and Rancher distributions.](http://riboseyim-qiniu.riboseyim.com/Docker_2018_10.png)

#### 容器健康与应用性能监控

了解用户体验的四个“黄金信号” ：延迟（latency），流量（traffic），错误（errors）和饱和度（saturation）。
响应时间（Response time）是配置最广泛的告警类型，紧随其后的是正常运行时间（uptime）和停机告警。基于主机的告警是最常用的，包括主要资源指标 -  CPU ，内存和磁盘使用率等仍然被广泛使用。用户想知道托管 Docker 的服务器（物理机，虚拟机或云实例）是否处于资源紧张或达到容量上限的状态。这些告警的触发条件通常设置在利用率达到 80%-95％ 之间。

同时，出现了越来越多的以容器为中心的资源告警。最主要有两种风格：
- 1）资源利用率
- 2）容器数量

![最流行的告警条件：服务响应时间和正常运行时间](http://riboseyim-qiniu.riboseyim.com/Docker_2018_11.png)

**默认情况下容器没有资源限制** 。鉴于客户越来越注意容器限制方面的告警，这意味着他们正在使用 Docker运行时配置来控制容器使用内存，CPU或磁盘I / O 的上限，用户希望知道何时会超出阈值，应用程序的性能风险需要处于可控状态。

对于容器数量来说，这个问题通常与用户至少需要 X 个给定类型的容器并运行以提供所需的服务级别有关，特别是在微服务部署中。例如，“我知道如果需要确保应用程序运行良好，至少有三个 NGINX 容器可用。如果任何一个有问题，我都想知道。”

**基于编排的告警（Orchestration-focused alerts）** 也越来越受欢迎。与我们 2017 年的报告类似，“Pod Restart Count” 位列榜首。在一个 Pod 中，一个或多个容器是定位相同、共同调度（通常作为微服务的一部分）。如果某个容器重新启动太频繁，则表示存在可能影响应用程序性能的问题。

Kubernetes 管理员也经常使用 **基于事件的告警（ Event-based alerts ）** 。与基于度量的告警相比，它的区别在于，监控程序需要查找环境中生成的事件消息，例如 Kurthnetes “CrashLoopBackoff” 条件 — 代表 Pod 反复失败或重启，或者“Liveness probe failed”，表示容器是否为活跃和运行。这些告警有助于 DevOps 工程师快速定位问题。

Http 错误可能表明软件或基础架构存在问题，最终会影响性能。

![Kubernetes pod and namespace rises to top of alert scoping in 2018.](http://riboseyim-qiniu.riboseyim.com/Docker_2018_12.png)

> Alerts are not a one-size-fits-all approach.

告警不是一种万能的方法。有时需要设置基于指定范围的告警，无论是逻辑或物理实体，还是整个基础结构（注：Sysdig 通过标签实现）。

在 2018 年的研究中，用于确定告警范围的最常用标签与 Kubernetes 有关（Scoping by pods），命名空间（namespace）紧随其后。特定的容器范围（Container specific scoping）也很受欢迎，包括容器名称，容器镜像和容器 ID 。2018年再次名列榜首的是云服务提供商标签，通常针对“名称”，“环境”，“ID”和“区域”标签以区分开发、测试和生产资源，以及标记云数据中心的位置。

#### 容器和基础设施自定义监控指标

>There’s no one custom metrics format to rule them all.

"在环境中运行容器的客户，使用自定义指标的比例是多少，都是哪些？"

55％ 的 Sysdig SaaS 用户使用与 Java 应用程序相关的 JMX 指标。这与我们看到的 Java 应用程序部署非常广泛的事实一致。 StatsD 占有 29％ 的份额，Prometheus 占有 20％ 的份额（预计这个数字会随着时间的推移而增长）。

![JMX is the most used custom metric format.](http://riboseyim-qiniu.riboseyim.com/Docker_2018_13.png)

#### 容器注册

>It’s a split decision - registries are critical but there’s no clear leader.

注册管理机构至关重要，但是目前没有明确的领导者。
容器注册表（container registry）是任何容器部署的基本组件。市场上有许多解决方案：一些是公共的，一些是私有的，一些是作为服务提供，一些是作为本地软件（private registry）部署。

2018 年前三名中，Google Container Registry（GCR）的比例最高，其次是 Quay ,之后是 Docker和 Amazon Elastic Container Registry（ECR）。 GCR 和 ACR 都是完全基于云托管的（private Docker container registries）。Quay 和 Docker 既可以用作本地解决方案也可以在云中运行（注：Sysdig 的用户群只有 50％ 能够清楚地识别出容器注册方案）

![Container registry use is divided across public and private solutions.](http://riboseyim-qiniu.riboseyim.com/Docker_2018_14.png)

>New approaches are maturing and helping organizations develop applications more quickly to solve real business challenges and compete in the digital marketplace.

## 参考文献
- [AgentNEO 架构简介 | 07 May 2018](https://agentneoteam.github.io/2018/05/07/agentneo-jia-gou-jian-jie.html)
- [用 Sysdig 监控服务器和 Docker 容器 | 曹元其 | 2016 年 7 月 15 日发布](https://www.ibm.com/developerworks/cn/linux/1607_caoyq_sysdig/index.html)
- [使用 sysdig 进行监控和调试 linux 机器](http://cizixs.com/2017/04/27/sysdig-for-linux-system-monitor-and-analysis)
