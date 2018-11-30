# Cyber-Security: Linux 容器安全的十重境界

## 摘要
容器安全解决方案需要考虑不同技术栈和容器生命周期的不同阶段。
- 1.容器操作系统与多租户
- 2.容器内容（使用可信源）
- 3.容器注册 (容器镜像加密访问)
- 4.构建过程安全
- 5.控制集群中可部署的内容
- 6.容器编排：加强容器平台安全
- 7.网络隔离
- 8.存储
- 9.API 管理, 终端安全和单点登录 (SSO)
- 10.角色和访问控制管理

- [10 layers of Linux container security | Daniel Oh | Senior Specialist Solution Architect at Red Hat](https://opensource.com/article/17/10/10-layers-container-security)

容器提供了一种简单的应用程序打包方法将它们无缝地从开发、测试环境部署到生产环境。它有助于确保各种环境中的一致性，包括物理服务器、虚拟机（VM）或私有或公共云。领先的组织基于这些好处迅速采用容器，以便轻松地开发和管理增加业务价值的应用程序。

![](http://riboseyim-qiniu.riboseyim.com/Linux-Containers-Security-5.png)

企业应用需要强壮的安全性，任何在容器中运行基础服务的人都会问：“容器是安全的吗？”、“可以让我们的应用程序信任容器吗？”

保护容器非常类似于确保任何正在运行的进程。在部署和运行容器之前，您需要考虑整个解决方案技术栈的安全性。您还需要考虑在应用程序和容器的完整生命周期的安全性。

请尝试在这 10 个方面加强容器的不同层次、不同技术栈和不同生命周期阶段的安全性。

#### 1. 容器操作系统与多租户
对于开发人员来说，容器使得他们更容易地构建和升级应用程序，它可以作为一个应用单元的被依赖，通过在共享主机上部署启用多租户应用程序来最大限度地利用服务器资源。容器很容易在单个主机上部署多应用程序，并根据需要开启和关闭单个容器。为了充分利用这种打包和部署技术，运维团队需要正确的运行容器环境。运维人员需要一个操作系统，该系统可以在边界处保护容器，使主机内核与容器隔离并确保容器彼此之间安全。

容器是隔离和约束资源的 Linux 进程，使您能够在共享宿主内核中运行沙盒应用程序。您保护容器的方法应该与确保 Linux 上任何正在运行的进程的安全方法相同。放弃特权是重要的，目前仍然是最佳实践。更好的方法是创建尽可能少的特权容器。容器应该作为普通用户运行，而不是 root 用户。接下来，利用 Linux 中可用的多种级别的安全特性确保容器的安全： Linux 命名空间，安全增强的Linux（SELinux）， cgroups，capabilities 和安全计算模式（seccomp）。

![](http://riboseyim-qiniu.riboseyim.com/Linux-Containers-Security-1.jpg)

#### 2. 容器内容（使用可信源）
当说到安全性的时候，对于容器内容来说意味着什么呢？。一段时间以来，应用程序和基础设施都是由现成的组件组成的。很多都来自于开源软件，例如如 Linux 操作系统，Apache Web 服务器，红帽 JBoss 企业应用平台，PostgreSQL 和 Node.js。基于容器的各种软件包版本现在一应俱全，所以你不需要建立自己。但是，与从外部源下载的任何代码一样，您需要知道包的起源、它们是由谁创建，以及它们内部是否存在恶意代码。

#### 3. 容器注册 (容器镜像加密访问)
你的团队构建容器的时候基于下载的公共容器镜像，
所以对它的访问管理和更新下载是管理的关键，需以同样的方式管理容器镜像、内建的镜像及其他类型的二进制文件。许多私有仓库注册服务器支持存储容器镜像。选择一个私有的、存储使用的容器镜像自动化策略的注册服务器。

#### 4. 构建过程安全
在一个容器化的环境里，软件的构建是整个生命周期的一个阶段，应用程序代码需要与运行库集成。管理此构建过程是确保软件栈安全的关键。坚持“一次构建，到处部署（build once, deploy everywhere）”的理念，确保构建过程的产品正是生产中部署的产品。这一点对于维护容器持续稳定也非常重要，换句话说，不要为运行的容器打补丁；而是应该重新构建、重新部署它们。
无论您是在高度规范的行业中工作，还是仅仅想优化团队的工作，需要设计容器镜像的管理和构建过程，以利用容器层实现控制分离，从而使：
- 运维团队管理基础镜像
- 架构团队管理中间件、运行时、数据库和其它解决方案
- 开发团队仅仅专注于应用层和代码

最后，对定制的容器签名，这样可以确保它们在构建和部署环节之间不会被篡改。

![](http://riboseyim-qiniu.riboseyim.com/Linux-Containers-Security-4.png)

#### 5. 控制集群中可部署的内容
为了防备在构建过程中发生任何问题，或者在部署一个镜像后发现漏洞，需要增加以自动化的、基于策略的部署的另一层安全性。

让我们看一下构建应用程序的三个容器镜像层：核心层（core）、中间件层（middleware）和应用层（application）。一个问题如果在核心镜像被发现，镜像会重新构建。一旦构建完成，镜像将被推入容器平台注册服务器。平台可以检测到镜像发生了变化。对于依赖于此镜像并有定义触发器的构建，该平台将自动重建应用程序并整合已经修复的库。

一旦构建完成，镜像将被推入容器平台的内部注册服务器。内部注册服务器中镜像的变化能立即检测到，通过应用程序中定义的触发器自动部署更新镜像，确保生产中运行的代码总是与最近更新的镜像相同。所有这些功能协同工作，将安全功能集成到您的持续集成和持续部署（CI / CD）过程中。

#### 6. 容器编排：加强容器平台安全
当然，应用程序很少在单个容器中交付。即使是简单的应用程序通常有一个前端，后端和数据库。在容器中部署现代微服务应用，通常意味着多容器部署，有时在同一主机上有时分布在多个主机或节点，如图所示。

当规模化管理容器部署时，您需要考虑：
- 哪些容器应该部署到哪个主机上？
- 哪个主机容量更大？
- 哪些容器需要相互访问？他们将如何相互发现？
- 如何控制对共享资源的访问和管理，比如网络和存储？
- 如何监控容器健康状态？
- 如何自动扩展应用能力以满足需求？
- 如何使开发者在自助服务的同时满足安全需求？

考虑到开发人员和运维人员拥有的广泛能力，强大的基于角色的访问控制是容器平台的关键元素。例如，编排管理服务器是访问的中心点，应该得到最高级别的安全检查。API 是大规模自动化容器管理的关键，用于验证和配置容器、服务和复制控制器的数据；对传入的请求执行项目验证；并调用其他主要系统组件上的触发器。

![](http://riboseyim-qiniu.riboseyim.com/Linux-Containers-Security-3.png)

#### 7. 网络隔离
在容器部署现代微服务应用程序往往意味着在多个节点分布式部署多个容器。考虑到网络防御，您需要一种在集群中隔离应用程序的方法。

一个典型的公共云服务，例如Google Container Engine (GKE),Azure Container Services, 或者 Amazon Web Services (AWS) Container Service，都是单租户服务。它们允许在您启动的 VM 集群上运行容器。为了实现多租户容器安全，您需要一个容器平台，允许您选择单个集群并将流量分段，以隔离该集群中的不同用户、团队、应用程序和环境。

通过网络命名空间，每个容器集合（称为“POD”）获得自己的IP和端口绑定范围，从而在节点上隔离 POD 网络。

默认情况下，来自不同命名空间（项目）的 POD  不能将包发送到或接收来自不同项目的 POD 、服务的数据包，除了下文所述的选项。您可以使用这些特性来隔离集群中的开发人员、测试和生产环境；然而，IP 地址和端口的这种扩展使得网络变得更加复杂。可以投资一些工具处理这种复杂性。首选的工具是采用[软件定义网络（SDN）](https://riboseyim.github.io/2017/05/12/SDN/)容器平台，它提供统一的集群网络，保证整个集群的容器之间的通信。

#### 8. 存储
对于有状态和无状态的应用程序来说，容器是非常有用的。
保护存储是保证有状态服务的关键要素。容器平台应提供多样化的存储插件，包括网络文件系统（NFS），AWS Elastic Block Stores（EBS，弹性块存储），GCE Persistent 磁盘，GlusterFS，iSCSI，RADOS（CEPH）、Cinder 等等。

一个持久卷（PV）可以安装在由资源提供者支持的任何主机。供应商将有不同的能力，每个 PV 的访问模式可以设置为特定卷支持的特定模式。例如，NFS 可以支持多个 读/写的客户端，但一个特定的 NFS  PV 可以在服务器上仅作为只读输出。每个 PV 有它自己的一套访问模式，定义特定 PV 的性能指标，例如ReadWriteOnce, ReadOnlyMany, 和 ReadWriteMany。

#### 9. API 管理, 终端安全和单点登录 (SSO)
保护应用程序安全包括管理应用程序和 API 身份验证和授权。
Web SSO 功能是现代应用程序的关键部分。当开发者构建他们自己的应用时，容器平台可以提供各种容器服务给他们使用。

API 是[微服务](http://riboseyim.github.io/2017/06/12/OpenSource-Kafka-Microservice/)应用的关键组成部分。[微服务](http://riboseyim.github.io/2017/06/12/OpenSource-Kafka-Microservice/)应用具有多个独立的 API 服务，这导致服务端点的扩张，因此需要更多的治理工具。推荐使用 API 管理工具。所有 API 平台都应该提供各种 API 认证和安全的标准选项，它们可以单独使用或组合使用，发布证书和控制访问。这些选项包括标准的 API 密钥、应用ID、密钥对和 [OAuth 2.0](http://riboseyim.github.io/2017/05/23/RestfulAPI/)。

#### 10. 角色和访问控制管理（Cluster Federation）
2016年7月，Kubernetes 1.3  介绍了 Kubernetes Federated Cluster。这是一个令人兴奋的新功能，目前在 Kubernetes 1.6 beta 。

在公共云或企业数据中心场景中，Federation 对于跨集群部署和访问应用服务是很有用的。多集群使得应用程序的高可用性成为可能，例如多个区域、多个云提供商（如AWS、Google Cloud 和 Azure）实现部署或迁移的通用管理。

在管理集群联邦时，必须确保编排工具在不同的部署平台实例中提供所需的安全性。与以往一样，身份验证和授权是安全的关键 —— 能够安全地将数据传递给应用程序，无论它们在何处运行，在集群中管理应用程序多租户。

Kubernetes 扩展了集群联邦包括支持联邦加密，联邦命名空间和对象入口。

## 参考文献
- [10 layers of Linux container security | Daniel Oh | Senior Specialist Solution Architect at Red Hat](https://opensource.com/article/17/10/10-layers-container-security)

## 扩展阅读: 网络安全专题合辑《Cyber-Security Manual》
- [Cyber-Security: Linux 容器安全的十重境界](https://riboseyim.github.io/2017/11/12/DevOps-Container-Security/)
- [Cyber-Security: 警惕 Wi-Fi 漏洞，争取安全上网](https://riboseyim.github.io/2017/10/29/CyberSecurity-WiFi/)
- [Cyber-Security: Web应用安全：攻击、防护和检测](https://riboseyim.github.io/2017/08/31/CyberSecurity-Headers/)
- [Cyber-Security: IPv6 & Security](http://riboseyim.github.io/2017/08/09/Protocol-IPv6/)
- [Cyber-Security: OpenSSH 并不安全](http://riboseyim.github.io/2016/10/06/CyberSecurity-SSH/)
- [Cyber-Security: Linux/XOR.DDoS 木马样本分析](http://riboseyim.github.io/2016/06/12/CyberSecurity-Trojan/)
- [浅谈基于数据分析的网络态势感知](http://riboseyim.github.io/2017/07/14/Network-sFlow/)
- [Packet Capturing:关于网络数据包的捕获、过滤和分析](http://riboseyim.github.io/2017/06/16/Network-Pcap/)
- [新一代Ntopng网络流量监控—可视化和架构分析](http://riboseyim.github.io/2016/04/26/Network-Ntopng/)
- [Cyber-Security: 事与愿违的后门程序 | Economist](http://www.jianshu.com/p/670c4d2bb419)
- [Cyber-Security: 美国网络安全立法策略](https://riboseyim.github.io/2016/10/07/CyberSecurity/)
- [Cyber-Security: 香港警务处拟增设网络安全与科技罪案总警司](http://riboseyim.github.io/2017/04/09/CyberSecurity-CSTCB/)
