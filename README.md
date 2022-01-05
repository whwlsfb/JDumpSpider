# JDumpSpider
HeapDump敏感信息提取工具

# 编译
需要Maven、JDK 1.8。
```
$ mvn package
```

# 使用

```sh
$ java -jar .\target\JDumpSpider-1.0-SNAPSHOT-full.jar                  
Missing required parameter: '<heapfile>'
Usage: JDumpSpider [-hV] <heapfile>                   
Extract sensitive information from heapdump file.     
      <heapfile>   Heap file path.                    
  -h, --help       Show this help message and exit.   
  -V, --version    Print version information and exit.

```
