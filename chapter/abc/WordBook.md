# Perf Master WordBook


**dynamic tracing，动态追踪技术**
>kk


### 指标

**IOPS**
>每秒的I/O操作数

**吞吐量**
>评价执行速率。
数据传输：字节／秒或比特／秒
数据库：秒操作数，秒业务数

**延迟**
>描述操作里等待服务的时间。某些情况下等同于响应时间。

#### **使用率**
>给定时间区间内，资源的繁忙程度。如内存使用率。
非空闲时间（Brendan定义）

1.基于时间：
  U=B/T
U是使用率，B是T时间内系统繁忙时间，T是观测周期。

2.基于容量：

**observer effect ,观测者效应**：指标的获取不是免费的。

-------------------------


**响应时间**
>一次操作完成的时间。包括用于等待和服务的时间。



**饱和度**
>指的是某一资源无法满足服务的排队工作量。
随着工作量增加而对资源的请求超过资源所能处理的程度。


**瓶颈**
>限制性资源。

**工作负载**
>系统的输入、输出对系统所施加的负载叫工作负载。
如数据库的工作负载就是客户端发出的请求和指令。

**缓存**
>用于复制或存储一定量数据的高速存储区域。
ps:相对概念。只要存在高低速差，即可视为广义缓存？

1.冷：填充的数据为空，或填充无用的数据，命中率为0
2.热：填充的都是常用数据
3.温：命中率还没有达到理想的高度
4.热度：

## P

**Profiling 剖析**

**Perturbation 扰动**
>同义词：

**ROI 投资回报率**
>性能技术取决于投资回报率！
横跨大西洋连接纽约交易所和伦敦交易所的光缆，预算3亿美金，用以减少6ms的传输时延


**SUT,system under test 受测系统**


## 方法论
分类：信息收集、观测分析、容量规划、统计分析、调优、实验分析

### USE方法 （utilization,saturation,errors）

for(资源;所有资源;i++){
  1.出现错误？
  2.使用率高？
  3.饱和？
}

# Scheduling Algorithm 调度算法

|**Code**|**Name**|**名称**|**说明**|
|----|----------|---------|-------------------|
|RR|**round－robin**|轮询调度|单纯依次请求真实服务器。均等分发|
|WRR|**weighted round-robin**|加权轮询调度|引入加权值来控制分发比率。|
|LC|**least-connection**|最小连接调度|将新的请求连接到当前连接数最少的服务器|
|WLC|**weighted least-connection**|加权最小连接调度|（连接数＋1）／加权值。|
|SED|**shortest expected delay**|最短预期延时调度|选择响应速度最快的那台服务器，选择状态ESTABLISHED的连接数最少的服务器。与WLC类似，但WLC会把TIME_WAIT或FIN_WAIT的连接数计算在最小因素中|
|NQ|**never queue**|不排队调度|最优先选择连接数为0的服务器|
|SH|**source hashing**|源地址散列调度|对发出请求的IP地址计算散列值|
|DH|**destination hashing**|目标地址散列调度|对需要接收请求的目标地址计算散列值|
|LBLC|**locality-based least-connection**|基于局部性的最小连接|在连接数没有超过加权值指定的值时，将选择同一台服务器|
|LBLCR|**locality-based least-connection with replication**|带复制的基于局部性最小连接|当所有服务器的连结数都超过加权值指定的值时，将选择连接数最少的那台服务器|

# DevOps-WordBook


## A

**Availability** () [可用性]
```
系统停止的可能性。
```

**Algorithm**()[算法]

[Scheduling Algorithm 调度算法](SchedulingAlgorithm.md)

## B

**Bottleneck** () [瓶颈]
```
阻碍系统整体性能提升的地方。
```


## C

**CDN** (Content Delivery Network) [内容分发网络]
```
从散布在全世界的缓存服务器中，选择离客户端较近的服务器来发送信息，据此实现性能的提升
```


## D

**DC** (Data Center) [数据中心]
```
容纳服务器设备。配备空调，冗余电源。配备火灾、地震等应急措施。
```

**Daemon** () [守护程序]
```
在后台持续运行并发挥作用。
```

## E

**Environment** () [环境]
```
Production Environment 生产环境
Staging Environment  准生产环境
```

## F

**Failover** () [故障转移]
```
在冗余系统中，活动节点（Active Node）停止时，自动切换到备用节点（Backup Node）.
如果不是自动切换，而是手动，通常叫作Switchover.
```
**Failback** ()[故障恢复]


**File System** () [文件系统]
```
分类：
Memory File System:建立在内存中的文件系统

```

## G

**** () []
```

```

## H

**Health Check** () [健康检查]
```
确认检查对象的状态是否正常。
例如：确认服务器是否能响应ping,是否能应答HTTP等。
```

## I

**** () []
```

```

## J

**** () []
```

```

## K

**Keepalived** () [内容分发网络]
```
http://www.keepalived.org
```
## L

**LVS** (Linux Virtual Server) [Linux虚拟服务器]
```
原伟项目名，旨在搭建具有可扩展性的、实用性高的系统。
http://www.linuxvirtualserver.org
```

**Load Balancer** () [负载均衡器]
```
位于客户端和服务器之间，将客户端的请求分散到后端的多台服务器。
Load［负载］大致力可分为“CPU负载”和“IO负载”。详见：http://www.jianshu.com/p/db8e8a2884ef
```

**Latency** () [延迟]
```
通常指数据投递完成所花费的时间。
```

## M

**** () []
```

```

## N

**NIC** (Network Interface Card) [网络接口卡，网卡]
```
网络接口的总称
```

**Netfilter**
```
Linux内核中操作网络数据包所需的协议框架。
实现分组过滤的iptables和负载均衡的IPVS也应用了Netfilter协议。
```

## O

**OSI** (Open Systems Interconnection) [参考模型]
```
分为七层（Layer）框架，例如：
第七层：应用层。HTTP及SMT等协议
第四层：传输层。TCP及UDP
第三层：网络层。IP、ARP及ICMP
第二层：数据链路层。以太网等
```

**OSS** (OpenSource Software) [开源软件]
```
```

## P

**Proxy** () [代理]
```
Transparent Proxy:透明代理

```
## Q

**** () []
```

```

## R

**Redundancy** () [冗余]
```
将系统的构成要素配置多个，即使其中一个发生故障而停止运作，也可以切换到备用设备以使服务不停止。
```
**Resource** () [资源]
```
指CPU,内存，磁盘空间等
```

## S

**Scalability** () [可扩展性]
```
随着用户的增多以及规模的扩大，在某种程度上扩展系统以加强应对的能力。
Scale-out:横向扩展。例如：将服务分摊到多台服务器
Scale-up:纵向扩展。 例如：提升单个服务器性能
```

**SPO** (Single Point of Failure) [单点故障]
```
若此处出现问题，就会令整个系统停止，即系统要害。
```


## T

**Throughput** () [吞吐量]
```
代表单位时间的传送量。
例如：大巴车和小汽车。
```

## U

**** () []
```

```

## V

**VIP** (Virtual IP Address) [虚拟IP地址]
```
浮动分配给某项服务或功能的IP地址。例如负载均衡器中接收客户端请求的IP地址。
```
## W

**** () []
```

```

## X

**** () []
```

```

## Y

**** () []
```

```

## Z

**** () []
```

```
