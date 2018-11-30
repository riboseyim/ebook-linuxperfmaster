# 微服务架构与分布式跟踪系统

## 摘要
- Distributed Tracing and Monitoring System
- OpenCensus: A framework for distributed tracing

![](http://riboseyim-qiniu.riboseyim.com/DTM-OpenCensus-Theme.png)

## 背景

随着互联网技术的高速发展，以往单应用的服务架构已经很难处理如山洪般增长的信息数据，随着云计算技术的大规模应用，以微服务、RESTful 为代表的各种软件架构广泛应用，跨团队、跨编程语言的大规模分布式系统也越来越多。相对而言，现在要理解系统行为，追踪诊断性能问题会复杂得多。

在单应用环境下，业务都在同一个服务器上，如果出现错误和异常只需要盯住一个点，就可以快速定位和处理问题；但是在微服务的架构下，功能模块天然是分布式部署运行的，前后台的业务流会经过很多个微服务的处理和传递，就连日志监控都会成为一个大问题（日志分散在多个服务器、无状态服务下如何查看业务流的处理顺序等），更不要说服务之间还有复杂的交互关系。

用户的一个请求在系统中会经过多个子系统（或者多个微服务）的处理，而且是发生在不同机器甚至是不同集群，当发生异常时需要快速发现问题，并准确定位到是哪个环节出了问题。对系统行为进行跟踪必须持续进行，因为异常的发生是无法预料的，有些甚至难以重现。跟踪需要无所不在，否则可能会遗漏某些重要的故障点。

为了解决上述问题，分布式跟踪系统 —— 一种帮助理解分布式系统行为、帮助分析性能问题的工具应运而生。

![](http://riboseyim-qiniu.riboseyim.com/DTM-OpenCensus-Micro-1.png)

![](http://riboseyim-qiniu.riboseyim.com/DTM-OpenCensus-Micro-2.png)


## Distributed Tracing and Monitoring System

讨论分布式跟踪，就一定会谈到 Dapper —— Google 公司研发并应用于自己生产环境的一款跟踪系统（设计之初参考了一些 Magpie 和 X-Trace 的理念 ）。Dapper 不仅为业内提供了非常有参考价值的实现，同步发表论文的也成为了当前分布式跟踪系统的重要理论基础。

- [《Dapper, a Large-Scale Distributed Systems Tracing Infrastructure|Google Technical Report dapper-2010-1, April 2010》](https://static.googleusercontent.com/media/research.google.com/zh-CN//archive/papers/dapper-2010-1.pdf)

>Modern Internet services are often implemented as complex, large-scale distributed systems.These applications are constructed from collections of software modules that may be developed by different teams, perhaps in different programming languages, and could span many thousands of machines across multiple physical facilities. Tools that aid in understanding system behavior and reasoning about performance issues are invaluable in such an environment.

在这篇论文中，Google 提出了关于分布式跟踪系统的一些重要概念：

- Annotation-based，基于标注或植入点、埋点
在应用程序或中间件中明确定义全局标注（Annotation），一个特殊的ID，通过这个 ID 连接每一条请求记录。当然，这需要代码植入，在生产环境中可以通过一个通用组件开放给开发人员。

- 跟踪树和span
在 Dapper 跟踪树（Trace tree）中，基本单元是树节点（分配 spanid）。节点之间通过连线表示父子关系，通过 parentId 和 spanId 把所有的关系串联起来，实现记录业务流的作用。

![](http://riboseyim-qiniu.riboseyim.com/DTM-Dapper-TraceTree-Span.png)

Google Dapper 的理念影响了一批分布式跟踪系统的发展，例如 2012 年，Twitter 公司严格按照 Dapper 论文的要求实现了 Zipkin （Scala 编写，集成到 Twitter公司自己的分布式服务 Finagle ）；Uber 公司基于 Google Dapper 和 Twitter Zipkin 的灵感，开发了开源分布式跟踪系统 Jaeger，例如 Jaeger 规范中同样定义了 Span（跨度, 跨径，两个界限间的距离）。

![](http://riboseyim-qiniu.riboseyim.com/DTM-Uber-Jaeger.png)

然而，Google Dapper 的定位更准确的说是分析系统，并不能解决从生产服务中提取数据的难题，OpenCensus 项目为此提供了解决方案。

## OpenCensus: A framework for distributed tracing

>OpenCensus is a framework for stats collection and distributed tracing.

![](http://riboseyim-qiniu.riboseyim.com/DTM-OpenCensus-Logo.png)

OpenCensus 项目是 Google 开源的一个用来收集和追踪应用指标的第三方库。OpenCensus 能够提供了一套统一的测量工具：跨服务捕获跟踪跨度（span）、应用级别指标以及来自其他应用的元数据（例如日志）。OpenCensus 有如下一些主要特点：
- 标准通信协议和一致的 API ：用于处理 metric 和 trace
- 多语言库，包括Java，C++，Go，.Net，Python，PHP，Node.js，Erlang 和 Ruby
- 与 RPC 框架的集成，可以提供开箱即用的追踪和指标。
- 集成的存储和分析工具
- 完全开源，支持第三方集成和输出的插件化
- 不需要额外的服务器或守护进程来支持 OpenCensus
- In process debugging：一个可选的代理程序，用于在目标主机上显示请求和指标数据

![](http://riboseyim-qiniu.riboseyim.com/DTM-OpenCensus-Language.png)

## OpenCensus Concepts

#### Tags | 标签
OpenCensus 允许系统在记录时将度量与维度相关联。记录的数据使我们能够从各种不同的角度分析测量结果，即使在高度互连和复杂的系统中也能够应付。

#### Stats | 统计
Stats 收集库和应用程序记录的测量结果，汇总、导出统计数据。

#### Trace | 跟踪
Trace 是嵌套 Span (跨度)的集合。Trace 包括单个用户请求的处理进度，直到用户请求得到响应。Trace 通常跨越分布式系统中的多个节点。跟踪由 TraceId 唯一标识， Trace 中的所有 Span 都具有相同的 TraceId 。

一个 Span 代表一个操作或一个工作单位。多个 Span 可以是“Trace”的一部分，它代表跨多个进程/节点的执行路径（通常是分布式的）。同一轨迹内的 Span 具有相同的 TraceId。

Span 共有属性：
- TraceId
- SpanId
- Start Time
- End Time
- Status

Span 可选属性：
- Parent SpanId
- Remote Parent
- Attributes
- Annotations
- Message Events
- Links

#### Exporter | 出口商
OpenCensus is vendor-agnostic and can upload data to any backend with various exporter implementations. Even though, OpenCensus provides support for many backends, users can also implement their own exporters for proprietary and unofficially supported backends.

OpenCensus 是独立于供应商的，可以通过各种 Exporter 实现将数据上传到任何后端。尽管OpenCensus 为一些后端服务提供了 API ，但用户也可以实现自己的 Exporter。

#### Introspection | 内省
OpenCensus 提供在线仪表板，显示进程中的诊断数据。这些页面被称为 z-pages ，它们有助于了解如何查看来自特定进程的数据，而不必依赖任何度量收集器或分布式跟踪后端。

![](http://riboseyim-qiniu.riboseyim.com/DTM-OpenCensus-traceZ.png)

## OpenCensus Examples

#### 创建指标

- 定义指标类型
- 定义显示方式

Track Metrics 一般需要考虑服务负载（Server Load）、响应时间（Response Time）、误码率(Error Rates)等。

实例：
- [opencensus-go-examples-helloworld](https://github.com/census-instrumentation/opencensus-go/blob/master/examples/http/helloworld_server/main.go)
- [opencensus-java-examples](https://github.com/census-instrumentation/opencensus-java)
- [“Hello, world!” for web servers in Go with OpenCensus](https://medium.com/@orijtech/hello-world-for-web-servers-in-go-with-opencensus-29955b3f02c6)

```go
import (
  "go.opencensus.io/stats"
  "go.opencensus.io/tag"
  "go.opencensus.io/stats/view"
)

var (
  requestCounter             *stats.Float64Measure
  requestlatency             *stats.Float64Measure
  codeKey                    tag.Key
  DefaultLatencyDistribution = view.DistributionAggregation{0, 1, 2, 3, 4, 5, 6, 8, 10, 13, 16, 20, 25, 30, 40, 50, 65, 80, 100, 130, 160, 200, 250, 300, 400, 500, 650, 800, 1000, 2000, 5000, 10000, 20000, 50000, 100000}
)
	codeKey, _ = tag.NewKey("banias/keys/code")
	requestCounter, _ = stats.Float64("banias/measures/request_count", "Count of HTTP requests processed", stats.UnitNone)
	requestlatency, _ = stats.Float64("banias/measures/request_latency", "Latency distribution of HTTP requests", stats.UnitMilliseconds)
	view.Subscribe(
		&view.View{
			Name:        "request_count",
			Description: "Count of HTTP requests processed",
			TagKeys:     []tag.Key{codeKey},
			Measure:     requestCounter,
			Aggregation: view.CountAggregation{},
		})
	view.Subscribe(
		&view.View{
			Name:        "request_latency",
			Description: "Latency distribution of HTTP requests",
			TagKeys:     []tag.Key{codeKey},
			Measure:     requestlatency,
			Aggregation: DefaultLatencyDistribution,
		})

	view.SetReportingPeriod(1 * time.Second)
```

#### 收集指标数据

- Call the Record method

```go
// Go Code Example
// 说明：defer 用于资源的释放，会在函数返回之前进行调用。
// 如果有多个 defer表达式，调用顺序类似于栈，越后面的 defer 表达式越先被调用。
func (c *Collector) Collect(ctx *fasthttp.RequestCtx) {
  defer func(begin time.Time) {
      responseTime := float64(time.Since(begin).Nanoseconds() / 1000)
      occtx, _ := tag.New(context.Background(), tag.Insert(codeKey, strconv.Itoa(ctx.Response.StatusCode())), )
      stats.Record(occtx, requestCounter.M(1))
      stats.Record(occtx, requestlatency.M(responseTime))
    }(time.Now())

    /*do some stuff */

}
```

#### 第三方监控系统接口

OpenCensus 收集和跟踪的应用指标可以在本地显示，也可将其发送到第三方分析工具或监控系统实现可视化，目前支持：
- [Prometheus|普罗米修斯](https://prometheus.io)
- [SignalFX](https://signalfx.com)
- [Stackdriver|适用于 Google Cloud Platform 与 AWS 应用的监控、日志记录和诊断工具](https://cloud.google.com/stackdriver/)
- [Zipkin](https://zipkin.io)
- AWS X-Ray

```go
  import (
  	 "go.opencensus.io/exporter/prometheus"
	   "go.opencensus.io/exporter/stackdriver"
	   "go.opencensus.io/stats/view"
 )

	// Export to Prometheus Monitoring.
  Exporter, err := prometheus.NewExporter(prometheus.Options{})
	if err != nil {
		logger.Error("Error creating prometheus exporter  ", zap.Error(err))
	}
	view.RegisterExporter(pExporter)


  // Export to Stackdriver Monitoring.
	sExporter, err := stackdriver.NewExporter(stackdriver.Options{ProjectID: config.ProjectID})
	if err != nil {
		logger.Error("Error creating stackdriver exporter  ", zap.Error(err))
	}

	view.RegisterExporter(sExporter)
```

## 扩展阅读：开源架构技术漫谈
- [DevOps 漫谈：基于OpenCensus构建分布式跟踪系统](https://riboseyim.github.io/2018/04/27/DevOps-OpenCensus)
- [基于Go语言快速构建一个RESTful API服务](https://riboseyim.github.io/2017/05/23/RestfulAPI/)
- [基于Kafka构建事件溯源型微服务](https://riboseyim.github.io/2017/06/12/OpenSource-Kafka-Microservice/)
- [远程通信协议：从 CORBA 到 gRPC](https://riboseyim.github.io/2017/10/30/Protocol-gRPC/)
- [应用程序开发中的日志管理(Go语言描述)](https://riboseyim.github.io/2017/05/24/Log/)
- [数据可视化（七）Graphite 体系结构详解](https://riboseyim.github.io/2017/12/04/Visualization-Graphite/)
- [动态追踪技术(一)：DTrace 导论](https://riboseyim.github.io/2016/11/26/DTrace/)
- [动态追踪技术(二)：strace+gdb 溯源 Nginx 内存溢出异常 ](https://mp.weixin.qq.com/s?__biz=MjM5MTY1MjQ3Nw==&mid=2651939588&idx=1&sn=35f71c5f88d1edf23cb2efc812ab8e6c&chksm=bd578c168a20050041c08618281691f0111f61c789097a69095933057618637fc54817815921#rd)
- [动态追踪技术(三)：Tracing Your Kernel Function!](https://riboseyim.github.io/2017/04/17/DTrace_FTrace/)
- [动态追踪技术(四)：基于 Linux bcc/BPF 实现 Go 程序动态追踪](https://riboseyim.github.io/2017/06/27/DTrace_bcc/)
- [动态追踪技术(五)：Welcome DTrace for Linux](https://riboseyim.github.io/2018/02/16/DTrace-Linux/)
- [DevOps 资讯 | LinkedIn 开源 Kafka Monitor](https://riboseyim.github.io/2016/08/15/OpenSource-Kafka/)
