# 监控数据可视化：开源地理信息系统简史

## 摘要
- 开源 GIS 技术简史
- 1.1、GIS 的起源: MOSS and GRASS
- 1.2、GIS 的发展：GeoTools, GDAL, PostGIS 和 GeoServer
- 1.3、创新和教育：开源项目驱动
- 1.4、开源 GIS 的商业支持
- Demo:A Full Stack Geo-enabled Internet of Things (IoT) Solution

## 一、开源 GIS 技术简史：从渺小到改变世界

- [原文: A History of Open Source GIS, from Humble Beginnings to World-Changing Applications | 23 Jun 2017 9:00am, by Anthony Calamito](https://thenewstack.io/humble-beginnings-world-changing-applications-history-open-source-gis/)

![](http://riboseyim-qiniu.riboseyim.com/GIS_History_1.jpg)

数字制图和地理空间信息系统（Geographic Information System,GIS）的出现彻底改变了人们和对周围世界思考、互动的方式。将位置信息分层重叠用于决策的概念首先是由 Ian McHarg（景观设计师）在上世纪60年代提出。大约在同一时间，Roger Tomlinson —— 人们普遍称之为“GIS 之父”（Father of GIS） 完成了他的博士论文，主要研究使用计算方法处理分层的地理空间信息。罗杰随后致力于创建第一个计算机化的地理信息系统——加拿大地理信息系统（the Canada Geographic Information System），主要用于勘探测绘。

开源 GIS 的起源可以追溯到 1978 年的美国内政部（U.S. Department of the Interior）。从那时起，开源 GIS 基于不同的知识产权许可证，深入影响到许多行业的发展，包括政府和商业领域。美国劳工部称 GIS 技术为二十一世纪最重要的三大高增长产业之一。开源 GIS 技术在过去四十年的发展，直到今天演变出许多具有开创性和影响力的应用。

#### 1.1、GIS 的起源: MOSS and GRASS
1978年，美国内政部创建了 MOSS 系统（the Map Overlay and Statistical System ，地图叠加和统计系统）。MOSS 系统主要用于跟踪和评估矿山开发对环境、野生植物、野生动物及其迁徙方式的影响。这是第一个广泛部署，基于矢量（Vector Based）、可互动的地理信息系统。第一套 GIS 生产部署在小型机上。

随后不久，GRASS (“草” ，Geographic Resources Analysis Support System，地理资源分析支持系统）诞生。GRASS 系统拥有 350 多个模块用于处理栅格、拓扑向量、图像和图形数据，该软件最初设计提供给美国军方使用，以协助土地管理和环境规划。GRASS 系统广泛应用于科学研究和商业领域，包括地理空间数据管理和分析、图像处理、空间和时间建模以及创建图形和地图。

![](http://riboseyim-qiniu.riboseyim.com/GIS_History_2.png)

#### 1.2、GIS 的发展：GeoTools, GDAL, PostGIS 和 GeoServer
1996，利兹大学（the University of Leeds）在一个项目上开始创建基于 Java 开发语言的地理信息库，设计可以被纳入不同的应用需要。最终的成果是 GeoTools，一个可以操纵空间数据的开源库，在今天广泛应用于Web地理空间信息服务，网络地图服务和桌面应用程序。
![](http://riboseyim-qiniu.riboseyim.com/GIS_History_3.gif)

四年后，一个跨平台的地理信息库 GDAL (Geospatial Data Abstraction Library, 地理空间数据抽象库) 出现了 。GDAL 使得 GIS 应用程序可以支持不同的数据格式，它还附带了各种有用的命令行工具，用于处理和转换各种数据格式。GDAL 支持超过 50 个栅格格式和20 个矢量格式的数据，它是全世界使用最广泛的地理空间数据访问库，支持的应用程序包括谷歌地球（Google Earth），GRASS，QGIS、FME（the Feature Manipulation Engine）和ArcGIS。

![](http://riboseyim-qiniu.riboseyim.com/GIS_History_4.png)

2001年，[Refractions Research(加拿大 IT 咨询机构，创建于1998年)](http://www.refractions.net)， 研发了开源项目 PostGIS ，使得空间数据可以存储在 Postgres 数据库。同年，GeoServer 创建，一个基于 Java 语言开发的应用程序，用于将空间数据发布为标准的Web服务。PostGIS 和 GeoServer 项目都取得了令人难以置信的成功，今天广泛应用于开源 GIS 数据库和 GIS 服务器。

#### 1.3、创新和教育：开源项目驱动

QGIS 被认为是在开源桌面 GIS 的鼻祖。QGIS 在2002发布，它集成了GRASS 系统的分析功能，以及 GDAL 对于数据格式支持，提供一个用户友好的桌面应用程序进行数据编辑、地图制图与分析。QGIS 可以和其他开源 GIS 互相操作，例如；管理 PostGIS 数据库，将数据发布到 GeoServer 作为 Web 服务。

在21世纪初，开源GIS 继续获得发展动力， 创建的开源孵化项目是 OSGeo 和 LocationTech。OSGeo 在 2006 年被推出，设计目标是支持开源 GIS 软件的协同开发，以及促进相关软件的广泛应用。LocationTech 是在 Eclipse 基金会(the Eclipse Foundation ) 中设立的一个工作组，旨在促进 GIS 技术在学术研究者，产业和社区之间的合作。

2011 年，“Geo for All” 创建。他是是[开源地理空间基金会（Open Source Geospatial Foundation）](http://www.osgeo.org/)的教育推广项目，目的是使人人都能接触到地理空间技术教育的机会。作为该基金会的工作成果，许多开源 GIS 的教育资源能在互联网上免费提供，包括 FOSS4G Academy 和 GeoAcademy。最后，“Geo for All” 致力于在世界各地建立了开源地理空间实验室和研究中心，以支持开源的地理空间技术开发、培训和研究。

#### 1.4、开源 GIS 的商业支持

在2013年，我工作的 [Boundless 公司](https://boundlessgeo.com)，成为第一家提供世界上最流行开源 GIS 应用软件的公司，包括数据库、服务器、桌面、网络、移动和云级别的商业支持和维护。Boundless 的产品套件确保了大型组织利用开源 GIS 技术的时候获得成功所需要的充分技术支持。该公司为最流行的开源 GIS 软件提供持续升级和补丁更新。

#### 1.5、The Future and Beyond
目前，现代计算的挑战要求将软件部署在云平台工作，并支持创建大量数据所带来的需求。两种开源 GIS 软件解决方案可以满足这些挑战，包括 [GeoMesa，一个开源的分布式时空数据库](http://www.geomesa.org/)，[GeoTrellis，一个支持高性能应用的地理数据处理引擎](https://geotrellis.io/)。

这两种解决方案在2014中引入，可以处理云中的地理空间大数据。由于它们是建立在开源框架之上的，用户不需要商业软件许可证就可以使用而不必担心受到惩罚，而且可以按照用户需要进行弹性扩展。

开源 GIS 拥有美好的前景和巨大的潜力，它使得增强协作、共享有价值的数据和访问关键资源成为可能。凭借其众多的环境、政府、公共安全和健康应用，开源GIS 技术及其应用项目具有改变世界的潜力。

![](http://riboseyim-qiniu.riboseyim.com/GIS-History-5.png)

#### Demo:A Full Stack Geo-enabled Internet of Things (IoT) Solution
- Github:https://github.com/amollenkopf/dcos-iot-demo
- using Mesosphere's open sourced Data Center Operating System (DC/OS)
- using Docker containerization and frameworks for Mesos including Marathon, Kafka, Spark, and Elasticsearch.
![](http://riboseyim-qiniu.riboseyim.com/GIS_History_Demo_Mesosphere_DCOS_Architecture.jpg)
![](http://riboseyim-qiniu.riboseyim.com/GIS_History_Demo_Mesosphere.png)

## 二、GIS Technology Applications  

```
ogr2ogr -f GeoJSON -where "NAME_1='CHINA'"       CHM_geo.json         CHN_adm1.shp   
ogr2ogr -f GeoJSON -where "NAME_1='Guangdong'"   Guangdong2_geo.json  CHN_adm2.shp
```

- [十行代码看到空气质量指数(leafletCN/geojsonMap)](https://mp.weixin.qq.com/s?__biz=MjM5NDQ3NTkwMA==&mid=2650141909&idx=1&sn=71c1bd26d54df9fe1737c0e80a954a55&chksm=be866ec689f1e7d08c420cdfe746562168167b444df320319d443174aa3514b368810cc87a8e&mpshare=1&scene=1&srcid=0224I7JQUudvP0PPn5XPamTt#rd)

## 扩展阅读
- [美国内政部 | U.S. Department of the Interior](http://www.doi.gov/)
美国内政部（United States Department of the Interior，缩写：DOI）与大多数国家的内政部负责警察或安全事务不同，负责管理美国联邦政府拥有的土地、开采和保护美国的自然资源，并负责有关阿拉斯加、夏威夷原住民和美国岛屿地区领土事务。下辖美国地质调查局、国家公园管理局、土地管理局等机构。

- [美国地质调查局 | U.S. Geological Survey](https://www.usgs.gov/)
美国地质调查局（United States Geological Survey，缩写：USGS）是美国内政部辖下的科学机构，是内政部唯一一个纯粹的科学部门，有约一万名人员，总部设在弗吉尼亚州雷斯敦。美国地质调查局的科学家主要研究美国的地形、自然资源和自然灾害与其的应付方法；负责四大科学范畴：生物学、地理学、地质学和水文学。
- **美国地质调查局是美国的主要公共地图制作机构**，制作线上的美国地图，又与商业公司合作向公众销售地图；
- 负责全球地震监测（美国地震资讯中心，科罗拉多），负责管理地震监测系统、地磁监测系统（公布磁场和实时磁力表）；
- 参与地球、月球和行星的探测和地图制作（从1962年开始）；
- 负责国家野生动植物健康中心：野生动物、植物和生态系统；在国内运行17个生物研究中心；
- 负责国家火山早期预警中心（2005年）：改善美国国内的169座火山的监测，研究新的监测方法。

- [Refractions Research(加拿大 IT 咨询机构，创建于1998年)](http://www.refractions.net)
- [开源地理空间基金会（Open Source Geospatial Foundation）](http://www.osgeo.org/)
- [OSGeo 中国中心 | 在线地图资源库 ](http://www.osgeo.cn/map)
- [GeoMesa，一个开源的分布式时空数据库](http://www.geomesa.org/)
- [GeoTrellis，一个支持高性能应用的地理数据处理引擎](https://geotrellis.io/)

## 扩展阅读：数据可视化
- [数据可视化（一）思维利器 OmniGraffle 绘图指南 ](https://riboseyim.github.io/2017/09/15/Visualization-OmniGraffle/)
- [数据可视化（二）跑步应用Nike+ Running 和 Garmin Mobile 评测](https://riboseyim.github.io/2016/04/26/BestAppMap/)
- [数据可视化（三）基于 Graphviz 实现程序化绘图](https://riboseyim.github.io/2017/09/15/Visualization-Graphviz/)
- [数据可视化（四）开源地理信息技术简史（Geographic Information System](https://riboseyim.github.io/2017/05/12/Visualization-GIS/)
- Preview:[数据可视化（五）可视化数据图表制作方法](https://riboseyim.github.io/2017/05/12/Visualization-Charts/)
- [数据可视化（六）常见的数据可视化仪表盘(DashBoard)](https://riboseyim.github.io/2017/11/23/Visualization-DashBoard/)
- [数据可视化（七）Graphite 体系结构详解](https://riboseyim.github.io/2017/12/04/Visualization-Graphite/)

## 参考文献
- [A History of Open Source GIS, from Humble Beginnings to World-Changing Applications | 23 Jun 2017 9:00am, by Anthony Calamito](https://thenewstack.io/humble-beginnings-world-changing-applications-history-open-source-gis/)
- [Mesosphere DC/OS Brings Large-Scale Real-Time Processing to Geospatial Data | 29 Sep 2017 6:00am, by Scott M. Fulton III](https://thenewstack.io/architectural-requirements-customers-require-processing-millions-events-per-second/?utm_content=buffere9607&utm_medium=social&utm_source=twitter.com&utm_campaign=buffer)
