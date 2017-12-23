# 基于看板（Kanban）的管理实践

## 摘要
Kanban看板是一种可视化生产管理系统，利用看板卡来增强信号量、标记生产过程，促进系统渐进式变化，提高团队协作的效率。本文主要包括以下内容：
- 核心理论：流动性、可视化
- 实践方法：看板设计模式、可视化技巧、平衡群体智慧和个体差异

## 一、Kanban看板核心理论

#### 1、起源
Kanban(看板)是一种生产管理系统，起源于1940年代的丰田汽车公司的TPS (Toyota Production System)。简单来说看板是一系列简单的视觉符号，它的出现是为了达到即时化生产（Just in time，JIT），JIT认为库存会带来成本以及浪费，而不是增加或储存价值，这与传统会计学不同，它鼓励企业逐步消除库存，以便削减生产流程中的成本，在管理中逐渐适应“零库存”的状态。
![](http://ombx24fbq.bkt.clouddn.com/Kanban_Toyota.gif)

#### 2、BASIC of Kanban：流动性
**Cycle Time = Work in Progress / Throughput**
Kanan看板系统的基础理论是利特尔法则（ Little’s Law），由MIT （Sloan School of Management）的教授John Little于1961年提出：在一个稳定的系统 L中，长期的平均顾客人数，等于长期的有效抵达率，系统中的平均存货等于存货单位离开系统的比率（亦即平均需求率）与存货单位在系统中平均时间的乘积。
>the relationship between the average number of customers in a store, their arrival rate, and the average time in the store.

根据利特尔法则，跟踪工作及其进展的最重要的目标是：限制在制品（Work in process，WIP），例如尚未完成制造过程的商品，或是停留在库存仓库或是产线上，等待着进一步处理的商品。

>高质量地完成工作只有在工作以可持续的节奏流动时才有可能。发现并维持这一节奏只有在在制品小于团队产能的情况下才有可能。— Jim Benson  《Personal Kanban》作者

看板的主要部分是故事卡片（Story Cards），上面显示了你和你的团队所需的所有必要信息。在基本设置中，故事卡片分为三个主要阶段（列）——To Do(计划做的事情）、In Progress (正在进行)、Done(完成)。根据实际的需要，还可以分为多个阶段。你也可以使用泳道任务（swimlanes）拆分为不同类别，最后根据进程随时移动状态和泳道之间的问题。

#### 3、可视化工作区

>The power of Little’s Law to Kanban teams is not its ability to predict WIP, Thoughput or Leadtime. The true power lies in its ability to influence team behavior with its underlying assumptions.

像看板这样的可视化系统因我们对视觉信息的偏好而成为了强有力的工具。真实地看到工作和流程有助于理解。看板墙可以作为一个简单的信息节点，使任何人都能走过来并了解项目的当前状态。— Jim Benson  《Personal Kanban》作者

看板方法要求团队将组织处理信号的规则显式化，利用精益度量体系对系统及时进行分析回顾，不断优化信号处理模式。这就形成了一个**完整高效的反馈闭环**，最终建立一个具备自我完善能力，并能随着组织发展和环境变化不断演进的自适应系统。— 李兴双 中国工商银行软件开发中心

Kanban看板可视化的一些技巧：
- Kanban看板墙需放置于工作区醒目位置
- 不同事件类别使用不同颜色，紧急事件（URGENT）使用醒目颜色（红色）
- 故事卡片常规要素：编号，标题，负责人，截止日期
- 故事卡片叠加要素：重要度，约束条件，延期原因等特殊描述
- 照相机定期快照（周），及时复盘总结（月）
- 限制进行中（In-Process）事件数量，限制已经完成（Done）事件数量（折叠或者更换新的Kanban看板墙）

总之，Kanban看板系统的本质意义在于促进团队成员对作业流程、过程和风险达成共同理解, 可视化的作用在于增强不确定风险的信息量（故障、阻碍因子、延期原因、特殊要求等），促进系统各方及时作出响应，或者通过快照机制随时复盘、研究改进措施。

## 二、Kanban看板实践注意事项

#### 1、看板墙设计模式
> ** Conway's law：Any organization that designs a system (defined broadly) will produce a design whose structure is a copy of the organization's communication structure.**

康威定律指出：任何组织在设计一套系统（广义概念上的系统）时，所交付的设计方案在结构上都与该组织的通信结构保持一致。设计一套可以落地的看板墙规则，第一个步骤不是按照教科书照搬其他企业的看板墙风格，而应首先画出自己所在组织的架构图。其内容包括管理架构、关联方和关注重点，在此基础之上再设计横坐标（Workflow）与纵坐标（Items）, 应当尽可能地使看板墙符合组织结构，而不是相反。

规模的增长很容易让工作陷入停滞，沟通成本加上工作划分会导致效率变化。具体表现为团队之间在工作时间里召开的沟通会议呈指数增长，不同团队的工作量差别很大，不同团队的工作节奏紊乱脱节。Kanban看板实践中，应考虑上述情况，在技术上作出特殊处理：例如当团队规模较大时，对看板墙进行拆分，不同的业务单元使用不同的看板，综合管控部门聚焦于较大标的，而技术实现部门侧重于细节。切记只有当工作可以划分时，你才可能通过增加团队成员来提高效率。

![](http://ombx24fbq.bkt.clouddn.com/Kanban_Pattern_DevOps.png)

#### 2、平衡群体智慧与个体差异

>保持群体智慧的唯一方式是保护每个人的独立性。--乔纳-莱勒（Jonah Lehrer）

群体会对特定类型的问题给出较好的答案。当大量的人做出回应时，他们产生了很多答案，但其平均值、中位数或最常见的回答往往会是一个很好的答案。这比人们被彼此隔离来发表独立意见更为可行。......但是，当人们看到别人提供的答案后，就出现了一些不好的事情。人们会修改自己的答案，从而造成最后的答案集合变得不够多样化，这样最好的答案就可能无法脱颖而出。人们通过强化会变得更加自信，但是精确度却没有改进。**群体智慧依赖于多样性和独立性**。在社交网络上（以及在企业、组织和政府机构工作的人员团队中），**同事压力和主导人物可能会降低该团体的智慧**。（《火的礼物-人类与计算技术的终极博弈（第4版）》，Sara Baase）

具体落地实践中，需要承认不同团队、不同团队成员的个体差异。这里所说的个体差异主要表表现为性格差异，它通过人对事物的倾向性态度、意志、语言、逻辑、行为方式等方面表现出来。一般情况下，随机组成的团队成员之间，心理风格的差异会非常显著。例如在感知方面，可以划分为亢奋敏感型、被动感知型等；在信息反馈习惯上，存在主动型和滞后型；在计划性方面也有不同的偏好倾向，有人喜欢按部就班的任务模式，有人善于临机应变，处置紧急情况更能触发神经亢奋，然前者则容易陷入焦虑和挫败感。组织模式可以简化为两种，矩阵式：适用于团队成员之间技能、心理强度较为均匀的理想情况；另外一种是集中式：由一名心理风格较为平和的成员负责日常沟通、统一维护看板墙，即适当缓冲敏感型成员的过度信息输出，另外主动轮询其它被动型成员。
![](http://ombx24fbq.bkt.clouddn.com/Kanban_Pattern_Org.png)

#### 3、慎用“高级”看板

不管什么时代什么组织，优秀管理的本质就是鼓励准确信息迅速向上、横向和向下传递，而最重要的是向上传递。在Kanban看板实践中，我目前的判断是不建议套用倾向量化方案的“高级”看板。

麦克纳马拉曾在福特汽车公司和五角大楼都创下管理奇迹的“神童”，在越南战争中使用量化准则来指挥越战，却导致越战的美军在结构上鼓励虚假信息向上传递：军队从上到下都渴望好消息，于是心照不宣地制造、传递假消息，甚至发明出所谓“尸体数”、“小便数”等荒唐可笑的考核指标，造成严重后果。麦克纳马拉也因为自己的过于“聪明”、刚愎自用，在战争决策上神话破产、风光不再。

>“每个定量指标都表明我们正在打胜仗。— 麦克纳马拉”

量化的破坏性经常被人忽略：第一，机会成本。量化是很耗时的，大量宝贵的管理时间浪费在量化上，做其他重要事情的时间就减少了。第二，量化容易误导决策。定量信息造成了各种各样错得离谱的决策。

当你的组织还远远没有达到精益生产、杜绝延期的境界之前，将敏捷的Kanban看板应用急用进入量化范畴，是不明智的。

![](http://ombx24fbq.bkt.clouddn.com/Kanban_Pattern_YR_201707.png)

## 参考文献
- [Marcus Hammarberg:Kanban in Action  (Youtube Video)](https://www.youtube.com/watch?v=ufCa1VlItLA)
- [Steven Tomas:Little’s Law – the basis of Lean and Kanban](http://itsadeliverything.com/littles-law-the-basis-of-lean-and-kanban)
- [PDF《kanban in Action》](http://3.droppdf.com/files/p99PT/kanban-in-action.pdf)
- [Just-in-Time — Philosophy of complete elimination of waste](http://www.toyota-global.com/company/vision_philosophy/toyota_production_system/just-in-time.html)
- [澎湃新闻：麦克纳马拉：以铁腕重塑五角大楼，却因越战一败涂地](http://www.thepaper.cn/newsDetail_forward_1365119_1)
- [何勉：解析精益产品开发（一）—— 看板开发方法](http://www.infoq.com/cn/articles/kanban-development-method)
