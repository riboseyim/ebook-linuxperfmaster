# PostgreSQL 的时代到来了吗 ？

PostgreSQL是对象-关系型数据库，BSD 许可证。拼读为"post-gress-Q-L"。

## PostgreSQL 的时代到来了吗 ?

- 作者： Tony Baer
- 原文： [Has the time finally come for PostgreSQL?](https://www.zdnet.com/article/has-the-time-finally-come-for-postgresql/)（有删节）

近30年来 PostgreSQL 无疑是您从未听说过的、最常见的开源 SQL 数据库。PostgreSQL 经常身居幕后：从 EnterpriseDB 到 Amazon Redshift 、Greenplum、Netezza 及其他许多商业数据库产品。

最近在许多商业产品的推动下，PostgreSQL 逐渐走向前台。大约十年前 EnterpriseDB 打开了潘多拉的盒子 —— 作为商业支持平台提供 Oracle 的替代品。特别是最近一段时间，云服务商提供了一系列托管产品：从 Amazon Wed Services 开始, 支持 PostgreSQL 作为其托管关系数据库服务 (Relational Database Service，RDS) 之一。

过去一年 AWS 和它的竞争对手将 PostgreSQL 的市场前景提升了一个等级。AWS 推出了兼容 PostgreSQL 的原生云数据库平台 Amazon Aurora ，作为回应 Microsoft 和 Google 推出了 Azure Database for PostgreSQL 和 Cloud SQL for PostgreSQL 解决方案。

Mark Porter（马克·波特，Amazon Aurora PostgreSQL  和 Amazon RDS for PostgreSQL 主管） 不得不通过一些花哨的方法表达 AWS 对开源社区的支持，例如修复 Bug、提供免费测试帐户和其他形式的财政支助。PostgreSQL 在 AWS 上的实现不是开源的, 因为它是为 AWS 自身的云基础结构而设计。

PostgreSQL 虽然是聚焦于事务型数据库的开源项目, 但是许多基于它的商业产品都是大规模并行处理数据仓库（Massively Parallel Processing，MPP）。出于这个原因,  Greenplums，Netezzas 和Redshifts 创建了自己的开源 forks 项目, 甚至添加像 columnar tables 这样的基本功能。

PostgreSQL 的一个常见主题是支持企业级负载的开源关系数据库。关于这一点，竞争者包括 MySQL 和 MariaDB , 但仍然存在差异, PostgreSQL 支持更复杂的 SQL 函数和数据类型, 包括数组（arrays）, 连接（joins）和视图（Window Functions）等等。

另一个原因是出现了“replace Oracle”的口号, PL/pgSQL 的设计非常类似 Oracle PL/SQL。这正是EnterpriseDB 多年以来一直提倡的，同时获得了 美国金融业监管局(Financial Industry Regulatory Authority，FINRA) 的支持。FINRA 将大约 650 个 Oracle 实例迁移入云（ Amazon RDS for PostgreSQL），作为一个更大战略的一部分, 将其整个部署在 IT 基础设施上的业务迁移到 AWS。根据 FINRA 首席开发者 Steve Downs 的说法，对于  Oracle DBA 而言，在 PostgreSQL 中使用诸如对象/关系映射（object/relational mappings）、存储过程（stored procedures）以及利用视图（view）支持复杂查询的功能，给人一种似曾相识的感觉。

然而, 作为两种不同的数据（包括 SQL 实现）PostgreSQL 和 Oracle 之间毕竟存在显著差异。例如数据库如何处理数字和可变字符字段、同义词、复制 (replication，PostgreSQL 不像 Oracle 那样成熟 ) 以及实例化视图刷新等等。

PostgreSQL 有它独特的优势，即作为第三方寻求自主数据库产品的开源平台。重要的是, 去年秋季发布的最新 10.0 版本（2017年11月09日）, 解决了在 Oracle 或 SQL Server 产品中被视为理所当然的功能，包括声明式表分区（declarative table partitioning）、改进后的复制功能（replication）, 发布/订阅（publish/subscribe）、仲裁提交（quorum commits，对于云部署可能非常有用)。

总之，PostgreSQL 还有很多需要追赶的领域,  Oracle 或 SQL Server 用户也仍然有理由继续使用他们的平台。大部分的差异将体现在数据库的实施, 而不是一些具体的功能特性。这种差异将主要体现在数据库弹性、自动化、基础架构选型以及云计算的规模等方面。有了 AWS、Azure 和 Google Cloud 的加持（非常值得注意的一个信号），若干年后 PostgreSQL 可能最终走出阴影。

## PostgreSQL 简史

PostgreSQL 开始于 UC Berkeley 的 Ingres 计划，经历了长时间的演变。

Ingres 计划的领导者 Michael Stonebraker（迈克尔·斯通布雷克，2015 年图灵奖得主，目前是麻省理工学院兼职教授）在 1982 年离开 Berkeley 进入商业公司 Ingres，1985年又返回 Berkeley 开始新项目 Postgres 。Postgres 项目组从1986年开始发表了一些描述系统基本原理的论文并发行了版本1、2、3 、4，到1993年就有大量的用户存在了。尽管 Postgres计划正式的终止了，BSD许可证却使开发者可以获得副本并进一步开发系统。1994年，两个 UC Berkeley 的研究生 Andrew Yu 和 Jolly Chen 增加了一个SQL语言解释器来替代早先的基于 Ingres 的 QUEL 系统，创建了 Postgres95。

1996年重命名为：PostgreSQL。（版本 6.0 ）

2000年，前 Red Hat 投资者筹组了一间名为Great Bridge的公司来商业化PostgreSQL，以和其他商用数据库厂商竞争。2001年末，Great Bridge 因市场原因终止营运。2001年，Command Prompt, Inc. 发布了最老牌的PostgreSQL 商业软件 Mammoth PostgreSQL，并提供开发者赞助和贡献 PL/Perl、PL/php、维护 PostgreSQL Build Farm 等。

2005年1月 ，Pervasive Software 宣布参与社区及商业支持，直到 2006 年 7 月退出。2005年1月19日，版本 8.0 发行。自 8.0 后，PostgreSQL以原生（Native）的方式，运行于Windows系统。2006年6月，Solaris 10 包含 PostgreSQL一起发布。

2012年09月10日，PostgreSQL 发布 9.2 版本，主要在性能方面的提升，也包括一些新的 SQL 特性。
2016年10月27日，PostgreSQL 发布 9.6.1 版本。
2017年11月09日，PostgreSQL 发布 10.1 版本。

## ABC

```bash
# install
$ brew install postgresql
# version
$ pg_ctl -V
pg_ctl (PostgreSQL) 10.1
# initdb -- 创建一个新的PostgreSQL数据库簇（cluster）,单个服务端实例管理的多个数据库的集合。
# 创建数据库数据的宿主目录，生成共享的系统表 （不属于任何特定数据库的表）和创建template1 和postgres数据库
$ initdb /Users/yanrui/Data/TestPG
# start
$ pg_ctl -D /Users/yanrui/Data/TestPG start
waiting for server to start....2018-01-03 14:13:17.005 CST [37621] LOG:  listening on IPv4 address "127.0.0.1", port 5432
2018-01-03 14:13:17.005 CST [37621] LOG:  listening on IPv6 address "::1", port 5432
2018-01-03 14:13:17.006 CST [37621] LOG:  listening on Unix socket "/tmp/.s.PGSQL.5432"
2018-01-03 14:13:17.048 CST [37623] LOG:  database system was shut down at 2018-01-03 14:11:30 CST
2018-01-03 14:13:17.066 CST [37621] LOG:  database system is ready to accept connections
 done
server started
# port listen
bash-3.2$ netstat -an | grep LISTEN     
tcp6       0      0  ::1.5432               *.*                    LISTEN
# createdb
bash-3.2$ createdb -O[owner] test_db
# default [当前登录系统用户名]
bash-3.2$ psql
2018-01-03 18:14:37.537 CST [45864] FATAL:  database "yanrui" does not exist
psql: FATAL:  database "yanrui" does not exist
You have new mail in /var/mail/yanrui
# login in
bash-3.2$ psql test_db
psql (10.1)
Type "help" for help.

# log out
test_db=# \q  (Ctrl+D)

# 卸载
$ brew uninstall postgres
# 开机
$ launchctl unload -w ~/Library/LaunchAgents/homebrew.mxcl.postgresql.plist
$ rm -rf ~/Library/LaunchAgents/homebrew.mxcl.postgresql.plist
```

## Architecture OverView

![](http://omb2onfvy.bkt.clouddn.com/DB_PostgreSQL_Overview.png)

## 扩展阅读
- [Data Model Generation for PostgreSQL](http://packagemain.blogspot.it/2016/05/data-model-generation-for-postgresql.html)
- [How FINRA is Migrating to Postgres](https://postgresconf.org/conferences/2018/program/proposals/finra-placeholder)

## 参考文献
- [PostgreSQL新手入门](http://www.ruanyifeng.com/blog/2013/12/getting_started_with_postgresql.html)
- [Postgres full-text search is Good Enough! | JULY 13,2015](http://rachbelaid.com/postgres-full-text-search-is-good-enough/)
- [Showdown: MySQL 8 vs PostgreSQL 10](https://hackernoon.com/showdown-mysql-8-vs-postgresql-10-3fe23be5c19e)
- [兼容 PostgreSQL 的 Amazon Aurora 已在 AWS GovCloud（美国）区域推出 | Jun 14,2018](https://aws.amazon.com/cn/about-aws/whats-new/2018/06/amazon-aurora-postgresql-compatibility-in-aws-govcloud-us/)
- [Amazon Aurora 产品信息](https://aws.amazon.com/cn/rds/aurora/)
