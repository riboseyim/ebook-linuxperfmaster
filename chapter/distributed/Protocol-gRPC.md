# 计算机远程通信协议：gRPC

## 摘要
- 一、远程调用技术简史:从 CORBA 到 gRPC
- 二、gRPC 简介
- 三、gRPC 示例代码

## 远程通信协议：从 CORBA 到 gRPC
自从产业界发明机器联网的那一天就已经开始探索最优的远程通信机制。操作系统如 UNIX、Windows 和 Linux 等都有实现远程通信的内部协议，挑战在于如何向开发人员开放一个通信框架。

## 一、远程调用技术简史
在20世纪90年代，当 TCP/IP 协议日臻成熟变成网络通信的黄金标准时，焦点转移到跨平台通信 —— 一台计算机可以通过某种类型网络在另一台计算机上发起一个动作。例如如 CORBA、DCOM、Java RMI 技术，在核心网络基础设施之上创造了一个对开发者友好的抽象层。这些技术还试图发展出一套与开发语言无关的通信框架，这一点对于客户机/服务器体系结构至关重要。

随着本世纪初 Web 技术的演进，HTTP 逐渐演变为事实上的通信标准。HTTP 结合 XML 提供了一种自我描述、不依赖语言、与平台无关的远程通信框架。这种结合的成果是 SOAP 和 WSDL 标准，它们保证了在各种运行环境和平台之间实现互操作的标准化。

下一个冲击互联网的浪潮是 Web 编程。许多开发人员发现定义 SOAP 标准的 HTTP 和 XML 的组合过于严格。这时 JavaScript 和 JSON 开始流行了。Web 2.0 现象（API 发挥了关键作用）, JSON 替代 XML 成为首选的协议。HTTP 和 JSON 这对致命的组合，催生了一个新的非官方标准 REST 。SOAP 要求严格遵守标准和结构定义，仅局限于大型企业应用程序，而 REST 在当代开发人员中很受欢迎。

#### 1.1 HTTP, REST 和微服务
归功于 JavaScript 框架，Node.js 以及文档数据库的发展，REST 在 Web 开发者中广受欢迎。许多应用程序开始基于 REST 实现 ，即使是内部序列化和通信模式领域。但 HTTP 是最有效的消息交换协议吗？即使在同一上下文、同一网络，或者是同一台机器上运行的服务之间？HTTP 的便捷性与高性能之间需要作出权衡，这促使我们回到问题的起点，寻找微服务架构中最优的通信框架。

进入 gRPC 时代 —— 来自谷歌，现代的轻量级通信协议。这是一个高性能的、开源的通用远程过程调用（RPC） 框架，它可以在多种开发语言、任何操作系统上运行。

gRPC 在推出的第一年内就被 CoreOS，Netflix，Square 和 Cockroach Labs 等机构采用。 CoreOS 团队的 Etcd，是一种分布式键/值存储服务，采用 gRPC 实现端通信。电信公司如 Cisco，Juniper 和 Arista 都使用 gRPC 实现数据流遥测和网络设备配置。

####  1.2 什么是 gRPC ?
当我第一次遇到 gRPC，它使我想到 CORBA。两个框架都基于语言无关的接口定义语言（IDL） 声明服务，通过特定的语言绑定实现。
![](http://riboseyim-qiniu.riboseyim.com/OpenSource-gRPC.png)

CORBA 和 gRPC 二者的设计，都是为了使客户端相信服务器在同一台机器。客户机在桩（Stub）上调用一个方法（method），调用过程由底层协议透明地处理。

gRPC 的秘诀在于处理序列化的方式。gRPC 基于 Protocol Buffer，一个开源的用于结构化数据序列化机制，它是语言和平台无关的。Protocol Buffer  的描述非常详细，与 XML 类似。但是它们比其他的协议格式更小，更快，效率更高。任何需要序列化的自定义数据类型在 gRPC 被定义为一个 Protocol Buffer 。

Protocol Buffer 的最新版本是 proto3，支持多种开发语言的代码生成，Java , C++，Python，Ruby , Java Lite , JavaScript，Objective-C 和 C # 。当一个 Protocol Buffer 编译为一个特定的语言，它的访问器（setter 和 getter）为每个字段提供定义。

相比于 REST + JSON 组合 ，gRPC 提供更好的性能和安全性。它极大的促进了在客户端和服务器之间使用 SSL / TLS 进行身份验证和数据交换加密。

为什么微服务开发者需要使用 gRPC ？gRPC 采用 HTTP / 2 以支持高性能的、可扩展的 API 。报文使用二进制而不是文本通信可以保持载荷紧凑、高效。HTTP / 2 请求在一个 TCP 连接上可支持多路复用，允许多个消息并发传送而不影响网络资源利用率。gRPC 使用报头压缩（header compression ）来减少请求和响应的大小。

## 二、gRPC 简介

#### 2.1 创建 gRPC 服务的流程
1. 在 Protocol Buffer (.proto) 文件中描述服务和载荷结构
2. 从 .proto 文件生成 gRPC 代码
3. 用一种开发语言实现服务端
4. 创建一个客户端调用服务
5. 运行服务端和客户端

![](http://riboseyim-qiniu.riboseyim.com/Arch_gRPC_Workflow.png)

**Note:Node.js 客户端不需要生成存根（Stub），只要 Protocol Buffer 文件是可访问的，它就可以直接与服务端对话。**

## 三、gRPC 示例代码

为了进一步熟悉 gRPC，我们将用 Python 语言创建一个简单的计算服务。它将同时被一个 Python 客户端和一个 Node.js 客户端调用。以下测试示例运行在 Mac OS X 。

你可以从 GitHub 库 https://github.com/grpc/grpc/tree/master/examples 访问源代码，在自己的机器上运行示例。

- 环境准备
```bash
// 配置 Python gRPC
python -m pip install virtualenv
virtualenv venv
source venv/bin/activate
python -m pip install --upgrade pip

//安装 gRPC 和 gRPC Tools
python -m pip install grpcio
python -m pip install grpcio-tools

// 配置 Node.js gRPC
npm install grpc --global

//创建目录
mkdir Proto
mkdir Server
mkdir -p Client/Python
mkdir -p Client/Node
```

- 创建 Protocol Buffer 文件

```go
//Proto/Calc.proto
syntax = "proto3";

package calc;

service Calculator {
  rpc Add (AddRequest) returns (AddReply) {}
  rpc Substract (SubstractRequest) returns (SubstractReply) {}
  rpc Multiply (MultiplyRequest) returns (MultiplyReply) {}
  rpc Divide (DivideRequest) returns (DivideReply) {}
}

message AddRequest{
  int32 n1=1;
  int32 n2=2;
}
message AddReply{
  int32 n1=1;
}
message SubstractRequest{
  int32 n1=1;
  int32 n2=2;
}
message SubstractReply{
  int32 n1=1;
}
message MultiplyRequest{
  int32 n1=1;
  int32 n2=2;
}
message MultiplyReply{
  int32 n1=1;
}
message DivideRequest{
  int32 n1=1;
  int32 n2=2;
}
message DivideReply{
  float f1=1;
}
```

- 生成 Python 服务端和客户端代码
```
$ python -m grpc.tools.protoc  --python_out=. --grpc_python_out=. --proto_path=. Calc.proto
$ cp Calc_pb2.py ../Server
$ cp Calc_pb2.py ../Client/Python
$ cp Calc.proto ../Client/Node
```

- 创建服务端

```python
# Server/Calc_Server.py
from concurrent import futures
import time

import grpc

import Calc_pb2
import Calc_pb2_grpc

_ONE_DAY_IN_SECONDS = 60 * 60 * 24

class Calculator(Calc_pb2.CalculatorServicer):

 def Add(self, request, context):
   return Calc_pb2.AddReply(n1=request.n1+request.n2)

 def Substract(self, request, context):
   return Calc_pb2.SubstractReply(n1=request.n1-request.n2)

 def Multiply(self, request, context):
   return Calc_pb2.MultiplyReply(n1=request.n1*request.n2)

 def Divide(self, request, context):
   return Calc_pb2.DivideReply(f1=request.n1/request.n2)

def serve():
 server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
 Calc_pb2_grpc.add_CalculatorServicer_to_server(Calculator(), server)
 server.add_insecure_port('[::]:50050')
 server.start()

 try:
   while True:
     time.sleep(_ONE_DAY_IN_SECONDS)
 except KeyboardInterrupt:
   server.stop(0)

if __name__ == '__main__':
 serve()
```

- 启动服务端
```bash
python Calc_Server.py
```

- 创建 Python 客户端

```python
# Client/Python/Calc_Client.py

from __future__ import print_function

import grpc
import Calc_pb2
import Calc_pb2_grpc

def run():
 channel = grpc.insecure_channel('localhost:50050')
 stub = Calc_pb2_grpc.CalculatorStub(channel)

 response = stub.Add(Calc_pb2.AddRequest(n1=20,n2=10))
 print(response.n1)
 response = stub.Substract(Calc_pb2.SubstractRequest(n1=20,n2=10))
 print(response.n1)
 response = stub.Multiply(Calc_pb2.MultiplyRequest(n1=20,n2=10))
 print(response.n1)
 response = stub.Divide(Calc_pb2.DivideRequest(n1=20,n2=10))
 print(response.f1)

if __name__ == '__main__':
  run()
```

- 创建 Node.js 客户端
```JavaScript
//Client/Node/Calc_Client.js
var PROTO_PATH = 'Calc.proto';

var grpc = require('grpc');
var calc_proto = grpc.load(PROTO_PATH).calc;
var params={n1:20, n2:10};

function main() {
 var client = new calc_proto.Calculator('localhost:50050',
                                      grpc.credentials.createInsecure());

 client.divide(params, function(err, response) {
   console.log(response.f1);
 });

 client.multiply(params, function(err, response) {
   console.log(response.n1);
 });

 client.substract(params, function(err, response) {
   console.log(response.n1);
 });

 client.add(params, function(err, response) {
   console.log(response.n1);
 });

}

main();
```

- 启动客户端 Node.js/Python
```bash
$ python Calc_Client.py
30
10
200
2.0

$ node Calc_Client.js
30
10
200
2.0
```

## 附表：gRPC 年谱
- 2011 : Protocol Buffers 2 => language neutral for serializing structured data
- 2015 : Borg => Large-scale cluster management => Kubernetes
- 2015 : Stubby => A high performance RPC framework => gRPC
- July 2016 : Protocol Buffers 3.0.0
- Aug 2016 : gRPC 1.0 ready for production
- Sept 2016 : Swift-protobuf
- Jan 2017 : Grpc Swift
- Apr 2017 : Google Endpoints => Manage gRPC APIs with Cloud Endpoints
- Sept 2017 : gRPC 1.6.1
- Sept 2017 : Protocol Buffers 3.4.1
- Oct 2017 : Swift-protobuf 1.0
- Oct 2017 : gRPC 1.7.0


## 扩展阅读：开发语言&代码工程
- [Stack Overflow：2017年最赚钱的编程语言](https://riboseyim.github.io/2017/07/23/CloudComputing/)
- [玩转编程语言:构建自定义代码生成器](https://riboseyim.github.io/2017/12/21/Language-Auto-Generator/)
- [远程通信协议：从 CORBA 到 gRPC](https://riboseyim.github.io/2017/10/30/Protocol-gRPC/)
- [基于Kafka构建事件溯源型微服务](https://riboseyim.github.io/2017/06/12/OpenSource-Kafka-Microservice/)
- [LinkedIn 开源 Kafka Monitor](https://riboseyim.github.io/2016/08/15/OpenSource-Kafka/)
- [基于Go语言快速构建一个RESTful API服务](https://riboseyim.github.io/2017/05/23/RestfulAPI/)
- [应用程序开发中的日志管理(Go语言描述)](https://riboseyim.github.io/2017/05/24/Log/)

## 参考文献
- [Google’s gRPC: A Lean and Mean Communication Protocol for Microservices | 9 Sep 2016 7:30am, by Janakiram MSV](https://thenewstack.io/grpc-lean-mean-communication-protocol-microservices/)
- [gRPC microservices are the future ? | Golang Nantes Meetup 21 September 2017 | Cyrille 、Hemidy](https://speakerd.s3.amazonaws.com/presentations/34441cd016c14d0ebc9aab78c5317f08/gRPC_microservices_are_the_future__5_.pdf)
- [gRPC：Google开源的基于HTTP/2和ProtoBuf的通用RPC框架](http://www.infoq.com/cn/news/2015/03/grpc-google-http2-protobuf)
- [gRPC调用超时控制](https://ipfans.github.io/2017/04/grpc-call-timeout/)
- [How to use etcd service discovery with gRPC in Go? ](https://stackoverflow.com/questions/43638397/how-to-use-etcd-in-service-discovery)
- [Building High Performance APIs In Go Using gRPC](http://www.agiratech.com/building-high-performance-apis-go-grpc/)
- [tracing gRPC calls in #Golang with #Google Stackdriver]( https://t.co/zxNzlk7kwC)
- [Getting Started with Microservices using Go, gRPC and Kubernetes](https://outcrawl.com/getting-started-microservices-go-grpc-kubernetes/?utm_content=bufferedde1&utm_medium=social&utm_source=twitter.com&utm_campaign=buffer)
