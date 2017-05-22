# Linux之父：人生在世，Just for Fun ！

《Just for Fun: The Story of an Accidental Revolutionary》，是Linux内核的创建者林纳斯·托瓦兹（Linus Torvalds）的自传。
这本书由他和大卫·戴蒙德（David Diamond）联合撰写，叙述林纳斯·托瓦兹从小的成长历程、创建Linux内核、Git的过程以及软件世界的江湖恩怨。
全书主体部分采用一问一答的访谈形式，由戴蒙德在他们的的汽车旅行过程中记录完成；另外一部分收录了林纳斯的几篇专题论述文章，比如作者关于软件版权的一些批判性意见。

![](http://og2061b3n.bkt.clouddn.com/Linus_Torvalds_BookTheme.png)

## I was an ugly child.I was a nerd.I was A geek.

关于自己的童年，林纳斯显然有着强烈的阴影。首先，他自认为是一个长得非常丑的孩子(ugly: unpleasant to look)。具体来说就包括极度没品味的衣着，Torvalds家族标志性的大鼻子，不擅长体育运动、腼腆害羞以及最重要的：很难引起妹子的关注(关于这一点，老林在全书多次反复提到)。

Nerd一词本来原意“a person who is boring, stupid and not fashionable”。俚语中一个稍含贬义的用语，一般指偏爱钻研书本知识，将大量闲暇用于脑力工作，对流行文化不感兴趣，而不愿或不善于体育活动或其他社会活动的人。相对于那些擅长体育、四肢发达、自信且善于泡妞的人来说，nerd的青春说起来都是眼泪啊。

geek和nerd这个词类似，江湖俗称发烧友或怪咖，通常被用于形容对计算机和网络技术有狂热兴趣并投入大量时间钻研的人。它们现在已经在IT圈里流行起来，逐渐从贬义词变成了具有自豪感和身份认同意义的词语，很多中关村或深圳的咖啡店里面都有很多自称Geek的人在和各路投资人畅聊IPO的美好图景。我想这显然归功于林纳斯这代牛人不遗余力地科普推广。

## Tell you about Family.

关于Torvalds家族，主要有三个特点：

1. 教授之家
   林纳斯·托瓦兹的外祖父家族原来只是贫穷的农民，直到外祖父那一代，六兄弟中有两人获得博士学位。（p13）Leo Törnqvist是芬兰第一批统计学教授，同时意外地开启了林纳斯的编程之路。教授购买了一台Commodore VIC 20电脑，期望用户解决工作中遇到的统计计算问题，显然老教授并不善此道。彼时小林纳斯11岁，仅仅是因为好玩，通过阅读手册自学了指令集，并开始使用BASIC语言编写一些统计学方面的小程序。

![](http://og2061b3n.bkt.clouddn.com/Linus_Torvalds_Family_201705.png)

2. 破碎之家（dysfunctional family）
   林纳斯的父母在他很小的时候就离异了。“At times we lived with my dad and his girlfriend, at other times Sara lived with my dad and I lived with my mom.At times both of us lived with my mom.” 他的祖父晚年中风，祖母也年老体弱，一家老小挤在一所旧房子里。作为职业女性，林纳斯的母亲经常需要工作到很晚，林纳斯只得和妹妹自己去购物、安排晚餐。在艰难的日子里，电脑是唯一的寄托。“The computer found a home on a tiny desk against the window, maybe two feet from my bed.(p19)”

![](http://og2061b3n.bkt.clouddn.com/Linux_Torvalds_VIC20.jpg)

3. 左派之家
   Torvalds家族的一大鲜明特色就是左翼传统。祖父是一名诗人和记者，父亲、母亲都是记者，曾是芬兰学生运动、左翼社会运动的活跃分子。直到林纳斯，坚持开放源代码理念也就顺理成章了。他本人对于金钱本身一直也没什么概念，直到1999年，Red Hat公司依靠Linux赚到不好意思以后，主动要求赠送一大笔股票期权给林纳斯（估值2000万美元）。在黑客的江湖里，林纳斯也许是最知名的“喷子”之一：例如，抨击Nvidia是他所接触过的“最烂的公司”（the worst company）和 “最麻烦的公司”（the worst trouble spot），有一次与人争论Git为何不使用C++开发时与对方用“放屁”（原文为“bullshit”、“BS”）互骂，更曾以“一群自慰的猴子”（原文为“OpenBSD crowd is a bunch of masturbating monkeys”）来称呼OpenBSD团队。不用奇怪，这也许只是他们家的记者基因灵魂附体而已。

### About Finnish Army

1989年，大学二年级，林纳斯加入芬兰国防军服11个月的国家义务兵役，军衔少尉，领导一个四、五个人小团队。他们的工作是负责火炮控制单元，大概是指示目标、弹道计算之类。
林纳斯无意于军官职业，也不喜好当领导，军旅生活中最重要的一件事就是看书：
>So there were two things I did that summer.Nothing.And read the 719 pages of **《Operating System:Design and Implementation》**

《操作系统：设计与实现》，作者Andrew S. Tanenbaum的这本书，激活了这位年轻人的视野，促成了林纳斯从事操作系统开发的职业生涯。

不管怎么说，林纳斯对于这段生活是非常感念的：

>some people suggest that the major reason for the required army duty is to give Finnish men something to talk about over beer for as long as they live.They all have something miserable in common.They hated the Army, but they're happy to talk about it afterward.(p30)

### Tell you about Finland.

在这么略带自黑的幽默自传中，作者对祖国芬兰的深情溢于言表。

芬兰地处严寒，有四分之一的地方处在北极圈内，最北的地区夏天有73天太阳不落于地平线下，冬天则有51天不出太阳。大概有500多万居民，93%的人使用芬兰语，大部分可以说英语。70%以上的人属于芬兰信义宗教会（路德宗）的成员。最大的民俗特点就是：一：低调内敛、不爱说话，如果有什么事他们更爱发短信（诺基亚手机发短信还真是无敌）二：宅，死宅！中国的国粹是麻将，芬兰的国粹就是桑拿浴，或者说桑拿才是芬兰真正的国家宗教。“Nobody actually knows how this religion started, but the tradition, at least in some places, is to build the sauna first, then the house.” (芬兰谚语：先建桑拿，再搭房屋)

芬兰的教育系统让人印象深刻，有一种英特纳雄耐尔已经实现的即时感。
教育国策一：教育免费。不仅免学费、而且提供全额伙食补助。不仅保障城里人就近入学，还为偏远地区学生提供免费交通运输系统。
教育国策二：学术教育与职业教育平衡发展。高中就有学术性的文理高中和职业高中，高等教育分成“研究性大学”(university) 以及科技大学(芬兰语ammattikorkeakoulu)系统。
500万人口的小国居然有17间大学以及27间科技大学。
教育国策二：教育平等。天朝惯有的排名制、淘汰法，是在国家法律和社会信仰层面所不能容忍的。教育系统不使用淘汰，分组或是放弃任何一位学生。

>Finnish schools don't separate out the good students-or the losers.(p25)

对于林纳斯这类一度具有阅读障碍的“Math Guy”，在某些方面（体育、社交等）非常自卑的人来说，并不妨碍他们过上好日子。芬兰教育系统有着非常丰富的奖学金体系，例如林纳斯的第一部电脑，就是通过高中时代的奖学金购买的（估值500欧元，5000元人民，算上那个年代的购买力，少说上万）。那可是1980年代，计算机才刚刚个人化，是非常昂贵的设备。就算30多年后今天，中国任何一所普通高中每学期的单科奖学金到万元标准的也不多吧。

>The biggest ones were on the order of $500. So that's where most of the money for my second computer came from.

据说今年在克强CEO的严重关切和亲自督战下，财政部和教育部把中国高校博士生的津贴从每生每年12000元大幅提高到15000元，即每生每月提高250元。还真不如一个芬兰的高中生。
所以，关键问题都要看数字。科教兴国是不是扯淡，领导是不是真的重视你，只要看账上那点饷银就清楚了。

## meaning for life

>There are three things that have meaning for life.They are the motivational factors for everything in your life——for anything that you do or any living thing does:The first is survival, the second is social order, and the third is entertainment. Everything in life progresses in that order.And there is nothing after entertainment. So, in a sense, the implication is that the meaning of life is to reach that third stage. And once you've reached the third stage, you're done. But you have to go through the other stages first.

"人类的追求分成三个阶段。第一是生存，第二是社会秩序，第三是娱乐。最明显的例子是性，它开始只是一种延续生命的手段，后来变成了一种社会行为，比如你要结婚才能得到性。再后来，它成了一种娱乐。" （是不是有点离经叛道? 我竟无力反驳)

>It started out as survival, but it became a social thing.That's why you get married.And then it becomes entertainment.

"技术最初也是为了生存，为了生存得更好。现在技术大体上还处于社会的层面，但正在朝娱乐的阶段发展。......（Linux的开发模式）为人们提供了依靠兴趣与热情而生活的机会。与世界上最好的程序员一起工作，是一种无与伦比的享受。"

>Technology came about as survival.And survival is not about just surviving, it's about surviving better.

人生在世，Just for fun.

What can I do to make society better?
You known that you're a part of society.You known that society is moving in this direction.
You can help society move in this direction.

## 参考文献

1. 《Just for Fun》
副题：The Story of an Accidental Revolutionary
作者：Linus Torvalds 、 David Diamond
售价：USA $14.99/CAN $18.50
Paperback: 288 pages
Publisher: HarperBusiness; Reprint edition (June 4, 2002)
Language: English
ISBN-10: 0066620732
ISBN-13: 978-0066620732
出版年份：2001年
阅读进度：201704～201705

Linus Torvalds was born in Finland and graduated from the University of Helsinki.
He lives in San Jose, California.

David Diamond has written for the New York Times,Wired,USA Weekend, and many other publications.
He lives in Kentfield, California.

2. [关于Torvalds及《Just For Fun》的批评意见](http://www.softpanorama.org/People/Torvalds/summing_up.shtml#Vanity%20Fair%20Autobiography)
I think that Linus Torvalds succeed first a foremost as an author of a "new BIOS", a POSIX-compatible kernel implementation which became a de-facto standard
"Linux is moving away from its founding ideals and not even Linus Torvalds can change it".

3. [阮一峰：《Linus Torvalds自传》摘录,20120903](http://www.ruanyifeng.com/blog/2012/09/linus_torvalds.html)

4. [维基百科：芬兰教育](https://zh.wikipedia.org/wiki/芬蘭教育)

更多精彩内容，请关注订阅：[@睿哥杂货铺](https://riboseyim.github.io)
