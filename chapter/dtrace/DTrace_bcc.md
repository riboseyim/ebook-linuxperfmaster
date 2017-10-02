---
title: 动态追踪技术（四）：基于 Linux bcc/BPF 实现 Go 程序动态追踪
date: 2017-06-27 14:47:15
tags: [DTrace,DevOps,Linux,Golang]
---

## 摘要

- 原文：[Brendan Gregg's Blog :《Golang bcc/BPF Function Tracing》，31 Jan 2017](http://www.brendangregg.com/blog/2017-01-31/golang-bcc-bpf-function-tracing.html)
- 引子：gdb、go execution tracer、GODEBUG、gctrace、schedtrace
- 一、gccgo Function Counting
- 二、Go gc Function Counting
- 三、Per-event invocations of a function
- 四、Interface Arguments
- 五、Function Latency
- 六、总结
- 七、Tips：构建 LLVM 和 Clang 开发工具库

<!--more-->

在这篇文章中，我将迅速调研一种跟踪的 Go 程序的新方法：基于 Linux 4.x eBPF 实现动态跟踪。如果你去搜索 Go 和 BPF，你会发现使用 BPF 接口的 Go 语言接口（例如，gobpf）。这不是我所探索的东西：我将使用 BPF 工具实现 Go 应用程序的性能分析和调试。

目前已经有多种调试和追踪 Go 程序的方法，包括但不限于：
- [gdb](https://golang.org/doc/gdb)
- [go execution tracer](https://golang.org/pkg/runtime/trace/) ：用于高层异常和阻塞事件
>Go execution tracer. (import "runtime/trace")
The tracer captures a wide range of execution events like goroutine creation/blocking/unblocking, syscall enter/exit/block, GC-related events, changes of heap size, processor start/stop, etc and writes them to an io.Writer in a compact form. A precise nanosecond-precision timestamp and a stack trace is captured for most events. A trace can be analyzed later with 'go tool trace' command.

- **GODEBUG** （一个跨平台的Go程序调试工具）、 **gctrace** 和 **schedtrace**

BPF 追踪以做很多事，但都有自己的优点和缺点，接下来将详细说明。首先我从一个简单的 Go 程序开始（ hello.go）

```go
package main

import "fmt"

func main() {
        fmt.Println("Hello, BPF!")
}
```

## 一、gccgo Function Counting

我开始会使用 gccgo 编译，然后使用 Go gc 编译器 。（区别：gccgo 可以生成优化后的二进制文件，但是基于老版本的 Go。）

```bash
## 编译
$ gccgo -o hello hello.go
$ ./hello
Hello, BPF!
```

现在我将使用 [bcc](https://github.com/iovisor/bcc) 工具的 **funccount** 来动态跟踪和计数所有以 “fmt.” 开头的 Go 库函数调用，在另一个终端重新运行 Hello 程序效果如下：

```bash
# funccount 'go:fmt.*'
Tracing 160 functions for "go:fmt.*"... Hit Ctrl-C to end.
^C
FUNC                                    COUNT
fmt..import                                 1
fmt.padString.pN7_fmt.fmt                   1
fmt.fmt_s.pN7_fmt.fmt                       1
fmt.WriteString.pN10_fmt.buffer             1
fmt.free.pN6_fmt.pp                         1
fmt.fmtString.pN6_fmt.pp                    1
fmt.doPrint.pN6_fmt.pp                      1
fmt.init.pN7_fmt.fmt                        1
fmt.printArg.pN6_fmt.pp                     1
fmt.WriteByte.pN10_fmt.buffer               1
fmt.Println                                 1
fmt.truncate.pN7_fmt.fmt                    1
fmt.Fprintln                                1
fmt.$nested1                                1
fmt.newPrinter                              1
fmt.clearflags.pN7_fmt.fmt                  2
Detaching...
```

Neat! 输出结果中包含该程序的 **fmt.Println()** 函数调用。

我不需要进入任何特殊的模式才能实现这个效果，对于一个已经在运行的 Go 应用我可以直接开始测量而不需要重启进程。 **So how does it even work?** 这要归功于 **uprobes** ，Linux 3.5 新增的特性，详见[Linux uprobes: User-Level Dynamic Tracing](http://www.brendangregg.com/blog/2015-06-28/linux-ftrace-uprobe.html) 。

>It overwrites instructions with a soft interrupt to kernel instrumentation, and reverses the process when tracing has ended.

gccgo 编译的输出提供一个标准的符号表用于函数查找。在这种情况下，我利用 libgo 当测量工具（假定“lib”在“go:”之前），作为 gccgo 发出的一个二进制动态链接库（libgo 包含 fmt 包）。uprobes 可以连接到已经运行的进程，或者像我现在一样作为一个二进制库，捕捉所有调用自己的进程。

为了提高效率，我在内核上下文中进行函数调用计数，只将计数发送到用户空间。例如：

```Go
$ file hello
hello: ELF 64-bit LSB executable, x86-64, version 1 (SYSV), dynamically linked, interpreter /lib64/ld-linux-x86-64.so.2, for GNU/Linux 2.6.32, BuildID[sha1]=4dc45f1eb023f44ddb32c15bbe0fb4f933e61815, not stripped
$ ls -lh hello
-rwxr-xr-x 1 bgregg root 29K Jan 12 21:18 hello
$ ldd hello
    linux-vdso.so.1 =>  (0x00007ffc4cb1a000)
    libgo.so.9 => /usr/lib/x86_64-linux-gnu/libgo.so.9 (0x00007f25f2407000)
    libgcc_s.so.1 => /lib/x86_64-linux-gnu/libgcc_s.so.1 (0x00007f25f21f1000)
    libc.so.6 => /lib/x86_64-linux-gnu/libc.so.6 (0x00007f25f1e27000)
    /lib64/ld-linux-x86-64.so.2 (0x0000560b44960000)
    libpthread.so.0 => /lib/x86_64-linux-gnu/libpthread.so.0 (0x00007f25f1c0a000)
    libm.so.6 => /lib/x86_64-linux-gnu/libm.so.6 (0x00007f25f1901000)
$ objdump -tT /usr/lib/x86_64-linux-gnu/libgo.so.9 | grep fmt.Println
0000000001221070 g     O .data.rel.ro   0000000000000008              fmt.Println$descriptor
0000000000978090 g     F .text  0000000000000075              fmt.Println
0000000001221070 g    DO .data.rel.ro   0000000000000008  Base        fmt.Println$descriptor
0000000000978090 g    DF .text  0000000000000075  Base        fmt.Println
```

这些内容看起来非常像一个编译过的 C 语言二进制程序，因此可以使用包括 bcc/BPF在内的许多现有的调试工具和追踪器观测。相对于测量即时编译的运行时要简单得多（例如 Java 和 Node.js）。到目前为止，这个例子唯一的麻烦事函数名称中可能包含非标准的字符，例如“.”。

**funccount** also has options like -p to match a PID, and -i to emit output every interval. It currently can only handle up to 1000 probes at a time, so "fmt.\*" was ok, but matching everything in libgo:

**funccount** 提供 -p 选项来匹配进程号（PID），-i 选项来控制输出频率。它目前能够同时处理 1000 个探测点，匹配 “fmt.\*” 时运行正常，但是匹配 libgo 的所有函数就出现异常。诸如此类的问题在 bcc/BPF 中还有不少，我们需要寻找其它的方法来处理。

```bash
# funccount 'go:*'
maximum of 1000 probes allowed, attempted 21178
```

## 二、Go gc Function Counting

使用 Go 语言的 gc 编译器实现 fmt 函数调用计数：

```bash
$ go build hello.go
$ ./hello
Hello, BPF!
```

```bash
# funccount '/home/bgregg/hello:fmt.*'
Tracing 78 functions for "/home/bgregg/hello:fmt.*"... Hit Ctrl-C to end.
^C
FUNC                                    COUNT
fmt.init.1                                  1
fmt.(*fmt).padString                        1
fmt.(*fmt).truncate                         1
fmt.(*fmt).fmt_s                            1
fmt.newPrinter                              1
fmt.(*pp).free                              1
fmt.Fprintln                                1
fmt.Println                                 1
fmt.(*pp).fmtString                         1
fmt.(*pp).printArg                          1
fmt.(*pp).doPrint                           1
fmt.glob.func1                              1
fmt.init                                    1
Detaching...
```

你依然能够追踪到 fmt.Println() ，这个二进制程序与 libgo 有所不同：包含该函数的是一个 2M 的静态库（而非动态库的 29K ）。另一个区别就是函数名称包含更多特殊字符，例如 "\*", "(",等等，我怀疑如果不能修正处理的haul将影响其它调试器（例如 bcc 追踪器）。

```bash
$ file hello
hello: ELF 64-bit LSB executable, x86-64, version 1 (SYSV), statically linked, not stripped
$ ls -lh hello
-rwxr-xr-x 1 bgregg root 2.2M Jan 12 05:16 hello
$ ldd hello
    not a dynamic executable
$ objdump -t hello | grep fmt.Println
000000000045a680 g     F .text  00000000000000e0 fmt.Println
```

## 三、Per-event invocations of a function

#### 3.1 gccgo Function Tracing

现在我将尝试使用 [Sasha Goldshtein](https://www.linkedin.com/in/sashag/?ppe=1) 的追踪工具，也是基于 [bcc](https://github.com/iovisor/bcc)，用来查看每一个函数调用事件。我将回到 gccgo，使用一个非常简单的示例程序（ from the [go tour](https://tour.golang.org/basics/4) ），functions.go:

```go
package main

import "fmt"

func add(x int, y int) int {
    return x + y
}

func main() {
    fmt.Println(add(42, 13))
}
```

追踪 add() 函数。所有参数都输出在右侧，trace 还有其他选项（帮助 -h ），例如输出时间戳和堆栈。

```bash
\# trace '/home/bgregg/functions:main.add'
PID    TID    COMM         FUNC             
14424  14424  functions    main.add  

#... and with both its arguments:

\# trace '/home/bgregg/functions:main.add "%d %d" arg1, arg2'
PID    TID    COMM         FUNC             -
14390  14390  functions    main.add         42 13
```

#### 3.2 Go gc Function Tracing

同样的程序，如果使用 go build 就没有 main.add() ?  禁用代码嵌入（ Disabling inlining）即可。

```Go
$ go build functions.go

# trace '/home/bgregg/functions:main.add "%d %d" arg1, arg2'
could not determine address of symbol main.add

$ objdump -t functions | grep main.add
$
```

```go
$ go build -gcflags '-l' functions.go
$ objdump -t functions | grep main.add
0000000000401000 g     F .text  0000000000000020 main.add

# trace '/home/bgregg/functions:main.add "%d %d" arg1, arg2'
PID    TID    COMM         FUNC             -
16061  16061  functions    main.add         536912504 16
```

**That's wrong.** 参数应该是 42 和 13 而不是 536912504 和 16。
使用 gdb 查看结果如下：

```bash
$ gdb ./functions
[...]
warning: File "/usr/share/go-1.6/src/runtime/runtime-gdb.py" auto-loading has been declined
 by your 'auto-load safe-path' set to "$debugdir:$datadir/auto-load".
[...]
(gdb) b main.add
Breakpoint 1 at 0x401000: file /home/bgregg/functions.go, line 6.
(gdb) r
Starting program: /home/bgregg/functions
[New LWP 16082]
[New LWP 16083]
[New LWP 16084]
Thread 1 "functions" hit Breakpoint 1, main.add (x=42, y=13, ~r2=4300314240) at
 /home/bgregg/functions.go:6
6           return x + y
(gdb) i r
rax            0xc820000180 859530330496
rbx            0x584ea0 5787296
rcx            0xc820000180 859530330496
rdx            0xc82005a048 859530698824
rsi            0x10 16
rdi            0xc82000a2a0 859530371744
rbp            0x0  0x0
rsp            0xc82003fed0 0xc82003fed0
r8             0x41 65
r9             0x41 65
r10            0x4d8ba0 5082016
r11            0x0  0
r12            0x10 16
r13            0x52a3c4 5415876
r14            0xa  10
r15            0x8  8
rip            0x401000 0x401000
eflags         0x206    [ PF IF ]
cs             0xe033   57395
ss             0xe02b   57387
ds             0x0  0
es             0x0  0
fs             0x0  0
gs             0x0  0
```

启动信息中包含一个关于 runtime-gdb.py 的警告，它非常有用：如果需要进一步深入挖掘 Go 上下文，我希望能够修复并找出告警原因。即使没有该信息，gdb 依然可以输出参数变量的值是 "x=42, y=13"。我也将它们从寄存器导出与 x86_64 ABI（Application Binary Interface，应用程序二进制接口）对比，which is how bcc's trace reads them. From the syscall(2) man page:

```bash
       arch/ABI      arg1  arg2  arg3  arg4  arg5  arg6  arg7  Notes
       ──────────────────────────────────────────────────────────────────
[...]
       x86_64        rdi   rsi   rdx   r10   r8    r9    -
```

>The reason is that Go's gc compiler is not following the standard AMD64 ABI function calling convention, which causes problems with this and other debuggers.

42 和 13 没有出现在 rdi , rsi 或者其它任何一个寄存器。原因是 Go 的 gc 编译器不遵循标准的 AMD64 ABI 函数调用约定，其它调试器也会存在这个问题。这很烦人。我猜 Go 语言的返回值使用的是另外一种 ABI，因为它可以返回多个值，所以即使入口参数是标准的，我们仍然会遇到差异。我浏览了指南（Quick Guide to Go's Assembler and the Plan 9 assembly manual），看起来函数在堆栈上传递。这些是我们的 42 和 13：

```C
(gdb) x/3dg $rsp
0xc82003fed0:   4198477 42
0xc82003fee0:   13
```

BPF can dig these out too. As a proof of concept, I just hacked in a couple of new aliases, "go1" and "go2" for those entry arguments:

BPF 也可以挖掘这些信息。为了验证这一个概念，我为入口参数声明一对新的别名“go1”和“go2” 。希望您阅读本文的时候，我（或者其他人）已经将它加入到 bcc 追踪工具中，最好是 "goarg1", "goarg2", 等等。

```bash
# trace '/home/bgregg/functions:main.add "%d %d" go1, go2'
PID    TID    COMM         FUNC             -
17555  17555  functions    main.add         42 13
```

## 四、Interface Arguments

你可以写一个自定义的 bcc/BPF 程序来挖掘，为了处理接口参数我们可以给 bcc 的跟踪程序添加多个别名。输入参数是接口的示例：

```go
func Println(a ...interface{}) (n int, err error) {
    return Fprintln(os.Stdout, a...)
```

```go
$ gdb ./hello
[...]
(gdb) b fmt.Println
Breakpoint 1 at 0x401c50
(gdb) r
Starting program: /home/bgregg/hello
[Thread debugging using libthread_db enabled]
Using host libthread_db library "/lib/x86_64-linux-gnu/libthread_db.so.1".
[New Thread 0x7ffff449c700 (LWP 16836)]
[New Thread 0x7ffff3098700 (LWP 16837)]
[Switching to Thread 0x7ffff3098700 (LWP 16837)]
Thread 3 "hello" hit Breakpoint 1, fmt.Println (a=...) at ../../../src/libgo/go/fmt/print.go:263
263 ../../../src/libgo/go/fmt/print.go: No such file or directory.
(gdb) p a
$1 = {__values = 0xc208000240, __count = 1, __capacity = 1}
(gdb) p a.__values
$18 = (struct {...} *) 0xc208000240
(gdb) p a.__values[0]
$20 = {__type_descriptor = 0x4037c0 <__go_tdn_string>, __object = 0xc208000210}
(gdb) x/s *0xc208000210
0x403483:   "Hello, BPF!"
```

## 五、Function Latency

- 示例：循环调用 fmt.Println() 函数的时延直方图（纳秒）

**WARNING:** Go 函数调用过程中，如果从一个进程（goroutine）切换到另外一个系统进程，**funclatency** 无法匹配入口-返回。这种场景需要一个新的工具 —— **gofunclatency** ，它基于 Go 内建的 GOID 替代系统的 TID 追踪时延，在某些情况下， **uretprobes** 修改 Go 程序可能出现崩溃的问题，因此在调试之前需要准备周全的计划。

```go
# funclatency 'go:fmt.Println'
Tracing 1 functions for "go:fmt.Println"... Hit Ctrl-C to end.
^C

Function = fmt.Println [3041]
     nsecs               : count     distribution
         0 -> 1          : 0        |                                        |
         2 -> 3          : 0        |                                        |
         4 -> 7          : 0        |                                        |
         8 -> 15         : 0        |                                        |
        16 -> 31         : 0        |                                        |
        32 -> 63         : 0        |                                        |
        64 -> 127        : 0        |                                        |
       128 -> 255        : 0        |                                        |
       256 -> 511        : 0        |                                        |
       512 -> 1023       : 0        |                                        |
      1024 -> 2047       : 0        |                                        |
      2048 -> 4095       : 0        |                                        |
      4096 -> 8191       : 0        |                                        |
      8192 -> 16383      : 27       |****************************************|
     16384 -> 32767      : 3        |****                                    |
Detaching...
```


## 六、总结

原作者总结很简洁，不再赘述。

>I took a quick look at Golang with dynamic tracing and Linux enhanced BPF, via bcc's funccount and trace tools, with some successes and some challenges. Counting function calls works already. Tracing function arguments when compiled with gccgo also works, whereas Go's gc compiler doesn't follow the standard ABI calling convention, so the tools need to be updated to support this. As a proof of concept I modified the bcc trace tool to show it could be done, but that feature needs to be coded properly and integrated. Processing interface objects will also be a challenge, and multi-return values, again, areas where we can improve the tools to make this easier, as well as add macros to C for writing other custom Go observability tools as well.


## 七、Tips

#### 6.1 安装和编译 BCC
```
git clone https://github.com/iovisor/bcc.git
mkdir bcc/build; cd bcc/build
cmake -DCMAKE_INSTALL_PREFIX=/usr \
      -DLUAJIT_INCLUDE_DIR=`pkg-config --variable=includedir luajit` \ # for lua support
      ..
make
sudo make install
cmake -DPYTHON_CMD=python3 .. # build python3 binding
pushd src/python/
make
sudo make install
popd
```

#### 6.2 构建 LLVM 和 Clang 开发工具库

```bash
yum install gcc
yum install gcc-g++

wget https://cmake.org/files/v3.9/cmake-3.9.0-rc4.tar.gz
tar -xvf cmake-3.9.0-rc4.tar.gz
cd cmake-3.9.0
./bootstrap  
gmake
gmake install
export CMAKE_ROOT=/usr/local/share/cmake-3.9.0
export PATH=$PATH:$CMAKE_ROOT

git clone http://llvm.org/git/llvm.git
cd llvm/tools;
git clone http://llvm.org/git/clang.git
cd ..; mkdir -p build/install; cd build
cmake -G "Unix Makefiles" -DLLVM_TARGETS_TO_BUILD="BPF;X86" -DCMAKE_BUILD_TYPE=Release -DCMAKE_INSTALL_PREFIX=$PWD/install ..
make
make install
export PATH=$PWD/install/bin:$PATH
```

#### 6.3 LLVM 与 Clang
LLVM （Low Level Virtual Machine）是一种编译器基础设施，以C++写成。起源于2000年伊利诺伊大学厄巴纳-香槟分校维克拉姆·艾夫（Vikram Adve）与克里斯·拉特纳（Chris Lattner）的研究，他们想要为所有静态及动态语言创造出动态的编译技术。2005年，Apple直接雇用了克里斯·拉特纳及他的团队，为了苹果电脑开发应用程序，期间克里斯·拉特纳设计发明了 Swift 语言，LLVM 成为 Mac OS X 及 iOS 开发工具的一部分。LLVM 的范围早已经不局限于创建一个虚拟机，成为了众多编译工具及低级工具技术的统称，适用于LLVM下的所有项目，包含LLVM中介码（LLVM IR）、LLVM除错工具、LLVM C++标准库等。

目前 LLVM 已支持包括ActionScript、Ada、D语言、Fortran、GLSL、Haskell、Java字节码、Objective-C、Swift、Python、Ruby、Rust、Scala1以及C#等语言。

Clang 是LLVM编译器工具集的前端（front-end），目的是输出代码对应的抽象语法树（Abstract Syntax Tree, AST），并将代码编译成LLVM Bitcode。接着在后端（back-end）使用 LLVM 编译成平台相关的机器语言 。Clang支持C、C++、Objective C。它的目标是提供一个 GCC 的替代品。作者是克里斯·拉特纳（Chris Lattner），在苹果公司的赞助支持下进行开发。Clang项目包括Clang前端和Clang静态分析器等。

#### 6.4 ABI
应用二进制接口（Application Binary Interface， ABI）描述了应用程序和操作系统之间或其他应用程序的低级接口。ABI涵盖了各种细节，如：
- 数据类型的大小、布局;
- 调用约定（控制着函数的参数如何传送以及如何接受返回值），例如，是所有的参数都通过栈传递，还是部分参数通过寄存器传递；哪个寄存器用于哪个函数参数；通过栈传递的第一个函数参数是最先push到栈上还是最后；
- 目标文件的二进制格式、程序库等等。

- ABI vs API
应用程序接口 (API)定义了源代码和库之间的接口，因此同样的代码可以在支持这个API的任何系统中编译，然而ABI允许编译好的目标代码在使用兼容 ABI 的系统中无需改动就能运行。

## 动态追踪技术合辑
- [How Linux Works（一）：How the Linux Kernel Boots](http://riboseyim.github.io/2017/05/29/Linux-Works/)
- [How Linux Works（二）：User Space & RAM](http://riboseyim.github.io/2017/05/29/Linux-Works/)

- [动态追踪技术（一）：简介| @RiboseYim 译](http://riboseyim.github.io/2016/11/26/DTrace/)
- [动态追踪技术（二）：strace+gdb 溯源 Nginx 内存溢出异常 ](https://mp.weixin.qq.com/s?__biz=MjM5MTY1MjQ3Nw==&mid=2651939588&idx=1&sn=35f71c5f88d1edf23cb2efc812ab8e6c&chksm=bd578c168a20050041c08618281691f0111f61c789097a69095933057618637fc54817815921#rd)
- [动态追踪技术（三）：Tracing your kernel Functions! | @RiboseYim 译](http://riboseyim.github.io/2017/04/17/DTrace_FTrace/)
- [动态追踪技术（四）：基于 Linux bcc/BPF 实现 Go 程序动态追踪](http://riboseyim.github.io/2017/06/27/DTrace_bcc/)

## 参考文献
- [Linux MySQL Slow Query Tracing with bcc/BPF | Brendan Gregg's Blog](http://www.brendangregg.com/blog/2016-10-04/linux-bcc-mysqld-qslower.html)
- [Notes on BPF & eBPF | Julia Evans](https://jvns.ca/blog/2017/06/28/notes-on-bpf---ebpf/)
- [Probing the JVM with BPF/BCC | Sasha](http://blogs.microsoft.co.il/sasha/2016/03/31/probing-the-jvm-with-bpfbcc/)
- [BPF: Tracing and more | Brendan Gregg SlideShare ](https://www.slideshare.net/brendangregg/bpf-tracing-and-more)
