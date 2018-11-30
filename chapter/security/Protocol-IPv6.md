# Cyber-Security: IPv6 & Security

## 概要
- IPv6 Overview
- IPv6 & Cyber-Security  (IPsec,TLS/SSL,NAT,Source routing)
- IPv6 & Linux Security （iptables vs ip6tables,SSH/SCP/Rsync）

## IPv6

IPv6(Internet Protocol version 6)是互联网协议的最新版本，用于分组交换互联网络的网络层协议，旨在解决IPv4地址枯竭问题。IPv6二进位制下为128位长度，以16位为一组，每组以冒号“:”隔开，可以分为8组，每组以4位十六进制方式表示。例如：2001:0db8:85a3:08d3:1319:8a2e:0370:7344 是一个合法的IPv6地址。

![Allocation of IPv4 address space: the United States uses more than a third of the available IPv4 addresses.](https://www.linux.com/sites/lcom/files/styles/rendered_file/public/ip-address-allocation.png?itok=ajvQlGr-)

#### IPv6 报文包格式
定义：[RFC 1883](https://tools.ietf.org/html/rfc1883)中定义的原始版本，[RFC 2460](https://tools.ietf.org/html/rfc2460)中描述的现在提议的标准版本。
![](http://riboseyim-qiniu.riboseyim.com/Network_IPv6_Packet.png)

#### IPv6地址分类
- 单播地址（unicast）：单播地址标示一个网络接口，协议会把送往地址的数据包送往给其接口。单播地址包括可聚类的全球单播地址、链路本地地址等。
- 多播地址（multicast）：多播地址也称组播地址。多播地址也被指定到一群不同的接口，送到多播地址的数据包会被发送到所有的地址。
- 任播地址（anycast）：Anycast 是 **IPv6 特有的数据发送方式**，它像是IPv4的Unicast（单点传播）与Broadcast（多点广播）的综合。

Anycast像 IPv4 多点广播（Broadcast）一样，会有一组接收节点的地址栏表，但指定为Anycast的数据包，只会发送给距离最近或发送成本最低（根据路由表来判断）的其中一个接收地址，当该接收地址收到数据包并进行回应，且加入后续的传输。该接收列表的其他节点，会知道某个节点地址已经回应了，它们就不再加入后续的传输作业。以目前的应用为例，Anycast地址只能分配给路由器，不能分配给电脑使用，而且不能作为发送端的地址。

## IPv6 & Cyber-Security

对我们网络的攻击来自各种各样的来源：社会工程（Social Engineering）、粗心、垃圾邮件、网络钓鱼、操作系统漏洞、应用程序漏洞、广告网络、跟踪和数据收集、服务提供商窥探等。作为一个新兴的网络协议，安全问题同样无法避免。

#### IPsec/TLS/SSL/SSH
IPsec（网际网络安全协议，Internet Protocol Security，[RFC 4301](https://tools.ietf.org/html/rfc4301)、[RFC 4309](https://tools.ietf.org/html/rfc4309) ），旨在在网络层为IP分组提供安全服务，包括访问控制、数据完整性、身份验证、防止重放和数据机密性。IPsec原本是为IPv6开发，但是在IPv4中已被大量部署。最初，IPsec是IPv6协议组中不可少的一部分，但现在是可选的。

在传输模式下，IPsec在IP报头和高层协议之间插入一个报头，IP报头与原始IP报头相同，只是IP协议字段被改为ESP或者AH，并重新计算IP报头的校验和。IPsec假定IP端点是可达的，源端头不会修改IP报头中的目标IP地址。IPsec 工作在网络层。其它加密协议如 TLS/SSL 和 SSH，工作在传输层之上，是针对具体应用的。
![](http://riboseyim-qiniu.riboseyim.com/Network_Procotol_Security.png)
![](http://riboseyim-qiniu.riboseyim.com/Network_IPsec_PacketHeader.png)

#### NAT = Security ?
>NAT is not and never has been about security.

NAT(Network Address Translation) 是一种在IP数据包通过路由器或防火墙时重写来源IP地址或目的IP地址的技术.NAT 延长了 IPv4的寿命。它的本意是通过地址伪装，阻止外部网络主机的恶意活动，阻止网络蠕虫病毒来提高本地系统的可靠性，阻挡恶意浏览来提高本地系统的私密性。另外，它也为UDP的跨局域网的传输提供了方便。很多防火墙都NAT功能，它使防火墙变成有状态的，检查所有的流量、跟踪哪些数据包进入您的内部主机，并将多个私有内部地址重写到一个外部地址。它在外部网关上创建一个单点故障源，并为拒绝服务（DoS）攻击提供了一个简单的目标。NAT有它的优点，但安全不是其中之一。

#### Source routing
Source routing 允许发送方控制转发，而不是将其留给任何一个包通过的路由器，通常是OSPF（Open Shortest Path First，开放最短路径优先）。Source routing 有时用于负载平衡，和管理VPN（Virtual Private Network，虚拟专用网络），所以，Source routing 并不是 IPv6 带来的特性，但是提出了许多安全问题。你可以用它来探测网络，获取信息，绕过安全设备。路由报头0型（RH0）是使源路由的IPv6扩展报头，它一直受到抨击，因为它使一中非常聪明的DoS攻击被放大，在两个路由器之间的反弹包直到它们超载及带宽耗尽。

#### 大就是强 ？
有些人认为IPv6地址空间如此之大，为网络扫描提供了一种防御。这是一种错误的观念。我们确实有潜在的庞大地址资源可供应用，但是我们倾向于在可预见的范围组织我们的网络。硬件在变得廉价的和强大，云计算资源的快速发展在惠及企业的同时，也降低了攻击方的门槛和成本（黑客、黑产）。计算机网络的复杂性决定了挫败恶意网络扫描的困难很大，包括在IPv6网络中进行监测辨别哪些属于本地访问、哪些不是，IPv6 控制访问的问题在于，它的管理能力要求超过了其它任何协议。传统的防御工具和方法论都有待更新。

## IPv6 & Linux Security

#### iptables & ip6tables

ip6tables命令和iptables一样，都是Linux中防火墙软件

```bash
# Block All IPv6
$ vi /etc/sysctl.conf:
net.ipv6.conf.all.disable_ipv6 = 1
net.ipv6.conf.default.disable_ipv6 = 1
# load your changes:
$ sudo sysctl -p
net.ipv6.conf.all.disable_ipv6 = 1
net.ipv6.conf.default.disable_ipv6 = 1
# Test
$ ping6 -c3 -I eth0 fe80::f07:3c7a:6d69:8d11
PING fe80::f07:3c7a:6d69:8d11(fe80::f07:3c7a:6d69:8d11)
from fe80::2eef:d5cc:acac:67c wlan0 56 data bytes
--- fe80::2eef:d5cc:acac:67c ping statistics ---
3 packets transmitted, 0 received, 100% packet loss,time 2999s
# Listing
$ sudo ip6tables -L
# Flushing
$ sudo ip6tables -F
```

#### Example Rules

```bash
#!/bin/bash

# ip6tables single-host firewall script

# Define your command variables
ipt6=/sbin/ip6tables

# Flush all rules and delete all chains
# for a clean startup
$ ipt6 -F
$ ipt6 -X

# Zero out all counters
$ ipt6 -Z

# Default policies: deny all incoming
# Unrestricted outgoing

$ ipt6 -P INPUT DROP
$ ipt6 -P FORWARD DROP
$ ipt6 -P OUTPUT ACCEPT

# Must allow loopback interface
$ ipt6 -A INPUT -i lo -j ACCEPT

# Reject connection attempts not initiated from the host
$ ipt6 -A INPUT -p tcp --syn -j DROP

# Allow return connections initiated from the host
$ ipt6 -A INPUT -m conntrack --ctstate ESTABLISHED,RELATED -j ACCEPT

# Accept all ICMP v6 packets
$ ipt6 -A INPUT -p ipv6-icmp -j ACCEPT

# Optional rules to allow other LAN hosts access
# to services. Delete $ipt6 -A INPUT -p tcp --syn -j DROP

# Allow DHCPv6 from LAN only
$ ipt6 -A INPUT -m state --state NEW -m udp -p udp \
-s fe80::/10 --dport 546 -j ACCEPT

# Allow connections from SSH clients
$ ipt6 -A INPUT -m state --state NEW -m tcp -p tcp --dport 22 -j ACCEPT

# Allow HTTP and HTTPS traffic
$ ipt6 -A INPUT -m state --state NEW -m tcp -p tcp --dport 80 -j ACCEPT
$ ipt6 -A INPUT -m state --state NEW -m tcp -p tcp --dport 443 -j ACCEPT

# Allow access to SMTP, POP3, and IMAP
$ ipt -A INPUT -m state --state NEW -p tcp -m multiport \
--dport 25,110,143 -j ACCEPT
```

#### SSH and SCP
我们熟悉的文件拷贝工具：SSH, SCP, 和 Rsync 都支持 IPv6，坏消息是他们的语法怪异。

所有 Linux 管理员都知道使用 SSH 和 SCP。 它们在 IPv6 网络中有一些怪异，特别是关于远程地址，一旦你弄明白这个问题就能像过去一样熟练使用 SSH 和 SCP 。默认情况下，sshd 守护进程同时监听 IPv4 和 IPv6 协议，你可以通过 netstat 查看：
```bash
$ sudo netstat -pant|grep sshd
tcp   0  0 0.0.0.0:22  0.0.0.0:*  LISTEN   1228/sshd       
tcp6  0  0 :::22       :::*       LISTEN   1228/sshd
## 通过 sshd_config 中的 AddressFamily 选项禁用任何一种协议。禁用 IPv6:
## 默认选项为 any ，只允许IPv6 inet6
AddressFamily inet

## 在客户端方面，通过 IPv6 网络登陆和 IPv4一样登陆、运行命令行以及退出。
$ ssh carla@2001:db8::2
$ ssh carla@2001:db8::2 backup

## 您可以使用链路本地地址访问本地局域网上的主机。
## 这有一个无正式文档说明的怪癖，会让你抓狂，但现在你知道它是什么：你必须把你的网络接口名称与远程地址用符号 “%” 连接。
$ ssh carla@fe80::ea9a:8fff:fe67:190d%eth0
```

你也可以简化远程root登录。聪明的管理员禁用root登录ssh，所以你必须登录为一个普通用户，然后换为root用户登录。这不是那么费力，但我们可以用一个命令来完成这一切，系统将在倒数120分钟后停止！这个shutdown将持续打开直到完成运行，所以中途你能改变注意并且用寻常的方式取消shutdown，通过 Ctrl+c。
```
$ ssh -t  carla@2001:db8::2 "sudo su - root -c 'shutdown -h 120'"
carla@2001:db8::2 password:
[sudo] password /for carla:
Broadcast message from carla@remote-server
        (/dev/pts/2) at 9:54 ...
## 技巧：强制使用 IPv4 或者 IPv6
$ ssh -6 2001:db8::2
```

SCP是怪异的。你必须在链路本地地址的后面使用符号“%”来指定网络接口，并且将地址用方括号括起来，并避开括号；如果使用全球单播地址则不需要指定网络接口，但是依然需要方括号。
```bash
$ scp filename [fe80::ea9a:8fff:fe67:190d%eth0]:
carla@fe80::ea9a:8fff:fe67:190d password:
filename

$ scp filename [2001:db8::2]:
carla@2001:db8::2 password:
filename

## 示例：登录到远程主机上的不同用户帐户，指定将文件复制到的远程目录，并更改文件名：
scp filename userfoo@[fe80::ea9a:8fff:fe67:190d%eth0]:/home/userfoo/files/filename_2
```

#### Rsync
rsync 要求使用各种标点符号封闭远程的 IPv6 地址。与以往一样，请记住源目录中的尾随斜杠，例如 /home/carla/files/ ，表示只复制目录的内容。省略尾随斜杠将复制目录及其内容。尾随斜线并不重要，关键是你的目标目录。
```
##全球单播地址不需要指定网络接口：
$ rsync -av /home/carla/files/ 'carla@[2001:db8::2]':/home/carla/stuff
carla@f2001:db8::2 password:
sending incremental file list
sent 100 bytes  received 12 bytes  13.18 bytes/sec
total size is 6,704  speedup is 59.86
##使用链路本地地址时必须包含网络接口。
$ rsync -av /home/carla/files/ 'carla@[fe80::ea9a:8fff:fe67:190d%eth0]':/home/carla/stuff
```

## Master: Carla Schroder

[Carla Schroder](http://www.oreilly.com/pub/au/1909) is a self-taught Linux and Windows sysadmin who laid hands on her first computer around her 37th birthday. Her first PC was a Macintosh LC II. Next came an IBM clone--a 386SX running MS-DOS 5 and Windows 3.1 with a 14-inch color display--which was adequate for many pleasant hours of Doom play. Then around 1997 she discovered Red Hat 5.0 and had a whole new world to explore. She is the author of the Linux Cookbook for O'Reilly, and writes Linux how-tos for several computer publications.
- 《Linux Cookbook》2004,$49.99
- 《Linux Networking Cookbook》2007,$44.99
- 《The Book of Audacity》2011,$34.95
