# 浅谈基于数据分析的网络态势感知

## 摘要
态势感知（Situational Awareness，SA）的概念最早在军事领域被提出。20世纪80年代，美国空军就提出了态势感知的概念，覆盖感知（感觉）、理解和预测三个层次。90年代,态势感知的概念开始被逐渐被接受，并随着网络的兴起而升级为“网络态势感知（Cyberspace Situation Awareness，CSA）”，指在大规模网络环境中对能够引起网络态势发生变化的安全要素进行获取、理解、显示以及最近发展趋势的顺延性预测，而最终的目的是要进行决策与行动。本文将围绕以下话题讨论网络态势感知中的几个常见问题：
- 网络感知的基础:网络分层、传感器
- 网络分析技术：SNMP、NetFlow、sFlow、NetStream、Packet Capturing
- 网络数据可视化: WireShark、NTopng、Ganglia、GeoIP

## 一、网络感知的基础

#### 1、没有任何一个传感器是全能的
测量一个网络的一般步骤如下：首先获得网络拓扑图，网络的连接方法、潜在的观察点列表等；然后确定潜在观察点，确定该位置所能看到的流量；最后，确定最优的覆盖方案。在复杂网络中，没有任何一个传感器能够全面覆盖，需要多种传感器配合使用。按照采集的领域，传感器可以分为三类：
- 网络：入侵检测系统（IDS）、NetFlow采集器、TCP采集器（如tcpdump）
- 主机：驻留在主机上，监控主机上的活动（文件访问、登录注销）、网卡流量
- 服务：邮件消息、特定服务的HTTP请求

#### 2、网络分层对传感器的影响
总的来说，网络传感器的焦点是OSI模型中的第2层～第4层，而服务传感器的焦点是第5层及以上。分层对网络流量的影响中，还需要考虑最大传输单元（MTU）：数据帧尺寸的上限，影响到介质中可以传送的封包的最大尺寸，以太网的MTU为1500字节，即IP封包不会超过这个尺寸。OSI模型第5层会话层需要考虑的情况是会话加密，加密后的信息无法直接理解；在第6层和第7层中，必须知道协议细节，才能提取有意义的信息。   

![](http://riboseyim-qiniu.riboseyim.com/Network_Layer_201707.png)                                       

## 二、网络分析技术
网络流量反映了网络的运行状态，是判别网络运行是否正常的关键。如果网络所接收的流量超过其实际运载能力，就会引起网络性能下降。网络中流量的各种参数主要包括：接收和发送数据报、丢包率、数据报延迟。

#### 1、SNMP
SNMP（ Simple Network Management Protocol )包含一个应用层协议（application layer protocol）、数据库模型（database schema），和一组数据对象。SNMP的第一个RFC系列出现在1988年(RFC1065-1067)，第二版（RFC1441–1452）作了修订，由于第二版的新安全系统被认为过于复杂而不被广泛接受，因此出现了两个方案：SNMP v2c（基于社区，RFC1901–1908）、SNMP v2u(基于用户，RFC1909–1910)。SNMP第三版（RFC3411-3418）主要增加了安全性方面的强化：信息完整性，保证数据包在发送中没有被窜改；认证，检验信息来自正确的来源；数据包加密，避免被未授权的来源窥探。

基于SNMP协议定义的计数器：ifInOctets、ifOutOctets，两次采样的差值除以间隔时间即可获得平均流量。需要注意的是计数器的数据类型有两种：counter32和counter64。counter32计数的最大值是2的32次方减1，当超过4G的时候，计数器就会清零。如果是大流量、高精度采样（间隔时间低于1分钟），需要考虑使用counter64（ifHCInOctets、ifHCOutOctets）,否则可能出现数据偏差，例如：

```
snmpwalk -v 2c -c public -u username 192.168.1.10 ifHCInOctets
IF-MIB::ifHCInOctets.1 = Counter64: 5020760
IF-MIB::ifHCInOctets.2 = Counter64: 12343743
IF-MIB::ifHCInOctets.3 = Counter64: 7123
IF-MIB::ifHCInOctets.21 = Counter64: 3854
```
![](http://riboseyim-qiniu.riboseyim.com/Flow_RRDTool_Demo_1.png)

#### 2、RMON
SNMP是基于TCP/IP、应用最广泛的网管协议，但是也有一些明显的不足，如：SNMP使用轮询方式采集数据信息，在大型网络中轮询会产生巨大的网络管理通讯报文；不支持管理进程的分布式管理，它将收集数据的负担加在网管站上，网络管理站会成为瓶颈；只能从这些管理信息库中获得单个设备的局部信息，标准管理信息库MIB-II(RFC1213)和各厂家的专有MIB库主要提供设备端口状态、流量、错误包数等数据，要想获得一个网段的性能信息是比较困难。

因此IETF提出了RMON（Remote Network Monitoring，RFC2021）以解决SNMP所面临的局限性。RMON 由 SNMP MIB 扩展而来，网络监视数据包含了一组统计数据和性能指标，它们在不同的监视器（或称探测器）和控制台系统之间相互交换。它可以主动地监测远程设备，对设备端口所连接的网段上的各种流量信息进行跟踪统计，如某段时间内某网段上报文总数等。只要给予探测器足够的资源，它还可以对数据设备进行防防性监视，设备主动地对网络性能进行诊断并记录网络性能状况，在发生故障时可以把信息及时通知管理者，相关信息分为统计量、历史、告警、事件等四个组，可以预置规则。

#### 3、NetFlow vs sFlow vs NetStream
NetFlow最早由 Cisco 研发的流量汇总标准，最初用于网络服务计费，本意不是为了流量分析和信息安全。它通过路由器提供收集IP网络流量的能力，分析的NetFlow数据（UDP packets）感知网络流量和拥塞情况。NetFlow的核心概念流（Flow），NetFlow直接从 IP Packet 中复制信息，包含来源及目的地、服务的种类等字段：
1. Source and destination IP address
2. Input and output interface number
3. Source and destination port number
4. Layer 4 Protocol
5. Number of packets in the flow
6. Total Bytes in the flow
7. Time stamp in the flow
8. Source and destination AS
9. TCP_Flag & TOS

![](http://riboseyim-qiniu.riboseyim.com/Flow_Vendor_Supported_201707.png)

**NetFlow vs IPFIX** NetFlow 的主力实现版本是 v5，但是 v5 主要针对 IPv4 存在很多限制，因此 Cisco 推出了基于模版的 NetFlow v9 。在NetFlow v9 的基础上，IETF在2008年发布了标准化的 IPFIX( Internet Protocol Flow Information eXport)（RFC5101/5102），IPFIX 提供了几百种流字段。另外，Juniper也有一套自己的标准 **J-Flow** 。

![](http://riboseyim-qiniu.riboseyim.com/Network_NetFlow_Arch.png)

**sFlow** (Sampled Flow, 采样流，RFC3176 )是另一种一种基于报文采样的网络流量监控技术，主要用于对网络流量进行统计分析。sFlow 2001年由lnMon公司提出来，目前是IEFE的一个开放标准，可提供完整的第二层到第四层、全网络范围内的流量信息。sFlow 关注的是接口的流量情况、转发情况以及设备整体运行状况，因此适合于网络异 常监控以及网络异常定位，通过 Collector 可以以报表的方式将情况反应出来，特别适合于企业网用户 。sFlow Agent内嵌于网络设备中，在 sFlow 系统中收集流量统计数据发送到 Collector 端供分析。

**NetStream** 是H3C定义的一套网络流量的数据统计方法。它需要由特定的设备支持，首先由设备自身对网络流进行初步的统计分析，并把统计信息储存在缓存区。值得注意的是，NetStream（IPv6）功能需要跟华为购买License，并且NetStream功能和sFlow功能不能同时在同一接口板上配置。如果接口板已经配置sFlow功能，则不能配置NetStream功能。

综上所述，各种 NetFlow 方案都是基于网络硬件设施生成或者软件封包为流，不同的厂商标准不同，尤其需要考虑兼容性。同时，各种机制都可能对硬件造成性能问题，特别是旧的型号存在更大的风险，一般不轻易开启。无论是硬件（中高端设备）还是软件（nProbe、nDPI）、NetStream（IPv6），都意味着昂贵的费用，需要充分考虑成本预算。

#### 4、NetFlow的其它替代方案

基于软件替代路由采集，基本都是采用封包的思路，将pcap文件当作数据源或者直接从网络接口上封包，通过解析Header聚合成流格式或者更丰富的输出。常见的产品如下：
- [CERT YAT(Yet Another Flowmeter)](http://tools.netsa.cert.org)
- [softflowd](http://bit.ly/softflowd)
- [QoSient Argus](http://bit.ly/qo-argus)

#### 5、协议和用户识别
   我们可以把数据包想像成一封信。根据解析数据报报头的内容，可以分析IP地址、端口号、协议、报文格式等特征，分类后可以实现对各种应用层协议的准确识别，如P2P（迅雷）、即时通信（QQ、微信）、VPN、邮件等。当然，这只能算是“浅度”的数据包检测，就好像是看看信封上的发件人和收件人 。

   “深度”的数据包检测，可以理解成对信件内容的探查──相比起暴力打开信封，这种基于机器学习的技术更具有艺术性。它并不实际解读数据包的内容，而是搜集周边信息，对数据流进行“肖像刻划”（Profiling）。国内某研究团队曾发表论文“网络流量分类，研究进展与展望”，文章提到了多种使用机器学习进行“深度数据包检测”（Deep Packet Inspection，DPI）的技术。对“墙”有兴趣的同学可以深入了解,http://riboseyim.github.io/2017/05/12/GFW/ 。

## 三、网络数据可视化

#### 1、面向流向分析的可视化
文中开头我们就提到测量网络的第一步就是获得网络拓扑图，如果要获得全局角度实时感知能力，需要在拓扑的基础之上叠加通过各种网络分析技术获得的流量/Flow/事件等信息，进而处理分析网络异常流量。能够实用的数据分析具有相当的复杂性，需要专门的工具软件，区分正常流量数据和异常流量数据、对于“异常模式”的算法训练都有一定门槛，因此存在大量的开源和商业解决方案。
![](http://riboseyim-qiniu.riboseyim.com/Flow_DynamicTopo_LiveAction_1.png)

#### 2、面向故障诊断的可视化

- 抓包工具：tcpdump、TShark、 WinDump
- 图形化工具：wireshark(客户端)、ntopng(webUI)
- 自定义编程：R、Python([Python-Scapy](http://bit.ly/scapy))、[Graphviz工具包](http://www.graphviz.org)

一个典型的故障场景：两个服务之间发生故障、无法收发信息，可以通过tcpdump的抓包，并将抓包结果在WireShark上分析，基于染色的方式通信失败的报文被高亮提示。TCP通信中客户端向服务端发送tcp zero window（表示没有window可以接收新数据），如果出现该特征一般可以确定故障是由接收端服务器TCP缓冲区占满的引起，应将排查方向锁定在接收端。关于网络数据包的捕获、过滤、分析的具体实现细节，可以参考：[Packet Capturing:关于网络数据包的捕获、过滤和分析](http://riboseyim.github.io/2017/06/16/Pcap/)

![wireshark应用案例：TCP Window Zero](http://riboseyim-qiniu.riboseyim.com/PacketCapturing_WinZero_2015.png)

在企业应用中，网络监测数据通常需要与基础监控平台融合才能发挥最大价值（开源的方案Zabbix/Ganglia/Nagios／Graphite等）。Collectd与Ganglia是竞争关系，都是C语言开发,数据输出都是RRDTool，性能应该差不多，Collectd不包含图形化组件。zabbix是覆盖面比较广的综合套件，除了采集还有告警等其它管理功能，专业性和大规模应用方面可能就不太强。Nagios在思路方面比较接近zabbix,走的是综合性路子，侧重于告警方案：“Ganglia is more concerned with gathering metrics and tracking them over time while Nagios has focused on being an alerting mechanism.” 在Ganglia项目中提供了一个 gmond_proxy 可以搭配 sFlow-RT 支持 NetFlow／sFlow 的数据收集，如果是自己实现 sFlow-RT 类似的组件也需要考虑对 Logstash/splunk的支持。

|开源项目|开发语言|定位|说明|
|-----|-----|-----|-----|
|Collectd|C|数据采集器|不包含图形化组件|
|Ganglia|C，PHP（front-end）| 数据采集器 |包含一个Web图形化组件|
|Zabbix|C，PHP（front-end）| Server-Client |不包含图形化扩展插件|
|Nagios|C ，PHP（front-end）| Core+Plugins |包含多种图形化扩展插件|
|[Grafana](http://docs.grafana.org/features/panels/graph/) | Go | 指标数据的可视化展现板 | 需要提前对数据进行时序化处理，例如 InfluxDB 等 |

![](http://riboseyim-qiniu.riboseyim.com/Network_sFlow_Arch.png)
![](http://1.bp.blogspot.com/--W4Io9SdkCA/Vme0AecgIKI/AAAAAAAACOM/WRkZQRZNUVo/s1600/gmond-proxy.png)

#### 3、面向安全分析的可视化

- 流向&协议：[Ntopng](http://riboseyim.github.io/2016/04/26/Ntopng/)
- 地理位置服务，根据IP地址确定改地址的物理位置信息（坐标）：[MaxMind GeoIP](https://www.maxmind.com/zh/geoip-demo)
- 安全威胁情报服务，通过信息共享渠道了解识别攻击者的来源、类型和安全厂商确认情况，做到知己知彼。

![](http://riboseyim-qiniu.riboseyim.com/ntopng-geomap.png)
![](http://riboseyim-qiniu.riboseyim.com/Cyber-Security-Weibu-Demo.png)


## 参考文献
- [维基百科：NetFlow](https://en.wikipedia.org/wiki/NetFlow)
- [sflow.com:InfluxDB and Grafana](http://blog.sflow.com/2014/12/influxdb-and-grafana.html)
- [sflow.com:Metric export to Graphite](http://blog.sflow.com/2013/11/metric-export-to-graphite.html)
- [sflow.com:Exporting events using syslog](http://blog.sflow.com/2013/11/exporting-events-using-syslog.html)
- [sflow.com:Cluster performance metrics](http://blog.sflow.com/2013/02/cluster-performance-metrics.html)
- [sflow.com:Using a proxy to feed metrics into Ganglia](http://blog.sflow.com/2015/12/using-proxy-to-feed-metrics-into-ganglia.html?m=1)
- [李晨光：详解网络流量分析](http://chenguang.blog.51cto.com/350944/1761284)
- [飞翔的单车：解决zabbix用snmp监控网络流量不准的问题](http://xiaosu.blog.51cto.com/2914416/1590219)
- [lifeofzjs.com:Nagios监控工具介绍](http://lifeofzjs.com/blog/2014/07/11/nagios/)
- [王基立:Nagios企业级系统监控方案](http://www.infoq.com/cn/articles/nagios-enterprise-level-system-monitor)
- [Top 10 Best Free Netflow Analyzers and Collectors for Windows](https://www.pcwdld.com/best-free-netflow-analyzers-and-collectors-for-windows)
- [JUNIPER Networks:Juniper Flow Mornitoring](https://www.juniper.net/us/en/local/pdf/app-notes/3500204-en.pdf)
- [nProbe:An Extensible NetFlow v5/v9/IPFIX Probe for IPv4/v6](http://www.ntop.org/products/netflow/nprobe/)
- [华为：一种计算机网络远程网络监控方法，CN 1393801 A](https://encrypted.google.com/patents/CN1393801A?cl=zh)
- [Cisco Systems NetFlow Services Export Version 9](https://www.ietf.org/rfc/rfc3954.txt)
- [manageengine.com：NetFlow Analyzer - Supported Devices](https://www.manageengine.com/products/netflow/supported-devices.html)
- [H3C.com:NetStream技术介绍](http://www.h3c.com/cn/Products___Technology/Technology/ComwareV5/System_Management/Other_technology/Technology_recommend/200905/634610_30003_0.htm)
