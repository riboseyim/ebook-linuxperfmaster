# Linux Commands - Overview and Examples

>The command line is one of the most powerful features of Linux. There exists a sea of Linux command line tools, allowing you to do almost everything you can think of doing on your Linux PC. However, this usually creates a problem: with so many commands available to use, you don't know where and how to start learning them, especially when you are beginner.


#### Author
**[Himanshu Arora](https://www.linkedin.com/in/himanshu-arora-89b6365/)**：
印度理工学院、伊利诺伊大学香槟分校；软件开发工程师。

[原文：https://www.howtoforge.com/linux-commands/](https://www.howtoforge.com/linux-commands/)

本文的特点是非常简洁，将繁杂的Linux命令行筛选出100条左右，非常适合入门学习。
此外，将领域知识以“条目+示例”的方式来整理，类似编字典一样，在编辑的过程中可以促进学习者加深认识，也方便日后持续改进（增加注解、参考文献、索引等），是一种不错的学习方法。
最后，整理这些命令行的时候，我体会到操作系统最重要的工作实际就是对文件的管理，创建、移动、查看、编辑、销毁、检索，都是围绕文件的操作，事实上也是实际工作中使用最频繁的需求。对开发者来说，以Linux命令行为模版，命名风格、人机交互、小而美的实现方式，促进自己在其它领域的应用、提高大有裨益。


<!--more-->

#### Adduser/Addgroup

[分类：权限管理；增加用户、用户组](#)

The adduser and addgroup commands lets you add a new user and group to a system, respectively. Here's an example for adduser:
```
$ sudo adduser testuser
Adding user `testuser' ...
Adding new group `testuser' (1003) ...
Adding new user `testuser' (1003) with group `testuser' ...
Creating home directory `/home/testuser' ...
Copying files from `/etc/skel' ...
Enter new UNIX password:
```

#### Arch

[分类：系统信息；查看CPU架构](#)

The arch command is used to print the machine's architecture. For example:
```
$ arch
i686
Not sure what 'i686' means? Head here.
```
#### Cal/Ncal

[分类：系统信息；查看日历](#)

The cal and ncal commands display a calendar in the output.
```
$ cal
March 2017
Su Mo Tu We Th Fr Sa
1 2 3 4
5 6 7 8 9 10 11
12 13 14 15 16 17 18
19 20 21 22 23 24 25
26 27 28 29 30 31

$ ncal
March 2017
Su 5 12 19 26
Mo 6 13 20 27
Tu 7 14 21 28
We 1 8 15 22 29
Th 2 9 16 23 30
Fr 3 10 17 24 31
Sa 4 11 18 25
```

#### Cat
[分类：文件管理；查看文件内容](#)
The cat command allows you to concatenate files, or data provided on standard input, and print it on the standard output. In layman terms, the command prints the information provided to it, whether through stdin or in the form a file.
```
$ cat test.txt
Hello...how are you?
```

#### Cd
[分类：文件管理；切换工作目录](#)
The cd command is used to change user's present working directory.
```
$ cd /home/himanshu/
```

#### Chgrp
[分类：文件管理、权限管理；切换文件所属组](#)
The chgrp command allows you to change the group ownership of a file. The command expects new group name as its first argument and the name of file (whose group is being changed) as second argument.
```
$ chgrp howtoforge test.txt
```

#### Chmod
[分类：文件管理、权限管理；切换文件执行权限](#)
The chmod command lets you change access permissions for a file. For example, if you have a binary file (say helloWorld), and you want to make it executable, you can run the following command:
```
chmod +x helloWorld
```

#### Chown
[分类：文件管理、权限管理；切换文件所有者](#)
The chown command allows you to change the ownership and group of a file. For example, to change the owner of a file test.txt to root, as well as set its group as root, execute the following command:
```
chown root:root test.txt
```

#### Cksum
[分类：文件管理；查看文件属性](#)
The cksum command prints the CRC checksum and byte count for the input file.
```
$ cksum test.txt
3741370333 20 test.txt
Not sure what checksum is? Head here.
```

#### Clear
[分类：人机交互；清屏](#)
The clear command is used to clear the terminal screen.
```
$ clear
```

#### Cmp
[分类：文件管理；文件比对 byte-by-byte](#)
The cmp command is used to perform byte-by-byte comparison of two files.
```
$ cmp file1 file2
file1 file2 differ: byte 1, line 1
```


#### Comm
[分类：文件管理；文件比对](#)
The comm command is used to compare two sorted files line-by-line. For example, if 'file1' contains numbers 1-5 and 'file2' contains number 4-8, here's what the 'comm' command produces in this case:
```
$ comm file1 file2
```
支持选项：
```
-1：不显示在第一个文件出现的内容；
-2：不显示在第二个文件中出现的内容；
-3：不显示同时在两个文件中都出现的内容。
```

#### Cp
[分类：文件管理；文件复制](#)
The cp command is used for copying files and directories.
```
$ cp test.txt /home//himanshu/Desktop/
```

#### Csplit
[分类：文件管理；待补充内容](#)
The csplit command lets you split a file into sections determined by context lines. For example, to split a file into two where the first part contains 'n-1' lines and the second contains the rest, use the following command:

```
$ csplit file1 [n]
```

The two parts are saved as files with names 'xx00' and 'xx01', respectively.

#### Date
[分类：系统信息；查看系统时间](#)
The date command can be used to print (or even set) the system date and time.
```
$ date
Tue Feb 28 17:14:57 IST 2017
```

#### Dd
[分类：文件管理；待补充内容](#)
The dd command copies a file, converting and formatting according to the operands. For example, the following command creates an image of /dev/sda partition.
```
dd if=/dev/sda of=/tmp/dev-sda-part.img
```

#### Df
[分类：文件管理；查看文件系统利用率](#)
The df command displays the file system disk space usage in output.
```
$ df /dev/sda1
Filesystem 1K-blocks Used     Available Use% Mounted on
/dev/sda1  74985616  48138832 23014620  68%     /
```

#### Diff
[分类：文件管理；文件比对 line-by-line](#)
The diff command lets you compare two files line by line.
```
$ diff file1 file2
```

#### Diff3
[分类：文件管理；文件比对,三个文件](#)
The diff3 command, as the name suggests, allows you to compare three files line by line.
```
diff3 file1 file2 file3
```

#### Dir
[分类：文件管理；查看当前目录文件列表](#)
The dir command lists directory contents. For example:
```
$ dir
test1 test2 test.7z test.zip
```

#### Dirname
[分类：文件管理；查看当前目录](#)
The dirname command strips last component from a file name/path. In layman's terms, you can think of it as a tool that, for example, removes file name from the file's absolute path.
```
$ dirname /home/himanshu/file1
/home/himanshu
```

#### Dmidecode
[分类：系统信息；查看硬件信息](#)

The dmidecode command prints a system's DMI (aka SMBIOS) table contents in a human-readable format.
```
$ sudo dmidecode
# dmidecode 2.12
SMBIOS 2.6 present.
50 structures occupying 2056 bytes.
Table at 0x000FCCA0.
Handle 0x0000, DMI type 0, 24 bytes
BIOS Information
Vendor: American Megatrends Inc.
Version: 080015
Release Date: 08/22/2011
...
...
...
```
DMI (Desktop Management Interface, DMI)就是帮助收集电脑系统信息的管理系统，DMI信息的收集必须在严格遵照SMBIOS规范的前提下进行。 SMBIOS(System Management BIOS)是主板或系统制造者以标准格式显示产品管理信息所需遵循的统一规范。SMBIOS和DMI是由行业指导机构Desktop Management Task Force (DMTF)起草的开放性的技术标准，其中DMI设计适用于任何的平台和操作系统。

#### Du
[分类：文件管理；查看指定目录磁盘利用率](#)
The du command displays disk usage of files present in a directory as well as its sub-directories.
```
$ du /home/himanshu/Desktop/
92 /home/himanshu/Desktop/Downloads/meld/meld/ui
88 /home/himanshu/Desktop/Downloads/meld/meld/vc
56 /home/himanshu/Desktop/Downloads/meld/meld/matchers
12 /home/himanshu/Desktop/Downloads/meld/meld/__pycache__
688 /home/himanshu/Desktop/Downloads/meld/meld
16 /home/himanshu/Desktop/Downloads/meld/bin
328 /home/himanshu/Desktop/Downloads/meld/data/ui
52 /home/himanshu/Desktop/Downloads/meld/data/icons/svg
```

#### Echo

The echo command displays whatever input text is given to it.
```
$ echo hello hi
hello hi
```

#### Ed
[分类：文件管理；编辑器](#)
ed is a line-oriented text editor.
```
$ ed
```
单行纯文本编辑器，它有命令模式（command mode）和输入模式（input mode）两种工作模式。
支持选项：
```
 A：切换到输入模式，在文件的最后一行之后输入新的内容；
 C：切换到输入模式，用输入的内容替换掉最后一行的内容；
 i：切换到输入模式，在当前行之前加入一个新的空行来输入内容；
 d：用于删除最后一行文本内容；
 n：用于显示最后一行的行号和内容；
 w：<文件名>：一给定的文件名保存当前正在编辑的文件；
 q：退出ed编辑器。
```

#### Eject
[分类：媒体管理；卸载](#)
The eject command lets you eject removable media (typically, a CD ROM or floppy disk)
```
$ eject
```

#### Env
[分类：系统信息；查看用户环境变量](#)
The env command not only displays the current environment, but also lets you edit it.
```
$ env
```

#### Exit
[分类：交互；退出](#)
The exit command causes the shell to exit.
```
$ exit
```

#### Expand
[分类：文件管理；编辑器；将TAB符替换为空格符](#)
The expand command converts tabs present in the input file(s) into spaces, and writes the file contents to standard output.
```
$ expand file1
```

#### Expr
[分类：计算器；表达式](#)
The expr command evaluates expressions. For example:
```
$ expr 1 + 2
3
```

#### Factor
[分类：计算器；分解质因数](#)
The factor command prints the prime factors of the input number.
```
$ factor 135
135: 3 3 3 5
```

#### Fgrep
[分类：文件管理；搜索；匹配指定文件字符](http://man.linuxde.net/fgrep)

The fgrep command is equivalent to the grep command when executed with the -F command line option. The tool is also known as fixed or fast grep as it doesn't treat regular expression metacharacters as special, processing the information as simple string instead.

For example, if you want to search for dot (.) in a file, and don't want grep to interpret it as a wildcard character, use fgrep in the following way:
```
$ fgrep "." [file-name]
```

#### Find
[分类：文件管理；搜索；](#)
The find command lets you search for files in a directory as well as its sub-directories.
```
$ find test*
test
test1
test2
test.7z
test.c
test.txt
More examples for the Linux Find command:

* 14 Practical Examples of Linux Find Command for Beginners
* Searching For Files And Folders With The find Command
* Finding Files On The Command Line
```

#### Fmt
[分类：文件管理；读取文件内容并格式化输出（查看支持选项）](#)
fmt is a simple optimal text formatter. It reformats each paragraph in the file passed to it, and writes the file contents to standard output.
```
$ fmt file1
```

#### Fold
[分类：交互；控制文件内容输出时所占用的屏幕宽度](#)

The fold command wraps each input line to fit in specified width.
```
$ fold -w 10
Hi my name is himanshu Arora
Hi my name
is himans
hu Arora
```

#### Free
[分类：系统信息；性能监测；查看内存利用情况。详细介绍 >>>more>>>](http://www.jianshu.com/p/fd6e35f529c1)
The free command displays the amount of free and used memory in the system.
```
$ free
       total           used  free   shared buffers cached
Mem:   1800032       1355288 444744 79440   9068   216236
-/+ buffers/cache: 1129984 670048
Swap:  1832956      995076  837880
```

[参考：基于Linux单机的负载评估](http://www.jianshu.com/p/db8e8a2884ef)
[参考：Netflix性能分析模型：In 60 Seconds](http://www.jianshu.com/p/fd6e35f529c1)


#### Grep
[分类：文件管理；搜索；](#)
The grep command searches for a specified pattern in a file (or files) and displays in output lines containing that pattern.
```
$ grep Hello test.txt
Hello...how are you?
More tutorials and examples for the Linux Grep command:

* How to use grep to search for strings in files on the shell
* How to perform pattern search in files using Grep
```

#### Groups
[分类：文件管理；搜索；](#)
The groups command displays the name of groups a user is part of.
```
$ groups himanshu
himanshu : himanshu adm cdrom sudo dip plugdev lpadmin sambashare
```

#### Gzip
[分类：文件管理；压缩](#)
The gzip command compresses the input file, replacing the file itself with one having a .gz extension.
```
$ gzip file1
```

#### Gunzip
[分类：文件管理；解压缩](#)
Files compressed with gzip command can be restored to their original form using the gunzip command.
```
$ gunzip file1.gz
```

#### Head
[分类：文件管理；查看文件](#)
The head command displays the first 10 lines of the file to standard output
```
$ head CHANGELOG.txt
BEEBEEP (Secure Lan Messanger)
BeeBEEP
2.0.4
- Some GUI improvements (new icons, file sharing tree load faster)
- Always Beep on new message arrived (option)
- Favorite users (right click on user and enable star button) is on top of the list
- improved group usability
- Offline users can be removed from list (right click on an offline user in list and then remove)
- Clear all files shared (option)
- Load minimized at startup (option)
```

#### Hostname
[分类：系统信息；host name](#)
The hostname command not only displays the system's host name, but lets them set it as well.
```
$ hostname
himanshu-desktop
```

#### Id
[分类：系统信息；用户信息](#)
The id command prints user and group information for the current user or specified username.
```
$ id himanshu
uid=1000(himanshu) gid=1000(himanshu) groups=1000(himanshu),4(adm),24(cdrom),27(sudo),30(dip),46(plugdev),108(lpadmin),124(sambashare)
```

#### Kill
[分类：进程管理；](#)
The kill command, as the name suggests, helps user kill a process by sending the TERM signal to it.
```
$ kill [process-id]
```

#### Killall
[分类：进程管理；](#)
The killall command lets you kill a process by name. Unlike kill - which requires ID of the process to be killed - killall just requires the name of the process.
```
$ killall nautilus
```

#### Last
[分类：安全管理；查看最近登录用户](#)
The last command shows listing of last logged in users.
```
$ last
himanshu pts/11 :0 Thu Mar 2 09:46 still logged in
himanshu pts/1 :0 Thu Mar 2 09:46 still logged in
himanshu :0 :0 Thu Mar 2 09:42 still logged in
reboot system boot 4.4.0-62-generic Thu Mar 2 09:41 - 10:36 (00:54)
himanshu pts/14 :0 Wed Mar 1 15:17 - 15:52 (00:35)
himanshu pts/13 :0 Wed Mar 1 14:40 - down (08:06)
```

#### Ldd
[分类：软件包管理；查看一个共享库的依赖](#)
The ldd command displays in output dependencies of a shared library.
```
$ ldd /lib/i386-linux-gnu/libcrypt-2.19.so
linux-gate.so.1 => (0xb77df000)
libc.so.6 => /lib/i386-linux-gnu/libc.so.6 (0xb75da000)
/lib/ld-linux.so.2 (0x80088000)
```

#### Ln
[分类：文件管理；链接](#)
The ln command is used for creating link between files. For example, the following command would create a link named 'lnk' to a file with name 'test.txt':
```
$ ln test.txt lnk
```

#### Locate
[分类：文件管理；搜索](#)
The locate command helps user find a file by name.
```
$ locate [file-name]
```

#### Logname
[分类：登录信息；](#)
The logname command prints the user-name of the current user.
```
$ logname
himanshu
```

#### Ls
[分类：文件管理；查看文件列表](#)
The ls command lists contents of a directory in output.
```
$ ls progress
capture.png hlist.o progress progress.h sizes.c
hlist.c LICENSE progress.1 progress.o sizes.h
hlist.h Makefile progress.c README.md sizes.o
```

#### Lshw
[分类：系统信息；查看硬件信息](#)
The lshw command extracts and displays detailed information on the hardware configuration of the machine.
```
$ sudo lshw
[sudo] password for himanshu:
himanshu-desktop
description: Desktop Computer
product: To Be Filled By O.E.M. (To Be Filled By O.E.M.)
vendor: To Be Filled By O.E.M.
version: To Be Filled By O.E.M.
serial: To Be Filled By O.E.M.
width: 32 bits
capabilities: smbios-2.6 dmi-2.6 smp-1.4 smp
...
...
..
```

#### Lscpu
[分类：系统信息；查看硬件信息-CPU ](#)
The lscpu command displays in output system's CPU architecture information (such as number of CPUs, threads, cores, sockets, and more).
```
$ lscpu
Architecture: i686
CPU op-mode(s): 32-bit, 64-bit
Byte Order: Little Endian
CPU(s): 1
On-line CPU(s) list: 0
Thread(s) per core: 1
Core(s) per socket: 1
Socket(s): 1
Vendor ID: AuthenticAMD
CPU family: 16
Model: 6
Stepping: 3
CPU MHz: 2800.234
BogoMIPS: 5600.46
Virtualization: AMD-V
L1d cache: 64K
L1i cache: 64K
L2 cache: 1024K
```

#### Man
[分类：帮助；](#)
man lets you access reference manual for commands, programs/utilities, as well as functions.
```
$ man ls
```

#### Md5sum
[分类：计算器；md5](#)
The md5sum command lets you print  or check MD5 (128-bit) checksums.
```
$ md5sum test.txt
ac34b1f34803a6691ff8b732bb97fbba test.txt
```

#### Mkdir
[分类：文件管理；创建目录](#)
The mkdir command lets you create directories.
```
$ mkdir [dir-name]
```

#### Mkfifo
[分类：进程管理](#)
The mkfifo command is used to create named pipes.
```
$ mkfifo [pipe-name]
```

#### More
[分类：交互](#)
more is basically a filter for paging through text one screenful at a time.
```
$ cat [large-file] | more
```

#### Mv
[分类：文件管理；移动](#)
The mv command lets you either move a file from one directory to another, or rename it.
```
$ mv test.txt /home/himanshu/Desktop/
```

#### Nice
[分类：进程管理；指定进程优先级](#)
The nice command lets you run a program with modified scheduling priority.
```
$ nice -n[niceness-value] [program]

$ nice -n15 vim
```

#### Nl
[分类：文件管理；输出行号](#)
The nl command writes contents of a file to output, and prepends each line with line number.
```
$ nl file1
1 Hi
2 How are you
3 Bye
```

#### Nm
[分类：文件管理](#)
The nm command is used to display symbols from object files.

```
$ nm test
0804a020 B __bss_start
0804841d T compare
0804a020 b completed.6591
0804a018 D __data_start
0804a018 W data_start
08048360 t deregister_tm_clones
080483d0 t __do_global_dtors_aux
08049f0c t __do_global_dtors_aux_fini_array_entry
0804a01c D __dso_handle
08049f14 d _DYNAMIC
0804a020 D _edata
0804a024 B _end
080484e4 T _fini
080484f8 R _fp_hw
080483f0 t frame_dummy
...
...
...
```

#### Nproc
[分类：进程管理](#)
The nproc command displays the number of processing units available to the current process.
```
$ nproc
1
```

#### Od
[分类：文件管理](#)
The od command lets you dump files in octal as well as some other formats.
```
$ od /bin/ls
0000000 042577 043114 000401 000001 000000 000000 000000 000000
0000020 000002 000003 000001 000000 140101 004004 000064 000000
0000040 122104 000001 000000 000000 000064 000040 000011 000050
0000060 000034 000033 000006 000000 000064 000000 100064 004004
0000100 100064 004004 000440 000000 000440 000000 000005 000000
0000120 000004 000000 000003 000000 000524 000000 100524 004004
...
...
...
```

#### Passwd
[分类：用户权限管理](#)
The passwd command is used for changing passwords for user accounts.
```
$ passwd himanshu
Changing password for himanshu.
(current) UNIX password:
```

#### Paste
[分类：交互](#)
The paste command lets you merge lines of files. For example, if 'file1' contains the following lines:
```
$ cat file1
Hi
My name is
Himanshu
Arora
I
Am
a
Linux researcher
and tutorial
writer
Then the following 'paste' command will join all the lines of the file:

$ paste -s file1
Hi My name is Himanshu Arora I Am a Linux researcher and tutorial writer
```

#### Pidof
[分类：进程管理](#)
The pidof command gives you the process ID of a running program/process.
```
$ pidof nautilus
2714
```

#### Ping
[分类：网络管理](#)
The ping command is used to check whether or not a system is up and responding. It sends ICMP ECHO_REQUEST to network hosts.
```
$ ping howtoforge.com
PING howtoforge.com (104.24.0.68) 56(84) bytes of data.
64 bytes from 104.24.0.68: icmp_seq=1 ttl=58 time=47.3 ms
64 bytes from 104.24.0.68: icmp_seq=2 ttl=58 time=51.9 ms
64 bytes from 104.24.0.68: icmp_seq=3 ttl=58 time=57.4 ms
```

#### Ps
[分类：进程管理](#)
The ps command displays information (in the form of a snapshot) about the currently active processes.
```
$ ps
PID TTY TIME CMD
4537 pts/1 00:00:00 bash
20592 pts/1 00:00:00 ps
```

#### Pstree
[分类：进程管理](#)
The pstree command produces information about running processes in the form of a tree.
```
$ pstree
init???ModemManager???2*[{ModemManager}]
??NetworkManager???dhclient
? ??dnsmasq
? ??3*[{NetworkManager}]
??accounts-daemon???2*[{accounts-daemon}]
??acpid
??atop
```

#### Pwd

The pwd command displays the name of current/working directory.
```
$ pwd
/home/himanshu
```

#### Rm
[分类：文件管理](#)
The rm command lets you remove files and/or directories.
```
$ rm [file-name]
```

#### Rmdir
[分类：文件管理](#)
The rmdir command allows you delete empty directories.
```
$ rmdir [dir-name]
```

#### Scp
[分类：文件管理](#)
The scp command lets you securely copy files between systems on a network.
```
$ scp [name-and-path-of-file-to-transfer] [user]@[host]:[dest-path]
```

#### Sdiff
[分类：文件管理；文本比对 side-by-side](#)
The sdiff command lets you perform a side-by-side merge of differences between two files.
```
$ sdiff file1 file2
```

#### Sed
[分类：文件管理;编程工具](#)
sed is basically a  stream editor that allows users to perform basic text transformations on an input stream (a file or input from a pipeline).
```
$ echo "Welcome to Howtoforge" | sed -e 's/Howtoforge/HowtoForge/g'
Welcome to HowtoForge
```

#### Seq
[分类：计算器](#)
The seq commands prints numbers from FIRST to LAST, in steps of INCREMENT. For example, if FIRST is 1, LAST is 10, and INCREMENT is 2, then here's the output this command produces:

```
$ seq 1 2 10
1
3
5
7
9
```

#### Sha1sum
[分类：计算器](#)
The sha1sum command is used to print or check SHA1 (160-bit) checksums.
```
$ sha1sum test.txt
955e48dfc9256866b3e5138fcea5ea0406105e68 test.txt
```

#### Shutdown

The shutdown command lets user shut the system in a safe way.
```
$ shutdown
```

#### Size
[分类：文件管理](#)
The size command lists the section sizes as well as the total size for an object or archive file.
```
$ size test
text data bss dec hex filename
1204 280 4 1488 5d0 test
```

#### Sleep

The sleep command lets user specify delay for a specified amount of time. You can use it to delay an operation like:
```
$ sleep 10; shutdown
```

#### Sort
[分类：文件管理](#)
The sort command lets you sort lines of text files. For example, if 'file2' contains the following names:
```
$ cat file2
zeus
kyan
sam
adam
Then running the sort command produces the following output:

$ sort file2
adam
kyan
sam
zeus
```

#### Split
[分类：文件管理](#)
The split command, as the name suggests, splits a file into fixed-size pieces. By default, files with name like xaa, xab, and xac are produced.

$ split [file-name]

#### Ssh

ssh is basically OpenSSH SSH client. It provides secure encrypted communication between two untrusted hosts over an insecure network.
```
$ ssh [user-name]@[remote-server]
```

#### Stat
[分类：文件管理](#)
The stat command displays status related to a file or a file-system.
```
$ stat test.txt
File: ‘test.txt’
Size: 20 Blocks: 8 IO Block: 4096 regular file
Device: 801h/2049d Inode: 284762 Links: 2
Access: (0664/-rw-rw-r--) Uid: ( 0/ root) Gid: ( 0/ root)
Access: 2017-03-03 12:41:27.791206947 +0530
Modify: 2017-02-28 16:05:15.952472926 +0530
Change: 2017-03-02 11:10:00.028548636 +0530
Birth: -
```

#### Strings
[分类：文件管理](#)
The strings command displays in output printable character sequences that are at least 4 characters long. For example, when a binary executable 'test' was passed as an argument to this command, following output was produced:
```
$ strings test
/lib/ld-linux.so.2
libc.so.6
_IO_stdin_used
puts
__libc_start_main
__gmon_start__
GLIBC_2.0
PTRh
QVhI
[^_]
EQUAL
;*2$"
GCC: (Ubuntu 4.8.4-2ubuntu1~14.04.3) 4.8.4
....
....
....
```

#### Su
[分类：用户权限管理](#)
The su command lets you change user-identity. Mostly, this command is used to become root or superuser.
```
$ su [user-name]
```

#### Sudo
[分类：用户权限管理](#)
The sudo command lets a permitted user run a command as another user (usually root or superuser).
```
$ sudo [command]
```

#### Sum
[分类：文件管理](#)
The sum command prints checksum and block counts for each input file.
```
$ sum readme.txt
45252 5
```

#### Tac
[分类：文件管理](#)
The tac command prints input files in reverse. Functionality-wise, it does the reverse of what the cat command does.
```
$ cat file2
zeus
kyan
sam
adam
$ tac file2
adam
sam
kyan
zeus
```

#### Tail
[分类：文件管理](#)
The tail command displays in output the last 10 lines of a file.
```
$ tail [file-name]
```
#### Talk
[分类：网络管理](#)
The talk command lets users talk with each other.
```
$ talk [user-name]
```

#### Tar
[分类：文件管理；压缩&解压缩](#)
tar is an archiving utility that lets you create as well as extract archive files. For example, to create archive.tar from files 'foo' and 'bar', use the following command:
```
$ tar -cf archive.tar foo bar

More...
```

#### Tee
[分类：文件管理](#)
The tee command reads from standard input and write to standard output as well as files.
```
$ uname | tee file2
Linux
$ cat file2
Linux
```

#### Test
[分类：计算器](#)
The test command checks file types and compare values. For example, you can use it in the following way:
```
$ test 7 -gt 5 && echo "true"
true
```

#### Time
[分类：性能监测](#)
The time command is used to summarize system resource usage of a program. For example:
```
$ time ping google.com
PING google.com (216.58.220.206) 56(84) bytes of data.
64 bytes from del01s08-in-f14.1e100.net (216.58.220.206): icmp_seq=1 ttl=52 time=44.2 ms
^C
--- google.com ping statistics ---
1 packets transmitted, 1 received, 0% packet loss, time 0ms
rtt min/avg/max/mdev = 44.288/44.288/44.288/0.000 ms
real 0m0.676s
user 0m0.000s
sys 0m0.000s
```

#### Top
[分类：系统信息；性能监测；性能概览。详细介绍 >>>more>>>](http://www.jianshu.com/p/fd6e35f529c1)
The top command gives  a dynamic real-time view of a running system (in terms of its processes). For example:
```
$ top
```
[参考：基于Linux单机的负载评估](http://www.jianshu.com/p/db8e8a2884ef)
[参考：Netflix性能分析模型：In 60 Seconds](http://www.jianshu.com/p/fd6e35f529c1)

#### Touch
[分类：文件管理](#)
The touch command lets you change file timestamps (the access and modification times). When name of a non-existent file is passed as an argument, that file gets created.
```
$ touch [file-name]
```

#### Tr
[分类：文件管理](#)
The tr command can be used to translate/squeeze/delete characters. For example, here's how you can use it to convert lowercase characters to uppercase:
```
$ echo 'howtoforge' | tr "[:lower:]" "[:upper:]"
HOWTOFORGE
```

#### Tty
[分类：资源管理](#)
The tty command prints the filename of the terminal connected to standard input.
```
$ tty
/dev/pts/10
```

#### Uname
[分类：用户权限管理](#)
The uname command prints certain system information.
```
$ uname -a
Linux himanshu-desktop 4.4.0-62-generic #83~14.04.1-Ubuntu SMP Wed Jan 18 18:10:26 UTC 2017 i686 athlon i686 GNU/Linux
```

#### Uniq
[分类：文件管理；待补充信息](#)
The Uniq command is used to report or omit repeated lines. For example, if 'file2' contains the following data:
```
$ cat file2
Welcome to HowtoForge
Welcome to HowtoForge
A Linux tutorial website
Thanks
Then you can use the uniq command to omit the repeated line.

$ uniq file2
Welcome to HowtoForge
A Linux tutorial website
Thanks
```

#### Unexpand
[分类：文件管理；待补充信息](#)
The unexpand command converts spaces present in the input file(s) into tabs, and writes the file contents to standard output.
```
$ unexpand file1
```

#### Uptime
[分类：系统信息；性能监测；查看负载。详细介绍 >>>more>>>](http://www.jianshu.com/p/fd6e35f529c1)
The uptime command tells how long the system has been running.
```
$ uptime
15:59:59 up 6:20, 4 users, load average: 0.81, 0.92, 0.82
```

#### Users
[分类：用户权限管理；待补充信息](#)
The users command displays in output the usernames of users currently logged in to the current host.
```
$ users
himanshu himanshu himanshu himanshu
```

#### Vdir
[分类：文件管理；待补充信息](#)
The vdir command lists information about contents of a directory (current directory by default).
```
$ vdir
total 1088
-rw-rw-r-- 1 himanshu himanshu 4850 May 20 2015 test_backup.pdf
-rw-rw-r-- 1 himanshu himanshu 2082 May 28 2015 test-filled.pdf
-rw-rw-r-- 1 himanshu himanshu 7101 May 28 2015 test.pdf
```

#### Vim
[分类：编辑器](#)
vim is basically a text/programming editor. The name 'vim' stands for Vi IMproved as the editor is upwards compatible to the Vi editor.
```
$ vim [file-name]
```

#### W
[分类：性能监测](#)
The w command displays information about the users currently on the machine, and their processes.
```
$ w
16:18:07 up 6:39, 4 users, load average: 0.07, 0.32, 0.53
USER TTY FROM LOGIN@ IDLE JCPU PCPU WHAT
himanshu :0 :0 09:39 ?xdm? 1:08m 0.25s init --user
himanshu pts/0 :0 09:41 6:36m 0.84s 7.84s gnome-terminal
himanshu pts/10 :0 14:51 0.00s 0.16s 0.00s w
himanshu pts/11 :0 15:41 35:19 0.05s 0.05s bash
```

#### Wall
[分类：通讯；待补充信息](#)
The wall command lets you write and send a message to other users that are currently logged in.
```
$ wall [your-message]
```

#### Watch
[分类：性能监测](#)
The watch command can be used to monitor a program's output. It runs the program repeatedly, displaying its output and errors. For example:
```
$ watch date
```

#### Wc
[分类：文件管理；待补充信息](#)
The wc command prints newline, word, and byte counts for a file.
```
$ wc test.txt
0 3 20 test.txt
```

#### Whatis
[分类：帮助](#)
The whatis command displays single-line manual page descriptions.
```
$ whatis mkdir
mkdir (1) - make directories
mkdir (2) - create a directory
mkdir (1posix) - make directories
```

#### Which
[分类：文件管理；以来](#)
The which command basically lets you locate a command - the file and the path of the file that gets executed. For example:
```
$ which date
/bin/date
```

#### Who
[分类：登录信息](#)
The who command shows who is logged on.
```
$ who
himanshu :0 2017-03-03 09:39 (:0)
himanshu pts/0 2017-03-03 09:41 (:0)
himanshu pts/10 2017-03-03 14:51 (:0)
himanshu pts/11 2017-03-03 15:41 (:0)
```

#### Whereis
[分类：文件管理；以来](#)
The whereis command shows in output locations of the binary, source, and manual page files for a command.
```
$ whereis ls
ls: /bin/ls /usr/share/man/man1/ls.1posix.gz /usr/share/man/man1/ls.1.gz
```

#### Whoami
[分类：登录信息](#)
The whoami command prints effective userid of the current user.
```
$ whoami
himanshu
```

#### Xargs
[分类：编程工具](#)
The xargs command builds and executes command lines from standard input. In layman's terms, it reads items from stdin and executes a command passed to it as an argument. For example, here's how you can use xargs to find the word "Linux" in the files whose names are passed to it as input.
```
$ xargs grep "Linux"
file1
file2
file3
file1:Linux researcher
file2:A Linux tutorial website
file3:Linux is opensource
More...
```

#### Yes
[分类：交互；确认](#)
The Yes command outputs a string repeatedly until killed.
```
$ yes [string]
```

<hr>
更多精彩内容，请扫码关注公众号：@睿哥杂货铺  
[RSS订阅 RiboseYim](https://riboseyim.github.io?product=ebook&id=linuxperfmaster)
