

[原文](http://www.elvinefendi.com/2017/03/07/my-experience-with-lua-nginx-openssl-strace-gdb-glibc-and-linux-vm.html)

在lua-nginx-module 中，一个内存相关的黑魔法导致冗余的大内存分配。

最近我在线上改变了一个的 Nginx 配置，导致 OOM（Out of Memory） killer 在 Nginx 加载新配置的过程中 杀死了 Nginx 进程。这是添加到配置中的行：

lua_ssl_trusted_certificate /etc/ssl/certs/ca-certificates.crt;
在这篇文章中，我将会阐述我是如何找出这个问题的根本原因、记录在这个过程中现学现用的工具。这篇文章内容细节非常琐碎。在进行深入阅读前，先列下使用的软件栈：

Openssl 1.0.2j
OS:Ubuntu Trusty with Linux 3.19.0-80-generic
Nginx:Openresty bundle 1.11.2
glibc:Ubuntu EGLIBC 2.19-0ubuntu6.9
我们从 OOM Killer 开始。它是一个 Linux 内核函数，当内核不能分配更多的内存空间的时候它将会被触发。OOM Killer 的任务是探测哪一个进程是对系统危害最大（参考 https://linux-mm.org/OOM_Killer,获取更多关于坏评分是如何计算出来的信息），一旦检测出来，将会杀死进程、释放内存。也就是说我遇到的情况是 ，Nginx 是在申请越来越多的内存，最终内核申请内存失败并且触发OOM Killer，杀死 Nginx 进程。

到此为止，现在让我们看看当 Nginx 重新加载配置的时候做了什么。可以使用 strace 进行跟踪。这是一个非常棒的工具，能在不用阅读源码的情况下查看程序正在做什么。

在我这里，执行：

sudo strace -p `cat /var/run/nginx.pid` -f
接着

sudo /etc/inid.t/nginx reload
-f 选项告诉 strace 也要对子进程进行跟踪。 在http://jvns.ca/zines/#strace-zine.你能看到一个对strace非常好的评价。下面是一个非常有趣的片段，执行完strace后输出的：


[pid 31774] open("/etc/ssl/certs/ca-certificates.crt", O_RDONLY) = 5
[pid 31774] fstat(5, {st_mode=S_IFREG|0644, st_size=274340, ...}) = 0
[pid 31774] mmap(NULL, 4096, PROT_READ|PROT_WRITE, MAP_PRIVATE|MAP_ANONYMOUS, -1, 0) = 0x7f6dc8266000
[pid 31774] read(5, "-----BEGIN CERTIFICATE-----\nMIIH"..., 4096) = 4096
[pid 31774] read(5, "WIm\nfQwng4/F9tqgaHtPkl7qpHMyEVNE"..., 4096) = 4096
[pid 31774] read(5, "Ktmyuy/uE5jF66CyCU3nuDuP/jVo23Ee"..., 4096) = 4096
...<stripped for clarity>...
[pid 31774] read(5, "MqAw\nhi5odHRwOi8vd3d3Mi5wdWJsaWM"..., 4096) = 4096
[pid 31774] read(5, "dc/BGZFjz+iokYi5Q1K7\ngLFViYsx+tC"..., 4096) = 4096
[pid 31774] brk(0x26d3000)              = 0x26b2000
[pid 31774] mmap(NULL, 1048576, PROT_READ|PROT_WRITE, MAP_PRIVATE|MAP_ANONYMOUS, -1, 0) = 0x7f6c927c3000
[pid 31774] read(5, "/lmci3Zt1/GiSw0r/wty2p5g0I6QNcZ4"..., 4096) = 4096
[pid 31774] read(5, "iv9kuXclVzDAGySj4dzp30d8tbQk\nCAU"..., 4096) = 4096
...<stripped for clarity>...
[pid 31774] read(5, "ye8\nFVdMpEbB4IMeDExNH08GGeL5qPQ6"..., 4096) = 4096
[pid 31774] read(5, "VVNUIEVs\nZWt0cm9uaWsgU2VydGlmaWt"..., 4096) = 4004
[pid 31774] read(5, "", 4096)           = 0
[pid 31774] close(5)                    = 0
[pid 31774] munmap(0x7f6dc8266000, 4096) = 0

这段重复了很多次！有两行非常有意思。

open("/etc/ssl/certs/ca-certificates.crt", O_RDONLY) = 5
这行意味着是跟修改的配置（上面提到的修改）有关的操作，

mmap(NULL, 1048576, PROT_READ|PROT_WRITE, MAP_PRIVATE|MAP_ANONYMOUS, -1, 0) = 0x7f6c927c3000
这行意味着在read过程中间请求内核分配 1M 内存空间。

在 strace 的输出中，另一个有意思的细节是分配的内存从来没有执行munmap进行释放。注意在调用close后0x7f6dc8266000才被传入munmap。

这些事实让我相信 ，当设置lua_ssl_trusted_certificate这条指令后，Nginx 发生了 内存泄露（尽管我对底层调试几乎没有任何经验）。什么？Nginx 发生了内存泄露，难道那还不让人兴奋？！不要这么兴奋。

为了找出是Nginx 的哪个组件发生了内存泄露，我决定使用 gdb。如果编译程序的时候打开了调试符号选项，gdb将会非常有用。如上所述，我使用的是 Nginx Openresty 套件， 需要使用下面的命令开启调试符号选项重新编译：

~/openresty-1.11.2.2 $ ./configure -j2 --with-debug --with-openssl=../openssl-1.0.2j/ --with-openssl-opt="-d no-asm -g3 -O0 -fno-omit-frame-pointer -fno-inline-functions"
--with-openssl-opt="-d no-asm -g3 -O0 -fno-omit-frame-pointer -fno-inline-functions" 确保 OpenSSL 编译的时候也开启调试符号信息。现在已经在Openresty的可执行程序中带有了调试符号信息，能通过gdb启动运行、找到上面提到的触发mmap的具体的调用函数。

首先我们需要启动gdb调试 Openresty 可执行程序：

sudo gdb `which openresty`
这个命令将打开gdb命令行，像下面这样：


GNU gdb (Ubuntu 7.7.1-0ubuntu5~14.04.2) 7.7.1
Copyright (C) 2014 Free Software Foundation, Inc.
License GPLv3+: GNU GPL version 3 or later <http://gnu.org/licenses/gpl.html>
This is free software: you are free to change and redistribute it.
There is NO WARRANTY, to the extent permitted by law.  Type "show copying"
and "show warranty" for details.
This GDB was configured as "x86_64-linux-gnu".
Type "show configuration" for configuration details.
For bug reporting instructions, please see:
<http://www.gnu.org/software/gdb/bugs/>.
Find the GDB manual and other documentation resources online at:
<http://www.gnu.org/software/gdb/documentation/>.
For help, type "help".
Type "apropos word" to search for commands related to "word"...
Reading symbols from /usr/local/openresty/bin/openresty...done.
(gdb)

接下来，设置程序的命令行参数

(gdb) set args -p `pwd` -c nginx.conf
这将使gdb在启动 Opneresty/Nginx 的时候把给出的命令行参数传递过去。接着配置断点，使其能够暂停程序到某一个文件的某一行或者是某一个函数。因为我想找出在open打开信任的验证文件后，那个令人奇怪的mmap的调用者，所以我首先添加了一个断点在

open("/etc/ssl/certs/ca-certificates.crt", O_RDONLY) = 5
断点设置如下：

break open if strcmp($rdi, "/etc/ssl/certs/ca-certificates.crt") == 0
如果你先前没有了解过gdb，gdb 是非常棒的工具，可以使用它添加一个自定义的条件来创建复杂的断点。这里我们告诉gdb暂停程序，如果open函数被调用并且rdi寄存器指向的数据是 /etc/ssl/certs/ca-certificates.crt 。我不知道是否还有更好的方式，我是在反复尝试后，发现open函数的第一个参数（文件路径）保存在了rdi寄存器，所以才会如此设置断点。现在告诉gdb运行程序：

```
(gdb) run
```
第一次出现open("/etc/ssl/certs/ca-certificates.crt", O_RDONLY)调用时，gdb将会暂停程序执行。现在我们可以使用其他的gdb辅助命令观察此刻程序的内部状态。下面是程序执行到断点的时候的内部状态：

```
Breakpoint 1, open64 () at ../sysdeps/unix/syscall-template.S:81
81  ../sysdeps/unix/syscall-template.S: No such file or directory.
(gdb) bt
#0  open64 () at ../sysdeps/unix/syscall-template.S:81
#1  0x00007ffff6a3dec8 in _IO_file_open (is32not64=8, read_write=8, prot=438, posix_mode=<optimized out>, filename=0x7fffffffdb00 "\346\f\362\367\377\177", fp=0x7ffff7f28a10) at fileops.c:228
#2  _IO_new_file_fopen (fp=fp@entry=0x7ffff7f28a10, filename=filename@entry=0x7ffff7f20ce6 "/etc/ssl/certs/ca-certificates.crt", mode=<optimized out>, mode@entry=0x6fb62d "r", is32not64=is32not64@entry=1) at fileops.c:333
#3  0x00007ffff6a323d4 in __fopen_internal (filename=0x7ffff7f20ce6 "/etc/ssl/certs/ca-certificates.crt", mode=0x6fb62d "r", is32=1) at iofopen.c:90
#4  0x00000000005b3fd2 in file_fopen (filename=0x7ffff7f20ce6 "/etc/ssl/certs/ca-certificates.crt", mode=0x6fb62d "r") at bss_file.c:164
#5  0x00000000005b3fff in BIO_new_file (filename=0x7ffff7f20ce6 "/etc/ssl/certs/ca-certificates.crt", mode=0x6fb62d "r") at bss_file.c:172
#6  0x00000000005e8ad3 in X509_load_cert_crl_file (ctx=0x7ffff7f289e0, file=0x7ffff7f20ce6 "/etc/ssl/certs/ca-certificates.crt", type=1) at by_file.c:251
#7  0x00000000005e8626 in by_file_ctrl (ctx=0x7ffff7f289e0, cmd=1, argp=0x7ffff7f20ce6 "/etc/ssl/certs/ca-certificates.crt", argl=1, ret=0x0) at by_file.c:115
#8  0x00000000005e5747 in X509_LOOKUP_ctrl (ctx=0x7ffff7f289e0, cmd=1, argc=0x7ffff7f20ce6 "/etc/ssl/certs/ca-certificates.crt", argl=1, ret=0x0) at x509_lu.c:120
#9  0x00000000005dd5c1 in X509_STORE_load_locations (ctx=0x7ffff7f28750, file=0x7ffff7f20ce6 "/etc/ssl/certs/ca-certificates.crt", path=0x0) at x509_d2.c:94
#10 0x0000000000546e22 in SSL_CTX_load_verify_locations (ctx=0x7ffff7f27fd0, CAfile=0x7ffff7f20ce6 "/etc/ssl/certs/ca-certificates.crt", CApath=0x0) at ssl_lib.c:3231
#11 0x0000000000477d94 in ngx_ssl_trusted_certificate (cf=cf@entry=0x7fffffffe150, ssl=0x7ffff7f27a78, cert=cert@entry=0x7ffff7f22f20, depth=<optimized out>) at src/event/ngx_event_openssl.c:687
#12 0x00000000004f0a1b in ngx_http_lua_set_ssl (llcf=0x7ffff7f22ef8, cf=0x7fffffffe150) at ../ngx_lua-0.10.7/src/ngx_http_lua_module.c:1240
#13 ngx_http_lua_merge_loc_conf (cf=0x7fffffffe150, parent=0x7ffff7f15808, child=0x7ffff7f22ef8) at ../ngx_lua-0.10.7/src/ngx_http_lua_module.c:1158
#14 0x000000000047e2b1 in ngx_http_merge_servers (cmcf=<optimized out>, cmcf=<optimized out>, ctx_index=<optimized out>, module=<optimized out>, cf=<optimized out>) at src/http/ngx_http.c:599
#15 ngx_http_block (cf=0x7fffffffe150, cmd=0x0, conf=0x1b6) at src/http/ngx_http.c:269
#16 0x0000000000460b5b in ngx_conf_handler (last=1, cf=0x7fffffffe150) at src/core/ngx_conf_file.c:427
#17 ngx_conf_parse (cf=cf@entry=0x7fffffffe150, filename=filename@entry=0x7ffff7f0b9e8) at src/core/ngx_conf_file.c:283
#18 0x000000000045e2f1 in ngx_init_cycle (old_cycle=old_cycle@entry=0x7fffffffe300) at src/core/ngx_cycle.c:274
#19 0x000000000044cef4 in main (argc=<optimized out>, argv=<optimized out>) at src/core/nginx.c:276

```

真令人兴奋，gdb向我们展示了完整的函数调用栈及参数！查看此刻寄存器中的数据，可以用 info registers命令。为了更好的理解调用栈，我查看了一下 Nginx的内部工作流程（我记得Openresty仅仅是组装了一些额外的模块的Nginx）。Nginx 内部所有的（除了Nginx 核心）都被实现为模块，这些模块注册 handlers 和 filters。Nginx 的配置文件主要有三个主要的块组成，分别是main、server、location。 假设您的自定义Nginx模块引入了一个新的配置指令，那么您还需要注册一个处理程序（handler）来处理该指令的配置的值。因此整个过程如下 Nginx 解析配置文件，每一个配置部分解析后就会调用注册的相应处理程序。下面是lua-nginx-module（Openresty Nginx 组件的核心模块）的实现：

```
ngx_http_module_t ngx_http_lua_module_ctx = {
#if (NGX_HTTP_LUA_HAVE_MMAP_SBRK)                                            \
    && (NGX_LINUX)                                                           \
    && !(NGX_HTTP_LUA_HAVE_CONSTRUCTOR)
    ngx_http_lua_pre_config,          /*  preconfiguration */
#else
    NULL,                             /*  preconfiguration */
#endif
    ngx_http_lua_init,                /*  postconfiguration */

    ngx_http_lua_create_main_conf,    /*  create main configuration */
    ngx_http_lua_init_main_conf,      /*  init main configuration */

    ngx_http_lua_create_srv_conf,     /*  create server configuration */
    ngx_http_lua_merge_srv_conf,      /*  merge server configuration */

    ngx_http_lua_create_loc_conf,     /*  create location configuration */
    ngx_http_lua_merge_loc_conf       /*  merge location configuration */
};
```

这里是 Nginx 模块注册的处理程序。从注释中你也可以看到，Nginx 解析出来一个 location 配置 就会调用 ngx_http_lua_merge_loc_conf 将配置和 main 块合并。回到我们的上面的gdb输出,可以看到#13就是这个函数调用。默认情况下对于每一个 location 块配置这个函数将会被调用。通过源码我们可以看到这个函数直接去读去配置值、继承server中的配置条目、设置默认值。如果设置了lua_ssl_trusted_certificate 指令，可以看到其中调用了ngx_http_lua_set_ssl,在其内部又调用了Nginx SSL 模块的 ngx_ssl_trusted_certificate。ngx_ssl_trusted_certificate 是一个非常简单的函数，对于给定的配置块（一个location 块），设置SSL 环境(context)的验证深度，调用另外一个 OpenSSL API 加载验证文件（还有一些错误处理）。

```
0649 ngx_int_t
0650 ngx_ssl_trusted_certificate(ngx_conf_t *cf, ngx_ssl_t *ssl, ngx_str_t *cert,
0651     ngx_int_t depth)
0652 {
0653     SSL_CTX_set_verify_depth(ssl->ctx, depth);
0654
0655     if (cert->len == 0) {
0656         return NGX_OK;
0657     }
0658
0659     if (ngx_conf_full_name(cf->cycle, cert, 1) != NGX_OK) {
0660         return NGX_ERROR;
0661     }
0662
0663     if (SSL_CTX_load_verify_locations(ssl->ctx, (char *) cert->data, NULL)
0664         == 0)
0665     {
0666         ngx_ssl_error(NGX_LOG_EMERG, ssl->log, 0,
0667                       "SSL_CTX_load_verify_locations(\"%s\") failed",
0668                       cert->data);
0669         return NGX_ERROR;
0670     }
0671
0672     /*
0673      * SSL_CTX_load_verify_locations() may leave errors in the error queue
0674      * while returning success
0675      */
0676
0677     ERR_clear_error();
0678
0679     return NGX_OK;
0680 }
```

Nginx SSL 模块的完整代码在这里能找到。

现在我们已经走到调用栈的一半了，并且走出了 Nginx的世界。下一个函数调用是SSL_CTX_load_verify_locations，来自于 OpenSSL。程序在这里程序打开了信任的验证文件，并且暂停。接下来将会读取文件（根据上面的strace输出）。

由于我最初的目的就是找出是谁调用了令人奇怪的mmap 调用，很自然的下一个断点就是:
```
(gdb) b mmap
```
b是break的简写。(gdb) c将会继续程序的执行。程序暂停在了下一个断点：

```
Breakpoint 3, mmap64 () at ../sysdeps/unix/syscall-template.S:81
81  ../sysdeps/unix/syscall-template.S: No such file or directory.
(gdb) bt
#0  mmap64 () at ../sysdeps/unix/syscall-template.S:81
#1  0x00007ffff6a44ad2 in sysmalloc (av=0x7ffff6d82760 <main_arena>, nb=48) at malloc.c:2495
#2  _int_malloc (av=0x7ffff6d82760 <main_arena>, bytes=40) at malloc.c:3800
#3  0x00007ffff6a466c0 in __GI___libc_malloc (bytes=40) at malloc.c:2891
#4  0x000000000057d829 in default_malloc_ex (num=40, file=0x6f630f "a_object.c", line=350) at mem.c:79
#5  0x000000000057deb9 in CRYPTO_malloc (num=40, file=0x6f630f "a_object.c", line=350) at mem.c:346
<internal OpenSSL function calls stripped for clarity>
#30 0x000000000065e2f7 in PEM_X509_INFO_read_bio (bp=0x7ffff7f28c50, sk=0x0, cb=0x0, u=0x0) at pem_info.c:248
#31 0x00000000005e8b22 in X509_load_cert_crl_file (ctx=0x7ffff7f289e0, file=0x7ffff7f20ce6 "/etc/ssl/certs/ca-certificates.crt", type=1) at by_file.c:256
#32 0x00000000005e8626 in by_file_ctrl (ctx=0x7ffff7f289e0, cmd=1, argp=0x7ffff7f20ce6 "/etc/ssl/certs/ca-certificates.crt", argl=1, ret=0x0) at by_file.c:115
#33 0x00000000005e5747 in X509_LOOKUP_ctrl (ctx=0x7ffff7f289e0, cmd=1, argc=0x7ffff7f20ce6 "/etc/ssl/certs/ca-certificates.crt", argl=1, ret=0x0) at x509_lu.c:120
#34 0x00000000005dd5c1 in X509_STORE_load_locations (ctx=0x7ffff7f28750, file=0x7ffff7f20ce6 "/etc/ssl/certs/ca-certificates.crt", path=0x0) at x509_d2.c:94
#35 0x0000000000546e22 in SSL_CTX_load_verify_locations (ctx=0x7ffff7f27fd0, CAfile=0x7ffff7f20ce6 "/etc/ssl/certs/ca-certificates.crt", CApath=0x0) at ssl_lib.c:3231
#36 0x0000000000477d94 in ngx_ssl_trusted_certificate (cf=cf@entry=0x7fffffffe150, ssl=0x7ffff7f27a78, cert=cert@entry=0x7ffff7f22f20, depth=<optimized out>) at src/event/ngx_event_openssl.c:687
#37 0x00000000004f0a1b in ngx_http_lua_set_ssl (llcf=0x7ffff7f22ef8, cf=0x7fffffffe150) at ../ngx_lua-0.10.7/src/ngx_http_lua_module.c:1240
#38 ngx_http_lua_merge_loc_conf (cf=0x7fffffffe150, parent=0x7ffff7f15808, child=0x7ffff7f22ef8) at ../ngx_lua-0.10.7/src/ngx_http_lua_module.c:1158
#39 0x000000000047e2b1 in ngx_http_merge_servers (cmcf=<optimized out>, cmcf=<optimized out>, ctx_index=<optimized out>, module=<optimized out>, cf=<optimized out>) at src/http/ngx_http.c:599
<Nginx function calls stripped for clarity>
```

此刻我异常兴奋。我“发现”了一个OpenSSL内存泄露！带着异常兴奋的情绪，我开始阅读理解 上个世纪90年代就开发的 OpenSSL 的代码。如此高兴，接下来的几天几夜去理解这写函数并且试图找到我非常确定的函数中的内存泄露。看了许多给 OpenSSL 的内存泄露bug（尤其是和上面这个函数相关的）后，我信心大增，因此我有花了几天几夜去捉这个臭虫！

基本上这些函数做的事情是 首先打开受信任的证书文件，分配缓冲（4096字节），从文件中读取 4KB 内容到缓冲区，解密数据，转换成 OpenSSL 的内部表示，保存到给定的SSL context的证书存储区（这个属于一个 location 块上下文环境）。因此以后无论何时，在这个location块中，当Nginx需要验证SSL 客户端证书的时候，都将会调用OpneSSL 中的SSL_get_verify_result传递开始保存保存的 SSL context。接着那个函数将会使用已经加载的和内部初始化的受信任证书验证客户端。

这就是日日夜夜学习的那些所有的事情如何在一起工作的收获，但是没有发现一个bug。

也了解到mmap是被在CRYPTO_malloc 触发的malloc调用的，CRYPTO_malloc是另一个OpenSSL 函数，用来扩展证书存储大小，使其可以适应解密和内部初始化的证书数据。现在我已经知道究竟发生了什么，其不会释放所分配的内存，因为OpenSSL在这个进程生命周期中的后面可能会使用。

但是这个主要的问题 ，当lua_ssl_trusted_certificate 指令配置后，为什 么 Nginx 消耗的内存增长如此之快，还是一个谜。

从我手中掌握的已有数据来看是每个 location 块中的 mmap导致了这个问题。现在我决定提出 Openresty/Nginx 中的相关代码，用相同的 OpenSSL API 写一个独立C程序加载配置文件。

反复调用模拟多个 location 块（我这里是5000个）:

```
#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <stdint.h>
#include <sys/mman.h>
#include <openssl/ssl.h>
#include <malloc.h>
#include <unistd.h>

void read_cert() {
        const char ca_bundlestr[] = "/etc/ssl/certs/ca-certificates.crt";

        BIO               *outbio = NULL;
        int ret;

        SSL_CTX *ctx;

        outbio  = BIO_new_fp(stdout, BIO_NOCLOSE);

        SSL_library_init();
        SSL_load_error_strings();
        OpenSSL_add_all_algorithms();

        ctx = SSL_CTX_new(SSLv23_method());

        SSL_CTX_set_mode(ctx, SSL_MODE_RELEASE_BUFFERS);
        SSL_CTX_set_mode(ctx, SSL_MODE_NO_AUTO_CHAIN);
        SSL_CTX_set_read_ahead(ctx, 1);

        ret = SSL_CTX_load_verify_locations(ctx, ca_bundlestr, NULL);
        if (ret == 0)
                BIO_printf(outbio, "SSL_CTX_load_verify_locations failed");

        BIO_free_all(outbio);
        SSL_CTX_free(ctx);
}


int main() {
        int i = 0;
        for (i = 0; i < 5000; i++) {
                read_cert();
                //malloc_trim(0);
        }
        malloc_stats();
}

如果我能解决这里的问题，我就能解决 Openresty/Nginx 中的问题，由于这是等价于原问题的。但是猜猜发生了什么，strace 的输出跟我预期的不同！

...
read(3, "fqaEQn6/Ip3Xep1fvj1KcExJW4C+FEaG"..., 4096) = 4096
read(3, "IYWxvemF0Yml6dG9u\nc2FnaSBLZnQuMR"..., 4096) = 4096
read(3, "nVz\naXR2YW55a2lhZG8xHjAcBgkqhkiG"..., 4096) = 4096
read(3, "A\nMIIBCgKCAQEAy0+zAJs9Nt350Ulqax"..., 4096) = 4096
read(3, "MRAwDgYDVQQHEwdDYXJhY2FzMRkwFwYD"..., 4096) = 4096
read(3, "OR1YqI0JDs3G3eicJlcZaLDQP9nL9bFq"..., 4096) = 4096
read(3, "E7zelaTfi5m+rJsziO+1ga8bxiJTyPbH"..., 4096) = 4096
read(3, "Xtdj182d6UajtLF8HVj71lODqV0D1VNk"..., 4096) = 4096
read(3, "AAOCAQ8AMIIBCgKCAQEAt49VcdKA3Xtp"..., 4096) = 4096
brk(0x1cfb000)                          = 0x1cfb000
read(3, "396gwpEWoGQRS0S8Hvbn+mPeZqx2pHGj"..., 4096) = 4096
read(3, "QYwDwYDVR0T\nAQH/BAUwAwEB/zANBgkq"..., 4096) = 4096
read(3, "ETzsemQUHS\nv4ilf0X8rLiltTMMgsT7B"..., 4096) = 4096
read(3, "wVU3RhYXQgZGVyIE5lZGVybGFuZGVuMS"..., 4096) = 4096
read(3, "N/uLicFZ8WJ/X7NfZTD4p7dN\ndloedl4"..., 4096) = 4096
read(3, "fzDtgUx3M2FIk5xt/JxXrAaxrqTi3iSS"..., 4096) = 4096
read(3, "sO+wmETRIjfaAKxojAuuK\nHDp2KntWFh"..., 4096) = 4096
read(3, "8z+uJGaYRo2aWNkkijzb2GShROfyQcsi"..., 4096) = 4096
read(3, "CydAXFJy3SuCvkychVSa1ZC+N\n8f+mQA"..., 4096) = 4096
...
```

brk 调用后面没有 mmap 调用，内存消耗也没有按照超出预期的增长！

好吧，我现在非常恼火也想放弃。但是我我的好奇心没让我放弃。我决定了解更多的关于内存分配如何工作的。

通常来说当程序中申请更多的内存的时候会调用glibc中的malloc(或者改版)。对于用户空间的程序，glibc抽象了很多内存管理的工作、提供了一个使用虚拟内存的 API 。

默认情况下，当一个程序调用malloc的申请更多的堆上内存时候，将会使用brk申请需要的内存空间。如果堆上有洞，brk将不能正常工作。

现在假设你有1G的堆上内存空闲空间。在上面直接创建一个洞，可以使用mmap指定具体地址A 这种方式，指定内存空间大小。这样mmap就会从堆上的内存地址A开始，申请指定大小的内存空间。

但是因为程序中断点还在堆开始的地方,这时如果使用sbrk函数申请的B > A 字节大小的内存空间，此次请求将会失败，因为brk尝试申请的一部分内存区域已经被分配（洞）。这时候malloc会使用mmap代替申请内存空间。

因为mmap调用代价非常高，为了降低其调用次数，malloc 申请 1M内存即使申请分配的内存不足1M。https://code.woboq.org/userspace/glibc/malloc/malloc.c.html#406 注释文档中也有记载。你会发现上面的输出日志中，令人奇怪的mmap调用申请1048576字节内存，正好是1M–当brk失败后，malloc使用此默认值去调用mmap。

高潮来了！！！把这些线索放一起。一个明显的猜想是 brk 调用后面是mmap调用在Openresty 上下文环境中，但是在独立的c中却不是，因为 Openresty 在配置文件加载之前 在某个地方创建了一个洞。

这不难验证，使用grep 命令在PRs,issus和lua-nginx-module源码中查找。最后发现Luajit 需要工作在低地址空间获得更高的效率，这是为什么lua-nginx-module那群家伙决定在程序开始执行之前执行下面这段代码：
```
if (sbrk(0) < (void *) 0x40000000LL) {
    mmap(ngx_align_ptr(sbrk(0), getpagesize()), 1, PROT_READ,
         MAP_FIXED|MAP_PRIVATE|MAP_ANON, -1, 0);
}
```

完整代码可以在仓库中找到。现在我还没太弄明白这段代码是如何让luajit拥有低地址空间的（如果有人能在评论里面解释清楚，我将非常感激），但是这确实是导致这个问题的代码。

为了证明，我拷贝出来这段代码到我的 独立 C 程序中：

```
#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <stdint.h>
#include <sys/mman.h>
#include <openssl/ssl.h>
#include <malloc.h>
#include <unistd.h>

#define ngx_align_ptr(p, a) \
        (u_char *) (((uintptr_t) (p) + ((uintptr_t) a - 1)) & ~((uintptr_t) a - 1))

ngx_http_lua_limit_data_segment(void) {
        if (sbrk(0) < (void *) 0x40000000LL) {
                mmap(ngx_align_ptr(sbrk(0), getpagesize()), 1, PROT_READ,
                                MAP_FIXED|MAP_PRIVATE|MAP_ANON, -1, 0);
        }
}

void read_cert() {
        const char ca_bundlestr[] = "/etc/ssl/certs/ca-certificates.crt";

        BIO               *outbio = NULL;
        int ret;

        SSL_CTX *ctx;

        outbio  = BIO_new_fp(stdout, BIO_NOCLOSE);

        SSL_library_init();
        SSL_load_error_strings();
        OpenSSL_add_all_algorithms();

        ctx = SSL_CTX_new(SSLv23_method());

        SSL_CTX_set_mode(ctx, SSL_MODE_RELEASE_BUFFERS);
        SSL_CTX_set_mode(ctx, SSL_MODE_NO_AUTO_CHAIN);
        SSL_CTX_set_read_ahead(ctx, 1);

        ret = SSL_CTX_load_verify_locations(ctx, ca_bundlestr, NULL);
        if (ret == 0)
                BIO_printf(outbio, "SSL_CTX_load_verify_locations failed");

        BIO_free_all(outbio);
        SSL_CTX_free(ctx);
}


int main() {
        ngx_http_lua_limit_data_segment();
        int i = 0;
        for (i = 0; i < 5000; i++) {
                read_cert();
                //malloc_trim(0);
        }
        malloc_stats();
        usleep(1000 * 60);
}
```

当我编译运行这段程序的时候，通过strace我能看到和Openresty环境中相同的行为。为了更进一步的确认，我编辑Opneresty的源码、注释掉ngx_http_lua_limit_data_segment、重新编译运行，内存增长的现象没有发生。

搞定！！！

上面就是我这次的收获。根据这次结果，我提交了一个issue。当你有很多的location 块的时候，这真的会成为一个问题。例如加入你有一个很大的 Nginx 配置文件，里面有超过4k 个location 块，然后你加入了lua_ssl_trusted_certificate指令到 mian 配置块，然后当你 reload/restart/start Nginx 的时候，内存消耗将会增长到~4G(4k * 1MB)并且不会释放。
