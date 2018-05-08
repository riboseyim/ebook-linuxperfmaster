# 基础设施部署和配置管理

- Ansible vs. Chef vs. Fabric vs. Puppet vs. SaltStack

![](http://og2061b3n.bkt.clouddn.com/Deploying-Management-Tools.png)

在生产环境中工作，常常意味着连续部署和遍布全球的基础设施。如果您的基础架构是分散式和基于云的，同时您需要在大量服务器上频繁部署大量类似的服务，如果此时有一种方法可以自动配置和维护以上所有内容将是您的一大福音。

部署管理工具（Deployment management tools）和配置管理工具(configuration management tools)是为此目的而设计的。它们使您能够使用“食谱”（recipes），“剧本” (playbooks)，模板(templates)或任何术语来简化整个环境中的自动化和编排，以提供标准、一致的部署。

在选择工具时请记住几点注意事项。首先是了解工具的模型。有些工具采用主控模式（master-client model），它有一个集中控制点（master）与分布式部署的服务器进行通信，其他部分则可以在更本地的层面上运行。另一个考虑因素是你的环境构成。有些工具是采用不同的语言编写的，对于特定的操作系统或设置可能会有所不同。确保您选择的工具与您的环境完美配合，充分利用团队的特定技能可以为您节省很多麻烦。

#### 1. Ansible
![](http://og2061b3n.bkt.clouddn.com/Ansible-Logo.jpg)
￼Ansible是一种开源工具，用于以可重复的方式将应用程序部署到远程节点和配置服务器。它为您提供了基于推送模型（push model ）推送多层应用程序和应用程序组件的通用框架，您也可以根据需要将其设置为 master-client 模式。 Ansible 建立在可用于各种系统上部署应用程序的剧本(playbook)。

**何时使用它** ：对您来说最重要的是快速，轻松地启动和运行，并且您不想在远程节点或受管服务器上安装代理（Agent）。如果您的需求重点更多地放在系统管理员身上，专注于精简和快速，请考虑 Ansible 。

价格：免费的开源版本，Ansible Tower 的付费套餐每年 5000 美元（最多可容纳100个节点）。

**赞成的理由：**
- 基于 SSH , 不需要在远程节点安装任何代理
- 学习曲线平缓、使用 YAML
- Playbook 结构简单，结构清晰
- 具有变量注册功能，可以使前一个任务作为后一个任务的变量
- 代码库精简

**反对的理由：**
- 相较其他编程语言的工具功能有限。
- 通过 DSL 实现其逻辑，这意味着需要经常查看文档直到您学会为止
- 即使是最基本功能也需要变量注册，这可能使简单任务变得复杂
- 内省（Introspection）很差。例如很难在剧本中看到变量的值
- 输入，输出和配置文件格式之间缺乏一致性
- 性能存在一定瓶颈

![](http://og2061b3n.bkt.clouddn.com/Ansible-Tower-Dashboard.png)

#### 2. Chef
![](http://og2061b3n.bkt.clouddn.com/Chef-Logo.jpg)
￼Chef 是一个配置管理开源工具，用户群专注面向开发者。Chef 作为 master-client  模式运行，需要一个单独的工作站来控制 master 。Chef 基于 Ruby 开发，纯 Ruby 可以支持大多数元素。Chef 的设计是透明的，并遵循给定的指示，这意味着你必须确保你的指示是清楚的。

何时使用它：在考虑 Chef 之前，需要确保你熟悉 Git ，因为它需要配置 Git ，你必须编写 Ruby 代码。Chef 适合以开发为中心（development-focused ）的团队和环境。对于寻求更成熟异构环境解决方案的企业来说，这是一件好事。

价格：提供免费的开源版本，标准版和高级版计划以每年节点为单位定价。 Chef Automate 的价格为每个节点 137 美元，或者采用 Hosted Chef 每个节点每年节省72 美元。

**赞成的理由：**
- 丰富的模块和配置配方(recipes)
- 代码驱动的方法为您提供更多的配置控制和灵活性
- 以 Git 为中心赋予 Chef 强大的版本控制功能
- 'Knife'工具（使用 SSH 从工作站部署代理）减轻了安装负担

**反对的理由：**
- 如果您还不熟悉 Ruby 和面向过程编程，学习曲线会非常陡峭
- 这不是一个简单的工具，可能需要维护大量的代码库和复杂的环境
- 不支持推送功能

![](http://og2061b3n.bkt.clouddn.com/Chef-Compliance-Node.png)

#### 3. Fabric
![](http://og2061b3n.bkt.clouddn.com/Fabric-Logo.jpg)
Fabric 是一个基于 Python 的应用程序部署工具。Fabric 的主要用途是在多个远程系统上运行任务，但它也可以通过插件的方式进行扩展，以提供更高级的功能。 Fabric 将配置您的操作系统，进行操作系统/服务器管理，自动化部署您的应用。
￼
何时使用它：如果您刚刚开始进入部署自动化领域，Fabric 是一个良好的开端。如果您的环境至少包含一点 Python，它都会有所帮助。

价格：免费

**赞成的理由：**
- 擅长部署以任何语言编写的应用程序。它不依赖于系统架构，而是依赖于操作系统和软件包管理器
- 相比其他工具更简单，更易于部署
- 与 SSH 进行了广泛的整合，以实现基于脚本的流水线

**反对的理由：**
- Fabric 是单点设置（通常是运行部署的机器）
- 使用 PUSH 模型，因此不如其他工具那样适合流水线部署模型
- 虽然它是用于在大多数语言中部署应用程序的绝佳工具，但它确实需要运行Python，所以您的环境中必须至少有一个适用于 Fabric 的 Python 环境

![](http://og2061b3n.bkt.clouddn.com/Fabric-Dashboard-1024x823.png)

#### 4. Puppet
![](http://og2061b3n.bkt.clouddn.com/Puppet-Logo.png)
￼Puppet 长期依赖是全面配置管理领域的标准工具之一。Puppet 是一个开源工具，但是考虑到它已经存在了多长时间，它已经在一些最大和最苛刻的环境中进行了部署和验证。 Puppet 基于 Ruby 开发，但使用更接近 JSON 的领域专用语言（Domain Specific Language，DSL）。Puppet 采用master-client 模式运行，并采用模型驱动(model-driven)的方法。 Puppet 将工作设计为一系列依赖关系列表，根据您的设置，这可以使事情变得更容易或更容易混淆。

**何时使用它：** 如果稳定性和成熟度对您来说是最关键的因素，Puppet 是一个不错的选择。对于具有异构环境的大型企业和涉及多种技能范围的 DevOps 团队而言而言，这是一件好事。

价格：Puppet 分为免费的开源版本和付费的企业版本，商业版每年每个节点 120 美元（提供批量折扣）。

**赞成的理由：**
- 通过 Puppet Labs 建立了完善的支持社区
- 具有最成熟的接口，几乎可以在所有操作系统上运行
- 安装和初始设置简单
- 最完整的 Web UI
- 强大的报表功能

**反对的理由：**
- 对于更高级的任务，您需要使用基于 Ruby 的 CLI（这意味着您必须了解Ruby）
- 纯 Ruby 版本的支持正在缩减（而不是那些使用 Puppet 定制 DSL 的版本）
- Puppet 代码库可能会变得庞大，新人需要更多的帮助
- 与代码驱动方法相比，模型驱动方法意味着用户的控制更少

![](http://og2061b3n.bkt.clouddn.com/Puppet-Dashboard.png)

#### 5. Saltstack

![](http://og2061b3n.bkt.clouddn.com/SaltStack-Logo.jpg)

￼SaltStack（或 Salt）是一种基于 CLI 的工具，可以将其设置为 master-client 模型或非集中模型。 Salt 基于Python 开发，提供了 PUSH 和 SSH 两种方法与客户端通讯。 Salt 允许对客户端和配置模板进行分组，以简化对环境的控制。
**何时使用它：** 如果可扩展性和弹性是一个大问题，则 Salt 是一个不错的选择。对系统管理员来说，Salt 提供的可用性非常重要。

价格：提供免费的开源版本，以及基于年度/节点订阅的 SaltStack Enterprise 版本。具体的价格没有在他们的网站上列出，据说每个节点每年的起步价为 150 美元。

**赞成的理由：**
- 一旦你渡过了入门阶段，就可以简单地组织和使用
- DSL 功能丰富，不需要逻辑和状态
- 输入，输出和配置非常一致，全部所有 YAML （一个可读性高，用来表达数据序列的格式）
- 内省(Introspection)非常好。很容易看到 Salt 内部发生了什么
- 强大的社区
- 很高的可扩展性和灵活性

**反对的理由：**
- 对于新用户来说，非常难以配置，学习曲线陡峭
- 在入门级别而言，文档很难理解
- Web UI  比同领域的其他工具更新、更轻量
- 对非 Linux 操作系统没有很好的支持

![Salt Subsystems](https://docs.saltstack.com/en/getstarted/images/salt-subsystems.png)

![](http://og2061b3n.bkt.clouddn.com/SlatStack-Subsystem-Job.png)

#### Ansible vs. Chef vs. Fabric vs. Puppet vs. SaltStack
您使用的配置管理或部署自动化工具取决于您的环境需求和偏好。 Chef 和 Puppet 是一些较老的、更成熟的选项，它们适用于那些重视成熟性和稳定性而非简单性的大型企业和环境。 Ansible 和 SaltStack 是寻求快速和简约解决方案人士的理想选择，同时在不需要支持某些特殊功能或具有大量操作系统的环境中工作。Fabric 对于小型环境和那些正在寻求更低门槛和入门级解决方案的人来说是一个很好的工具。
