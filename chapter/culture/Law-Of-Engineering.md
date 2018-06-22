# 软件工程实践中的十大法则

## 摘要
- 0.1 Moore’s Law
- 0.2 Metcalfe's Law
- 1、Little’s Law
- 2、Goodhart’s Law
- 3、Conway’s Law
- 4、Parkinson’s Law
- 5、Humphrey’s Law
- 6、Law of the instrument
- 7、Law of Demeter
- 8、Dude’s Law
- 9、Law of 2 Feet
- 10、Law of Propinquity
- 11、Linus' Law

#### 0.1 Moore’s Law

摩尔定律,由英特尔创始人之一戈登·摩尔提出来的。其内容为：积体电路上可容纳的电晶体（晶体管）数目，约每隔两年便会增加一倍。主要有以下三种演绎版本：
- 集成电路芯片上所集成的电路的数目，每隔18个月就翻一倍。
- 微处理器的性能每隔18个月提高一倍，或价格下降一半。
- 用一个美元所能买到的电脑性能，每隔18个月翻两倍。

这个定律 **被引用最多，分歧很大，反对声音也最多**，有预测认为摩尔定律的极限将在2025年左右到来，但也有更乐观的预测认为还能持续更久。

#### 0.2 Metcalfe's Law
梅特卡夫定律，由乔治·吉尔德于1993年提出，但以计算机网络先驱、3Com公司的创始人罗伯特·梅特卡夫的姓氏命名。内容是：一个网络的价值等于该网络内的节点数的平方，而且该网络的价值与联网的用户数的平方成正比。该定律指出，一个网络的用户数目越多，那么整个网络和该网络内的每台电脑的价值也就越大。

#### 1. Little’s Law

**Cycle Time = Work in Progress / Throughput**

中译为利特尔法则，由MIT （Sloan School of Management）的教授John Little于1961年提出：在一个稳定的系统 L中，长期的平均顾客人数，等于长期的有效抵达率，系统中的平均存货等于存货单位离开系统的比率（亦即平均需求率）与存货单位在系统中平均时间的乘积。

>the relationship between the average number of customers in a store, their arrival rate, and the average time in the store.

应用领域：精益生产、系统运筹方面应用广泛，Kanban(看板)方法论的基石。

>It is the basis of Kanban.

![](http://ombx24fbq.bkt.clouddn.com/PM_Tools_kanban_Demo_5.png)

>The power of Little’s Law to Kanban teams is not its ability to predict WIP, Thoughput or Leadtime. The true power lies in its ability to influence team behavior with its underlying assumptions.

#### 2. Goodhart’s Law

>When a measure becomes a target, it ceases to be a good measure.
**当一个政策变成目标，它将不再是一个好的政策。**

古德哈特定律（Goodhart's law），是以[Charles Goodhart](Charles Goodhart)的名字命名，他在1975年的文章中首次发表（当时作为英格兰银行的政策建议）：当政府试图管理这些金融财产的特别标识时，它们便不再是可信的经济风向标。应用领域：公共管理，敏捷开发，目标管理

>了解一个指标有意义与否的一个好的方法是试着去理解其所体现的个人价值。关注那些可以给出好的建议、体现沟通技能和良好态度，尤其是需要巨大的付出才能作弊的指标。

- [程序员日志：是什么导致优秀的程序员写出如此垃圾的代码？](http://www.sohu.com/a/136852371_163156)

- [blogospheroid:The Importance of Goodhart's Law](http://lesswrong.com/lw/1ws/the_importance_of_goodharts_law/)

#### 3. Conway’s Law

**Any organization that designs a system (defined broadly) will produce a design whose structure is a copy of the organization's communication structure.任何组织在设计一套系统（广义概念上的系统）时，所交付的设计方案在结构上都与该组织的通信结构保持一致 -- Melvyn Conway, 1967**

![](http://omb2onfvy.bkt.clouddn.com/MicroService_Org.png)

>《人月神话》：Adding manpower to a late software project makes it later --Fred Brooks, (1975)

为了赶进度加程序员就像用水去灭油锅里的火一样，原因在于：沟通成本 = n(n-1)/2，沟通成本随着项目或者组织的人员增加呈指数级增长。很多项目在经过一段时间的发展之后，都会有不少恐龙级代码，无人敢挑战。比如一个类的规模就多达数千行，核心方法近千行，大量重复代码，每次调整都以失败告终。庞大的系统规模导致团队新成员接手困难，项目组人员增加导致的代码冲突问题，系统复杂度的增加导致的不确定上线风险、引入新技术困难等。应用领域：Micro-Service 微服务

![](http://omb2onfvy.bkt.clouddn.com/MicroServices_Math_Demo.png)

#### 4. Parkinson’s Law

>Work expands so as to fill the time available for its completion.
**在工作能够完成的时限内，工作量会一直增加，直到所有可用时间都被填充为止**

帕金森定理（英语：Parkinson's law），由英国作家[西里尔·诺斯古德·帕金森](https://zh.wikipedia.org/wiki/%E8%A5%BF%E9%87%8C%E7%88%BE%C2%B7%E8%AB%BE%E6%96%AF%E5%8F%A4%E5%BE%B7%C2%B7%E5%B8%95%E9%87%91%E6%A3%AE)提出，语最早出现在1955年《经济学人》中的幽默短文。在他后续的书中进一步阐述（《Parkinson's Law: The Pursuit of Progress》）。官僚组织随着时间而扩大的速率。一个官僚组织(作者注：包括非政府组织)中的雇员总数，通常以每年5-7%的速度增加。他认为，有两股力量造成了这个增长：(1) 一个官员希望他的下属增加，但不希望解雇造成敌人增加；以及(2) 官员会制造工作给彼此。 **同意！**

#### 5. Humphrey’s Law

>The user will never know what they want until after the system is in production (maybe not even then)
**用户将永远不知道他们想要什么，直到系统已经上线。（也许上线了也不知道）**

>"服务员，给我来份宫保鸡丁！"
"好嘞！"
------------这叫原始需求
大厨做到一半。
"服务员，菜里不要放肉。"
"不放肉怎么做啊？"
"不放肉就行了，其它按正常程序做，不就行了，难吗？"
"好的您稍等"
------------中途需求变更
大厨："你大爷，我肉都回锅了"
服务员："顾客非要要求的嘛，你把肉挑出来不就行了吗"
大厨："行你大爷"  然而还是一点点挑出来了
------------改动太大，部分重构
。。。。。。
"服务员，这样吧，腐竹不要了，换成蒜毫能快点吗？对了，顺便加点番茄酱"
------------因工期过长再次改动需求
大厨："我日了狗啊，你TM不知道蒜毫也得焯水啊？还有你让我怎么往热菜里放番茄酱啊？？"
服务员："焯水也比等腐竹强吧，番茄酱往里一倒不就行了吗？很难吗？"
大厨："腐竹我还得接着泡，万一这孙子一会又想要了呢。"
------------频繁改动开始导致大量冗余

- [冷兔笑话：码农做项目与点菜类比的笑话](https://www.maxcell.org/xiaohua/2016/009953.html)
- [John Eaton:The Three Laws of Software Development - Humphrey's Law](https://www.linkedin.com/pulse/20140712143010-6227721-the-three-laws-of-software-development-humphrey-s-law)

#### 6. Law of the instrument

>If all you have is a hammer, everything looks like a nail

**工具定律，锤子定律或马斯洛的锤子**。表现为对一个熟悉的工具过度的依赖，“如果你有的只是一个锤子，那么所有的东西看起来都像一个钉子”以及一些由此转化的说法，来自于亚伯拉罕·马斯洛1966年发行的《科学的心理学》一书。例如：
- “XXX是世界上最好的语言，没有之一”
- “加强party的领导”

- [Youtube:Law of the instrument](https://www.youtube.com/watch?v=7bf8uzAVsRE)

#### 7. Law of Demeter

>Law of Demeter is also called “Principle of Least Knowledge”

得墨忒耳定律（Law of Demeter，缩写LoD）是一种软件开发的设计指导原则，特别是面向对象的程序设计。美国东北大学在1987年末发明，名称来源于希腊神话中的农业女神，孤独的得墨忒耳。简单描述为:
- 每个单元对于其他的单元只能拥有有限的知识：只是与当前单元紧密联系的单元；
- 每个单元只能和它的朋友交谈：不能和陌生单元交谈；
- 只和自己直接的朋友交谈。

a.b.Method()违反了此定律，而a.Method()不违反此定律。一个简单例子是，人可以命令一条狗行走（walk），但是不应该直接指挥狗的腿行走，应该由狗去指挥控制它的腿如何行走。

- [Dan Manges:Misunderstanding the Law of Demeter](https://www.dan-manges.com/blog/37)

#### 8. Dude’s Law

**Value (of a project) = Why over How [or Value = Why / How]**

>“David [the law’s creator] comes from the music industry. New musicians focus on having a nice sound. Professional musicians first think about the mood of the song, the emotion they want to bring across and then work out the sound that fits it”

该法则的发明者来自音乐产业。新手音乐家注重好听的声音。专业的音乐家首先考虑歌曲的情绪，他们想要传达的情感，然后找出适合它的声音。应用领域：项目管理、目标管理

- [Dude’s Law, Don Reinertsen and Walmart](http://devjam.com/2010/08/05/dudes-law-gordon-pask-shoveler/)

- [How does a former musician build a successful business in the agile space? DevJam leads the way with David Hussman](https://futureofprojectmanagement.com/2012/06/21/how-does-a-former-musician-build-a-successful-business-in-the-agile-space-devjam-leads-the-way-with-david-hussman/)

#### 9. Law of 2 Feet

>If at any time during our time together you find yourself in any situation where you are neither learning nor contributing, use your two feet, go someplace else.

![](https://workshopbank.com/wp-content/uploads/2016/11/open-space-guidlines.jpg)

应用领域：组织沟通

- [Youtube:Online Open Space Technology Meetings](https://www.youtube.com/watch?v=QhNQ8Mhehpw)
- [Open Space Technology](https://en.wikiversity.org/wiki/Open_Space_Technology)
- [What Is Open Space Technology?](http://openspaceworld.org/wp2/what-is/)

#### 10. Law of Propinquity

>The probability of two people communicating is inversely proportional to the distance between them.两个人交流的概率与他们之间的距离成反比。数字时代亦然。

>Cucumbers get more pickled than brine gets cucumbered.
**当你进入一个环境，它会更多地影响你而不是相反**

**the more we see people and interact with them, the more probable we are to like them.**

- [Twitter and the law of propinquity](http://chiefmartec.com/2009/01/twitter-and-the-law-of-propinquity/)
- [Bonus: Prescott’s Pickle Principle](http://www.giveemthepickle.com/pickle_principle.htm)

#### 11. Linus' Law
以Linux创始人林纳斯·托瓦兹（Linus Torvalds）的名字来命名，但最先由埃里克·斯蒂芬·雷蒙（Eric S. Raymond）的作品《大教堂和市集》（The Cathedral and the Bazaar）中所提出。“足够多的眼睛，就可让所有问题浮现”（given enough eyeballs, all bugs are shallow）。

应用领域：代码审查。“只要有足够的单元测试员及共同开发者，所有问题都会在很短时间内被发现，而且能够很容易被解决”。将代码展示给更多开发者以达成共识。在2001年出版的《黑客伦理与信息时代的精神》(The Hacker Ethic And the Spirit of th Information Age)里，林纳斯在其为此书做的序言中，自己定义了另外一个林纳斯定律

>人类所有的动机可分为递进的三种类型 — 生存（survival）、社会生活（social life）、娱乐（entertainment）。...... “黑客”是已经超越利用计算机谋生存而进入后面两个阶段的人。计算机本身就是娱乐。黑客坚信没有比这更高的动力阶段。
