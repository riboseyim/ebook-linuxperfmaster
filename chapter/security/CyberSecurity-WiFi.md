# Cyber-Security:警惕 Wi-Fi 漏洞 KRACK

## 摘要
- 1、不可信任的基础设施：Wi-Fi
- 2、安全通信协议：HTTPS
- 3、历史遗留问题：DNS
- 4、最后一英里的安全：VPN

KRACK 攻击将目标放在你的移动设备和 Wi-Fi 接入点之间的链路，它可能是一台路由器 —— 在你的家里、办公室、社区图书馆或者你最喜欢咖啡店。 下面这些技巧能够帮助您提高网络连接的安全性。

密钥重安装攻击 （KRACK, Key Reinstallation Attacks)  概要如下:
- 基于 WPA2 无线握手协议的漏洞，攻击者可能在你的设备和Wi-Fi接入点之间嗅探或者操纵流量
-  WPA2 标准存在定义含糊以及标准实现不够严谨，特别是对于 Linux 和 Android 设备来说问题尤其糟糕。事实上，在底层操作系统被修补之前，该漏洞允许攻击者强制所有无线通信流量不进行任何加密。
- 这个漏洞可以在客户端修补，所以事情并没有到不可收拾的地步。WPA2 无线加密标准并没有过时，WEP （Wired Equivalent Privacy，有线等效保密协议）标准也是同样的道理（解决该问题的方案并不是切换到 WEP ）。
- 最流行的 Linux 发行版已经在客户机上发送修复此漏洞的更新，所以需要尽快更新。
- Android 将很快为这个漏洞推送补丁程序。如果您的设备正在接收 Android 安全补丁，您不久就会收到一个修复程序。如果您的设备不再接收这样的更新，那么这个特殊的漏洞仅仅是您停止使用旧的、不支持更新的 Android 设备的另一个原因。

也就是说，从我的观点来看，Wi-Fi 仅仅是不可信任基础设施链条中的另一个环节，我们应该避免把它当作完全可信的通信渠道。

#### 1、不可信任的基础设施：Wi-Fi
如果现在阅读本文是通过你的笔记本电脑或者移动电话，那么你的通信链可能看起来应该是这样的：
![](http://omb2onfvy.bkt.clouddn.com/BlankNetworkDiagram-Basics.png)

KRACK 攻击将目标放在你的移动设备和 Wi-Fi 接入点之间的链路，它可能是一台路由器 —— 在你的家里、办公室、社区图书馆或者你最喜欢咖啡店。
![](http://omb2onfvy.bkt.clouddn.com/BlankNetworkDiagram-WhereKrackshappen.png)

实际上，这个图应该类似于这样：
![](http://omb2onfvy.bkt.clouddn.com/BlankNetworkDiagram-Everywhere.png)

Wi-Fi 仅仅是我们漫长的通信链中的第一个不应该信任的环节。如果让我猜的话，你正在使用的 Wi-Fi 路由器可能从开始使用的那天起就没有收到过安全更新。更糟的是，它可能是默认的或容易被猜到的管理凭据（用户名/口令/密钥等），从来就没有更改过。除非你自己初始化并配置了路由器，同时你还能记住自己最后一次是什么时候更新它的固件，总之，你应该假设它现在是由别人控制，不能被信任。

通过 Wi-Fi 路由器，我们进入了一个由各类不可信任基础设施覆盖的区域 — 取决于你的偏执狂水平。在我们的上游有各类 ISP 和服务提供商，他们中的许多人捕获，监视，改变，分析和销售我们的个人流量，试图从我们的浏览习惯中获得更多的钱。通常，他们自己的安全补丁计划留下了许多有待改进的地方，最终使我们的流量暴露在恶意人士的眼里。

通常在互联网上，我们不得不担心强大的、国家级的演员操纵核心网络协议（例如 [BGP hijacking，BGP 劫持](https://en.wikipedia.org/wiki/BGP_hijacking) ） ，为了支持质量监控程序或者执行国家级的流量过滤能力。

#### 2、安全通信协议：HTTPS
幸运的是，我们有一个基于不安全介质进行安全通信的解决方案，我们每天都在使用它 —— HTTPS 协议对我们的 Internet 流量进行点对点加密，并确保我们可以信任我们所通信的站点。

Linux 基金会发起 [“Let’s Encrypt” 项目,Let’s Encrypt is a free, automated, and open Certificate Authority.](https://letsencrypt.org/)，便于网站所有者全球提供终端到终端的加密，这有助于确保任何受损的设备，我们的个人设备，我们要访问的网站之间没有通信泄密风险。

好吧…只能说尽量接近没有风险。

![](http://omb2onfvy.bkt.clouddn.com/BlankNetworkDiagram-HTTPS.png)

#### 3、历史遗留问题：DNS
即使我们忠实地使用 HTTPS 协议来创建一个可信的通信渠道，攻击者还有一个机会，访问我们的 Wi-Fi 路由器或改变我们的 Wi-Fi 流量 — 例如 KRACK 攻击 — 可以诱导我们同错误的网站通信。他们可以这样做事利用了另一个事实，即我们仍然十分依赖于 DNS —— 一种上世纪80年代开始应用，不加密，非常容易被欺骗协议。
![](http://omb2onfvy.bkt.clouddn.com/BlankNetworkDiagram-LOLDNS.png)

DNS （Domain Name System，域名系统）可以将人类友好的域名例如“linux.com”转换成 IP 地址 —— 计算机可以基于 IP 地址相互通信。为了将域名转换成 IP 地址，计算机会查询解析器软件 —— 通常运行在 Wi-Fi 路由器上或操作系统本身。解析器将查询一个包括 “root” 域名服务器的分布式网络，找出系统在互联网上所谓的“权威”的信息，域名“Linux .com”对应的IP地址。

麻烦的是，上述过程都是基于未经身份验证的通信，很容易假冒，明文协议和响应都可以很容易地被攻击者改变，使查询返回不正确的数据。如果有人设法进行 DNS 查询欺骗并返回错误的IP地址，他们就可以操纵我们系统的 HTTP 请求到指定的地方。

幸运的是，HTTPS 协议有很多内置的保护，可以确保不容易被别人假装成另一个站点。恶意服务器上的 TLS 证书必须匹配您请求的 DNS名称 ，TLS 证书由一个卓有声誉的 数字证书认证中心(Certificate Authority， CA)签发 ，并获得浏览器认可。如果不是这样的话，浏览器会显示出一个很大的警告：你要与之沟通的主机不是他们所说的那个人。如果你看到这样的警告，在选择忽略之前，请格外小心，因为你可能会泄露你的秘密给那些会使用它们的人攻击你。

如果攻击者完全控制了路由器，他们首先可以阻止您的连接使用HTTPS，通过拦截服务器的响应，指示浏览器设置安全连接（这称为“SSL strip 攻击”）。为了帮助保护您免受这种攻击，网站可以增加一个 特殊响应报头，告诉浏览器它们在未来始终使用HTTPS 协议进行通信，但这只是在你的第一次访问之后才有效。对一些非常受欢迎的网站，浏览器现在包括一个硬编码的域名列表，可以设置在第一次访问的时候也使用 HTTPS 协议 。

DNS 欺骗的解决方案称为 DNSSEC （Domain Name System Security Extensions ，CDNS安全扩展，由 IETF 提供的一系列 DNS 安全认证的机制，RFC 2535 ），但它看起来距离被接受的那一天还很遥远，其中有一个重要的障碍 —— 实时感知。DNSSEC 普遍使用之前，我们必须假定，我们收到的 DNS 信息不完全可信的。

#### 4、最后一英里的安全：VPN

所以，如果你不能信任 Wi-Fi — 和/或在地下室的无线路由器，它可能比你的大多数宠物的年龄都大，你可以采取什么措施来确保你的设备和互联网通信“最后一英里”的完整性呢？

一个可接受的解决方案是使用一个信誉良好的 VPN 提供商，在你的系统和基础设施之间建立安全通信链路。我们的希望在于他们比你的路由器厂商和你当前的互联网服务提供商更加关注安全，所以他们可以更好地保证您的流量不受嗅探，或者遏制恶意人士的骚扰。让你所有的工作站和移动设备使用 VPN ，可确保类似  漏洞，例如 KRACK 攻击或不安全的路由器等，不影响你与外部世界通信的完整性。

![](http://omb2onfvy.bkt.clouddn.com/BlankNetworkDiagram-VPN.png)

这里的重要警告是，在选择 VPN 提供商时，必须保证它们的合理地可信性；否则，您只是同另一组恶意行为者进行交易。远离任何提供“免费 VPN”的提供商，他们很可能把你作为间谍目标，或者出售您的流量给营销公司赚钱。 

并非所有的设备都需要安装 VPN，但您每天使用的、涉及访问个人私人信息网站的—— 尤其是访问您的金钱和身份信息（政府、银行网站、社交网络等）的任何东西都必须得到安全保护。VPN 不是应对所有网络层漏洞的灵丹妙药，但它肯定会有所帮助，当你在机场使用不安全的 Wi-Fi 卡，或者下次类似 KRACK 漏洞被发现的时候。

## 参考文献
- [BGP hijacking，BGP 劫持](https://en.wikipedia.org/wiki/BGP_hijacking)
- [“Let’s Encrypt” 项目,Let’s Encrypt is a free, automated, and open Certificate Authority.](https://letsencrypt.org/)

## 扩展阅读: 网络安全专题合辑《Cyber-Security Manual》
- [Cyber-Security: Linux 容器安全的十重境界](https://riboseyim.github.io/2017/11/12/DevOps-Container-Security/)
- [Cyber-Security: 警惕 Wi-Fi 漏洞，争取安全上网](https://riboseyim.github.io/2017/10/29/CyberSecurity-WiFi/)
- [Cyber-Security: Web应用安全：攻击、防护和检测](https://riboseyim.github.io/2017/08/31/CyberSecurity-Headers/)
- [Cyber-Security: IPv6 & Security](https://riboseyim.github.io/2017/08/09/Protocol-IPv6/)
- [Cyber-Security: OpenSSH 并不安全](https://riboseyim.github.io/2016/10/06/CyberSecurity-SSH/)
- [Cyber-Security: Linux/XOR.DDoS 木马样本分析](https://riboseyim.github.io/2016/06/12/CyberSecurity-Trojan/)
- [浅谈基于数据分析的网络态势感知](https://riboseyim.github.io/2017/07/14/Network-sFlow/)
- [Packet Capturing:关于网络数据包的捕获、过滤和分析](https://riboseyim.github.io/2017/06/16/Network-Pcap/)
- [新一代Ntopng网络流量监控—可视化和架构分析](https://riboseyim.github.io/2016/04/26/Network-Ntopng/)
- [Cyber-Security: 事与愿违的后门程序 | Economist](http://www.jianshu.com/p/670c4d2bb419)
- [Cyber-Security: 美国网络安全立法策略](https://riboseyim.github.io/2016/10/07/CyberSecurity/)
- [Cyber-Security: 香港警务处拟增设网络安全与科技罪案总警司](https://riboseyim.github.io/2017/04/09/CyberSecurity-CSTCB/)
