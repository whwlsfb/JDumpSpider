# JDumpSpider
HeapDump敏感信息提取工具

# 下载

预编译包：[Releases](https://github.com/whwlsfb/JDumpSpider/releases)

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
    - AliDruidDataSourceWrapper
    - HikariDataSource
- 配置文件信息
    - MapPropertySource
    - OriginTrackedMapPropertySource
    - MutablePropertySource
    - ConsulPropertySource
    - OSS（模糊搜索）
- Redis配置
    - RedisStandaloneConfiguration
    - JedisClient
- ShiroKey
    - CookieRememberMeManager
- JwtKey
    - SpringSecurityRsaSigner

更多类型支持尽请期待。

# 使用

本工具需要使用Oracle JDK 1.8版本（更高版本将导致异常）。

```sh
$ java -jar .\target\JDumpSpider-1.0-SNAPSHOT-full.jar                  
Missing required parameter: '<heapfile>'
Usage: JDumpSpider [-hV] <heapfile>                   
Extract sensitive information from heapdump file.     
      <heapfile>   Heap file path.                    
  -h, --help       Show this help message and exit.   
  -V, --version    Print version information and exit.

```
