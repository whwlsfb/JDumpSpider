# JDumpSpider
HeapDump敏感信息提取工具

# 编译
需要Maven、JDK 1.8。
```
$ mvn package
```
# 支持范围

暂支持提取以下类型的敏感信息

- 数据源
    - SpringDataSourceProperties
    - WeblogicDataSourceConnectionPoolConfig
    - MongoClient
- 配置文件信息
    - OriginTrackedMapPropertySource
    - MutablePropertySources
- Redis配置提取
    - RedisStandaloneConfiguration
    - JedisClient

更多类型支持尽请期待。

# 使用

本工具需要使用JDK 1.8版本（更高版本将导致异常）。

```sh
$ java -jar .\target\JDumpSpider-1.0-SNAPSHOT-full.jar                  
Missing required parameter: '<heapfile>'
Usage: JDumpSpider [-hV] <heapfile>                   
Extract sensitive information from heapdump file.     
      <heapfile>   Heap file path.                    
  -h, --help       Show this help message and exit.   
  -V, --version    Print version information and exit.

```
