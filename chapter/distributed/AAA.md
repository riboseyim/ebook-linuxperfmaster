# 基于LVS的AAA负载均衡架构实践

## 概要
本次分享将从一次实际的负载均衡改造案例出发，通过介绍项目背景、选型思路、测试方法和问题分析等方面展开，总结负载均衡架构的一般套路和经验教训。

## 一、背景
项目背景是某企业的AAA管理系统， AAA 即 Authentication（认证）、Authorization（授权）、Accounting（记账），是网络设备的一种集中化管理机制，可以在不同设备上为用户设置不同的权限，对网络安全起到监视作用。

 AAA服务是基于TACACS+协议（Terminal Access Controller Access Control System Plus），TACACS＋是在 TACACS 协议的基础上进行了功能增强的安全协议，最早由Cicso提出并开放标准。该协议与 RADIUS 协议的功能类似，采用客户端/服务器模式实现 网元与 TACACS+ 服务器之间的通信，使用TCP 49端口。

每次TACACS+ 交互主要实现：
认证 (Authentication): 确认访问网络的用户身份，判断访问者是否合法
授权( Authorization ): 对通过认证的用户，授权其可以使用哪些服务
记账( Accounting )：记录用户的操作行为、发生时间

### 1.问题描述

系统架构如下图所示，服务器采用一主一备模式，一般情况下由Master服务器处理请求，如果它故障或者负荷过高、无法快速响应请求，网元会将请求发送到BackUP服务器处理。AAA Server上运行守护进程处理请求，记为TACACSD。

![](http://o8m8ngokc.bkt.clouddn.com/LB-AAA-Now.png)

**容量计算**
	>服务端资源需求T＝ 认证请求规模g(n) ／ TACACSD运算能力 f(n)  

在很长一段时间内，原有架构可以满足应用需求，但是随着集中化的深入推进，资源不足的问题日益严重：Master负荷早已爆满，BackUP的负荷也几乎与Master相当，而且请求从Master切换到BackUP的时候，非常容易引起失败。
主要有三个关键因子的变化：
1、管理设备数量增长10倍，而且还要继续增长
2、网络配置自动化，单一网元的巡检、配置操作有数量级的提升  
3、TACACSD程序本身存在性能瓶颈，CPU消耗随着设备数量增长而增长

前两个因素属于业务需求，不能调整，程序性能问题涉及开发周期问题（这块以后再单独分析），迫于业务压力，我们必须快速寻找一种变通方案。

### 2.选型要求

在选择适用方案之前，我们必须考虑以下几个要求：

**可伸缩性（Scalability）**
当服务规模（设备数量、自动化操作次数）的负载增长时，系统能被扩展来满足需求（弹性扩展服务能力），且不降低服务质量。

**高可用性（Availability）**
尽管部分硬件和软件会发生故障，整个系统的服务必须是每天24小时每星期7天可用的。（必须去除原来过于依赖单一服务器的瓶颈）

**可管理性（Manageability）**
整个实现应该易于管理，提供灵活的负载均衡策略支持。

**价格有效性（Cost-effectiveness）**
整个实现是经济的。这个怎么说呢，比如这个问题吧，有人说：买四层交换机啊？ 没钱！宇宙上最好服务器来一台？ 没钱！！
于是我们的主要探索方向放在了开源软件，感谢开源社区解救穷人。


## 二、前戏

我们首先想到的是HAProxy，一款经典的负载均衡开源软件。
特别是具备以下几个特点：配置维护简单，支持热备，支持后端服务器的状态检测，可以自动摘除故障服务器；支持TCP 代理；支持Session的保持。
>tcp   
>The instance will work in pure TCP mode.
>A full-duplex  connection will be established between clients and servers,
> and no layer 7 examination will be performed.
>  This is the default mode. It should be used for SSL, SSH, SMTP, ...

```
vi haproxy.cfg
listen AAA-Cluster
         mode tcp      
         bind 49
         option tcplog
         source 0.0.0.0 usesrc clientip
         server AAA-Server-210 192.168.3.10:49
         server AAA-Server-211 192.168.3.11:49
```

### 1.HAProxy+TProxy
当我们满怀希望地推进之时，一个要命的问题摆在面前：后端的AAA服务器上看到的连接的Source IP都不再是用户原始的IP，而是前端的HAProxy服务器的IP，

![](http://o8m8ngokc.bkt.clouddn.com/LB-AAA-HAProxy.png)

官方文档对于source调度算法的描述：

>source      
>The source IP address is hashed and divided by the total weight of the >
> running servers to designate which server will receive the request.
> This ensures that the same client IP address will always reach the same
> server as long as no server goes down or up.
> If the hash result changes due to the number of running servers
> changing, many clients will be directed to a different server.

TACACSD进程必须获取到认证请求的Source IP，为此我们尝试引入TProxy。
它允许你”模仿"用户的访问IP，就像负载均衡设备不存在一样，TProxy名字中的T表示的就是transparent(透明)。当网元发起的认证请求到达后端的AAA服务器时，可以通过抓包看到的请求Source IP就是网元的真实IP。

即使用上“HAProxy+TProxy”的组合拳，还是存在另外一个问题：
**设备对于认证结果报文，似乎需要请求报文的目标地址（代理服务器）与结果报文的发送端（Real  AAA Server）一致。**
![](http://o8m8ngokc.bkt.clouddn.com/LB-AAA-Flow.png)

过程描述：网络设备会发送该用户的凭证到 TACACS+ 服务器进行验证，然后决定分配访问相关设备的权限，并将这些决定的结果包含在应答数据包中并发送到网络设备上，再由网络设备发送到用户终端。
**至于是否真的是这个校验规则，或者我们还没有找到更好的解释。暂且搁置，引述一段RFC 1492的说明，日后再补充这个问题。**
CONNECT(username, password, line, destinationIP, destinationPort)
returns (result1, result2, result3)

This request can only be issued when the username and line specify
an already-existing connection.  As such, no authentication is
required and the password will in general be the empty string. It
asks, in the context of that connection, whether a TCP connection
can be opened to the specified destination IP address and port.

### 2.IPTABLES NAT
为了解决上述Proxy无法传递Source IP 的问题，我们还尝试过基于 iptables 实现网络地址转换的方式，It’s Working !!
~~~
iptables -t nat -A PREROUTING -i eth0 -p tcp --dport 49 -j DNAT --to 192.168.3.10-192.168.3.13
~~~
如上即可解决HAProxy的Source IP 传递和报文回路的问题。
压力测试的时候，开始设备数比较少的时候，各项业务还很正常，当设备数加到1.5万台左右，或者几百台设备并发请求的时候，报文转发的时延久急剧上升，甚至出现丢包情况。这个方案对我们来说显然存在性能瓶颈。

>HAProxy—>HAProxy + TProxy —>IPTABLES NAT

转了一圈，回到起点。

## 三、终极杀器

经过之前一波三折的折腾，我们决定启用一款终极杀器：LVS。
LVS即Linux Virtual Server，是一个虚拟的服务器集群系统。它有三种工作模式NAT(地址转换),IP Tunneling(IP隧道)、Direct Routing(直接路由)。

|  ** **  | **NAT模式** | ** TUN模式** | ** DR模式** |
|----|----------|---------|------------|
|Server | any | Tunneling | Non-arp device |
|Server Network | private | LAN/WAN |LAN |
|Server Number | low(10-20) | HIGH(100) | HIGH(100) |
|Server Gateway | load balancer | own router | own router |

基于之前NAT方面的不良体验，我们这次直接选择了LVS-DR模式，
LVS支持八种调度算法，我们选择轮询调度（Round-Robin Scheduling）。

LVS只处理一般连接,将请求给后端real server,然后由real server处理请求直接相应给用户,Direct Routing与IP Tunneling相比，没有IP封装的开销。

缺点：由于采用物理层,所以DR模式的调度器和后端real server必须在一个物理网段里,中间不能过路由器。

![](http://o8m8ngokc.bkt.clouddn.com/LB-AAA-LVS-Single.png)

另外，为了防止LVS控制机的单点故障问题，还选用了Keepalived，负责LVS控制机和备用机的自动故障切换。

![](http://o8m8ngokc.bkt.clouddn.com/LB-AAA-LVS-Cluster.png)


LVS依赖项：IPVS内核模块和ipvsadm工具包。
具体配置不做过多说明，可以自行检索，关键注意以下几点：
1）检查服务器是否已支持ipvs
	modprobe -l |grep itvs
2）检查依赖包：
rpm -q kernel-devel
rpm -q gcc
rpm -q openssl
rpm -q openssl-devel
rpm -q popt
3）配置realserver节点ARP及VIP绑定脚本
 vi /etc/init.d/lvs
4）启动LVS-DR
/etc/init.d/lvsdr start
5）查看VIP 情况
ip addr list
6）启动realserver节点LVS
 /etc/init.d/lvs start

## 五、小结

### 1. 各种负载均衡实现在网络中的位置
![](http://o8m8ngokc.bkt.clouddn.com/LB-Normal-Model.png)

四层负载均衡的特点一般是在网络和网络传输层(TCP/IP)做负载均衡，而七层则是指在应用层做负载均衡。
四层负载均衡对于应用侵入比较小，对应用的感知较也少，同时应用接入基本不需要对此做特殊改造。
七层负载均衡一般对应用本身的感知比较多，可以结合一些通用的业务负载逻辑做成很细致的方案，比如我们通常用HAProxy/Nginx来做网站流量的分发。

>实践再次教育我们，天下没有一招鲜，任何技术都有它的江湖位置。

#### 2. 仿真能力
这次实践可以用一句话概括就是：“成也仿真，败也仿真”。
起初走了很长一段弯路，可以说是因为对整个负载均衡体系的理解不深入，也可以说是测试不足导致，凭着惯性，想当然地认为可以简单复制原来的“经验”，而
忽视了实验环境的构建。

后来可以快速推进，是因为重新规整了测试方法和目标，并且基于虚拟机搭建了验证环境，包括引入了可以仿真路由器的GNS3平台，完整地测试了真实的业务流程。LVS集群环境也是先完成构建、试运行一段时间之后才完成的业务割接。

IPTABLES NAT的方案并没有在早期发现性能瓶颈，也说明这快的测试能力不足。

### 3.花边故事
HAProxy的官网目前是被封锁的，国内不翻墙访问不了，Why ?
在他们家的操作手册后面有LVS、Nginx的推荐链接。以前并没有注意。

TPROXY最早是作为Linux内核的一个patch，从linux-2.6.28以后TPRXOY已经进入官方内核。iptables只是Linux防火墙的管理工具而已，位于/sbin/iptables。真正实现防火墙功能的是Netfilter，它是Linux内核中实现包过滤，如果要探讨Netfilter，又会是一个很长的故事。

LVS开始于1998年，创始人是章文嵩博士，从Linux2.4内核以后，已经完全内置了LVS的各个功能模块。到今天为止，依然是目前国内IT业界达到Linux标准内核模块层面的唯一硕果。章博士同时是前淘宝基础软件研发负责人、前阿里云CTO，三个月前刚转会到滴滴打车任副总裁。淘宝技术体系曾大规模使用了LVS，不过最新消息，淘宝的同学已经鼓捣出一个VIPServer，正逐步替代了LVS。

罗列的这几条信息，其实与这次的主题关系不大，但确是整理这次篇帖子过程中，感觉很有意思的事情。技术并不冰冷，它就像个江湖，到底还是关于人的故事。

### 续集

> 可能更新，也可能不更新，看天意。

1、本次场景中， HAProxy方案为什么会失败？还缺少一个深度解释。
2、本次场景中，LVS方案采用默认的轮询算法是否最优？
3、本次场景中，7X24系统如何完成服务切换？
4、本次场景中， IPTables NAT 的性能瓶颈如何解释？
5、来一个关于Netfilter的讨论
6、阅读参考资料 VIPServer: A System for Dynamic Address Mapping and Environment Management

<hr>
更多精彩内容，请扫码关注公众号：@睿哥杂货铺  
[RSS订阅 RiboseYim](https://riboseyim.github.io?product=ebook&id=linuxperfmaster)
