# DTrace

## 4.3.1 静态和动态跟踪
## 4.3.2 探针

**四元组**
>provider:module:function:name

provider:一些相关探针的集合，类似软件库的概念
module&function:动态产生，标记探针指示的代码位置。
name:探针的名字

## 4.3.3 provider

可用的DTrace provider 取决于你的DTrace和操作系统版本。例如：

syscall:系统调用自陷表
vminfo:虚拟内存统计
sysinfo:系统统计
profile:任意频率的采样
sched:内核调度事件
proc:进程级别事件，创建、执行、退出
io:磁盘
pid:用户级别动态追踪
tcp:TCP协议事件，连接、发送和接收。
ip:IP协议事件、发送和接收
fbt:内核级别动态追踪


## 4.3.4 参数
  入口：
  返回：

## 4.3.5 D语言
  D语言和awk类似
  ```
  probe_description /predicate/ ( action )
  ```
  action是一系列以分号分隔的语句，当探针触发时执行。
  predicate是可选的过滤表达式

## 4.3.6 内置变量
## 4.3.7 action
## 4.3.8 变量类型
## 4.3.9 单行命令
## 4.3.10 脚本
## 4.3.11 开销
## 4.3.12 文档和资源
