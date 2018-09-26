# Linux 实用扩展命令

- DevOps 资讯 | 是时候升级你的命令行了
- bat > cat
- prettyping > ping
- fzf > ctrl+r
- htop > top
- diff-so-fancy > diff
- fd > find
- ncdu > du
- tldr > man
- ack || ag > grep
- jq > grep et al

> 子贡问为仁。
子曰：“工欲善其事，必先利其器。居是邦也，事其大夫之贤者，友其士之仁者。”
——《论语·卫灵公》

#### bat > cat

cat 被用于打印文件内容。ccat 工具还提供像语法高亮显示这样的功能。在此基础之上，bat 还支持 分页, 行号和 git 集成。同时允许在输出中搜索 (当输出长于屏幕高度时) 。更多信息：[https://github.com/sharkdp/bat](https://github.com/sharkdp/bat)

![](http://og2061b3n.bkt.clouddn.com/CLI-20180902-bat-1.png)

![](http://og2061b3n.bkt.clouddn.com/CLI-20180902-bat-2.png)

![](http://og2061b3n.bkt.clouddn.com/CLI-20180902-bat-3.png)

```
# Linux
wget https://github.com/sharkdp/bat/releases/download/v0.6.1/bat-v0.6.1-x86_64-unknown-linux-gnu.tar.gz
make install

# Mac
brew install bat

#
alias cat='bat'
```

#### prettyping > ping

ping 是一种非常有用的网络工具。原理是向目标主机传出一个ICMP echo@要求数据包，并等待接收 echo 回应数据包。程序会按时间和成功响应的次数估算丢失数据包率（丢包率）和数据包往返时间（网络时延，Round-trip delay time）。不过默认的 ping 命令输出比较乏味。prettyping 则提供了更友好、更美观的输出，包括彩色点图表示网络连通性。prettyping 基于 bash 和 awk 编写能，够兼容大部分操作系统 (例如 Linux, BSD, Mac OS X, …)。更多信息：[http://denilson.sa.nom.br/prettyping/](http://denilson.sa.nom.br/prettyping/)

![](http://og2061b3n.bkt.clouddn.com/CLI-20180903-ping.png)

```
curl -O https://raw.githubusercontent.com/denilsonsa/prettyping/master/prettyping
chmod +x prettyping
./prettyping baiud.com

alias ping='prettyping --nolegend'
```

#### fzf > ctrl+r
在终端中使用 ctrl + r 组合键可以向后搜索历史操作记录（ 尽管有点繁琐 ）。fzf 工具是对 ctrl + r 的增强。支持对终端操作历史的模糊搜索, 预览可能的匹配结果。除了历史搜索之外, fzf 还可以预览和打开文件。
更多信息：[https://github.com/junegunn/fzf](https://github.com/junegunn/fzf)

![](http://og2061b3n.bkt.clouddn.com/CLI-20180902-fzf.png)

```
# Linux
git clone --depth 1 https://github.com/junegunn/fzf.git ~/.fzf
~/.fzf/install
# Mac
brew install fzf

# alias
alias preview="fzf --preview 'bat --color \"always\" {}'"
# add support for ctrl+o to open selected file in VS Code
export FZF_DEFAULT_OPTS="--bind='ctrl-o:execute(code {})+abor
```

#### htop > top

top 是一个快速诊断系统性能的工具。值得一提的是 top for Mac 与 Linux 上的输出不太一样。htop 优化了顶部输出格式，并支持大量的颜色, 键盘快键键和视图, 帮助我们洞察进程行为。更多信息：[http://hisham.hm/htop/](http://hisham.hm/htop/)

![htop](http://og2061b3n.bkt.clouddn.com/CLI-20180902-htop.png)

htop 提供的键盘快捷键：
- P - 按 CPU 利用率排序
- M - 按内存利用率排序
- F4 - 按字符串过滤进程
- space - 高亮显示某一进程，便于持续跟踪

```
alias top="sudo htop" # alias top
```

#### diff-so-fancy > diff

GIT 版本控制系统中使用 git diff 来显示两个版本之间差别(包括文件、元数据和改动等)。diff-so-fancy 是一个用 node.js 实现的命令行工具，提供更友好的输出样式和定制能力。更多信息：[https://github.com/so-fancy/diff-so-fancy](https://github.com/so-fancy/diff-so-fancy)

![](http://og2061b3n.bkt.clouddn.com/CLI-20180902-diff-so-fancy.jpg)

![](http://og2061b3n.bkt.clouddn.com/CLI-20180902-git-diff.jpg)

```
# download
wget https://raw.githubusercontent.com/so-fancy/diff-so-fancy/master/third_party/build_fatpack/diff-so-fancy
chmod +x diff-so-fancy
# npm
npm install -g diff-so-fancy
# 直接调用
git diff --color | diff-so-fancy
```
在 git diff 和 git show 中启用  diff-so-fancy ，需要修改 gitconfig :
```
[pager]
	   diff = diff-so-fancy | less --tabs=1,5 -RFX
	   show = diff-so-fancy | less --tabs=1,5 -RFX
```
除了默认样式优化，diff-so-fancy 还支持颜色和显示项定制，例如：
```
git config --global color.ui true

git config --global color.diff-highlight.oldNormal    "red bold"
git config --global color.diff-highlight.oldHighlight "red bold 52"
git config --global color.diff-highlight.newNormal    "green bold"
git config --global color.diff-highlight.newHighlight "green bold 22"

git config --global color.diff.meta       "yellow"
git config --global color.diff.frag       "magenta bold"
git config --global color.diff.commit     "yellow bold"
git config --global color.diff.old        "red bold"
git config --global color.diff.new        "green bold"
git config --global color.diff.whitespace "red reverse"

git config --bool --global diff-so-fancy.markEmptyLines false
```

#### fd > find
fd 非常快。有意思的是 fd 与 bat 的作者是同一个人（David Peter）。更多信息：[https://github.com/sharkdp/fd/](https://github.com/sharkdp/fd/)

![](http://og2061b3n.bkt.clouddn.com/CLI-20180902-fd.png)

常用的命令行：
```
## SourceCode
git clone https://github.com/sharkdp/fd
cd fd
cargo build
cargo test
cargo install

# Mac
brew install fd

## Usage
fd cli # 查找所有包含"cli"的文件名
fd -e md # 查找所有 .md 文件
fd cli -x wc -w # 查找 "cli" 并运行 `wc -w`
```

#### ncdu > du

了解磁盘空间占用情况是一项非常重要的工作。常用的命令是 du -sh  (-sh 表示摘要、便于人工阅读), 但我们经常需要挖掘目录的空间占用。ncdu 是一个替代选择（完全基于 C 语言编写，MIT 许可证）。ncdu 提供了一个交互式界面, 支持快速扫描哪些文件夹或文件占用空间, 并且导航非常方便。更多信息：[https://dev.yorhel.nl/ncdu](https://dev.yorhel.nl/ncdu)

![](http://og2061b3n.bkt.clouddn.com/CLI-20180902-nudu.png)

![](http://og2061b3n.bkt.clouddn.com/CLI-20180902-nudu-nav.png)

```
git clone git://g.blicky.net/ncdu.git/

# release
wget https://dev.yorhel.nl/download/ncdu-1.13.tar.gz
tar -xvf ncdu-1.13.tar.gz
./configure
make
make install

# Usage
ncdu path
```

```
alias du="ncdu --color dark -rr -x --exclude .git --exclude node_modules"

# 扩展选项
--color dark - use a colour scheme
-rr - read-only mode (prevents delete and spawn shell)
--exclude ignore directories I won't do anything about
```


#### tldr > man

几乎每一个命令行工具都可以通过手工输入 man 命令获得帮助信息。TL;DR 项目("too long; didn't read")是一个由社区驱动的命令行文档系统，以非常简洁的方式提供命令行参数列表、使用说明和示例。更多信息：[https://tldr.sh/](https://tldr.sh)

```
# Install
npm install -g tldr

alias help='tldr'

# Usage
Options:

  -V, --version            output the version number
  -l, --list               List all commands for the chosen platform in the cache
  -a, --list-all           List all commands in the cache
  -1, --single-column      List single command per line (use with options -l or -a)
  -r, --random             Show a random command
  -e, --random-example     Show a random example
  -f, --render [file]      Render a specific markdown [file]
  -m, --markdown           Output in markdown format
  -o, --os [type]          Override the operating system [linux, osx, sunos]
  --linux                  Override the operating system with Linux
  --osx                    Override the operating system with OSX
  --sunos                  Override the operating system with SunOS
  -t, --theme [theme]      Color theme (simple, base16, ocean)
  -s, --search [keywords]  Search pages using keywords
  -u, --update             Update the local cache
  -c, --clear-cache        Clear the local cache
  -h, --help               output usage information

# Example
bash-3.2$ tldr tar
✔ Page not found. Updating cache
✔ Creating index

  tar

  Archiving utility.
  Often combined with a compression method, such as gzip or bzip.

  - Create an archive from files:
    tar cf target.tar file1 file2 file3

  - Create a gzipped archive:
    tar czf target.tar.gz file1 file2 file3

  - Extract an archive in a target folder:
    tar xf source.tar -C folder
```

#### ack || ag > grep

grep 无疑是一个强大的命令行工具, 但多年来它已被许多工具所取代，包括 ack 和 ag 。更多信息：[https://beyondgrep.com/]

![](http://og2061b3n.bkt.clouddn.com/CLI-20180902-ack.png)

```
curl https://beyondgrep.com/ack-2.24-single-file > ~/bin/ack && chmod 0755 ~/bin/ack
```
ack 和 ag 默认情况下使用正则表达式进行搜索,可以指定文件类型 —— 使用像 --js 或 --html 标志搜索。ack 和 ag  工具都支持 grep 选项, 如 -B (表示输出匹配行和其之后(after)的N行)。

ack 默认没有支持 markdown 格式，可以在 .ackrc 文件定制：
```
--type-set=md=.md,.mkd,.markdown
--pager=less -FRX
```

#### jq > grep et al

> jq is like sed for JSON data

jq 可以作为 JSON 数据转换工具。示例：更新节点依赖项 (分为多行以便于可读性)。
更多信息：[https://stedolan.github.io/jq/](https://stedolan.github.io/jq/)

```node.js
npm i $(echo $(\
  npm outdated --json | \
  jq -r 'to_entries | .[] | "\(.key)@\(.value.latest)"' \
))
```

```node.js
{
  "node-jq": {
	"current": "0.7.0",
	"wanted": "0.7.0",
	"latest": "1.2.0",
	"location": "node_modules/node-jq"
  },
  "uuid": {
	"current": "3.1.0",
	"wanted": "3.2.1",
	"latest": "3.2.1",
	"location": "node_modules/uuid"
  }
}

```

```node.js
node-jq@1.2.0
uuid@3.2.1
```
