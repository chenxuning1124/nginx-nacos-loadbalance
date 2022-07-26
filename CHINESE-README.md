# nginx-nacos-loadbalance

 [![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
 
## 特性
##### 依靠nacos,让nginx拥有动态负载均衡的能力
将所有服务注册到nacos上,nginx-nacos-loadbalance将订阅指定的命名空间。如果其中一个服务的健康状态发生了改变,则nginx-nacos-loadbalance
将及时的感知到改变,并且修改nginx的配置文件,让nginx拥有动态负载均衡的能力

## 运行原理
````
程序运行时,会自动检测config.properties文件
--订阅services[i].nacosServiceName服务
    1.当发现nginx配置文件中不存在此services[i].nginxUpstreamValue时,会自动创建
        upstream services[i].nginxUpstreamValue {
            
        }
    2.当发现有健康实例时,会修改upstream,添加server节点
        upstream services[i].nginxUpstreamValue {
            server: 192.168.1.12:9081;
            server: 192.168.1.11:9081;
        }
    3.当发现有不健康实例时,会修改upstream,删除server节点
        upstream services[i].nginxUpstreamValue {
            server: 192.168.1.12:9081;
        }
    4.当发现没有健康实例时,会删除upstream
````

## 快速开始
#### Step 1: 下载最新版本

选择合适的版本进行下载[latest stable release](https://github.com/chenxuning1124/nginx-nacos-loadbalance/releases).  
```
tar -zxf nginx-nacos-loadbalance.tar.gz 
cd nginx-nacos-loadbalance
``` 

#### Step 2: 修改 config.properties
````
cd conf
vim config.properties
````
当前配置文件为json格式
````
{
    "nginxExe":"/usr/sbin/nginx",
    "refreshUpstreamThreadCount":10,
    "scheduleRefreshTime":10000,
    "nacosServerAddr":"127.0.0.1:8848",
    "nacosNamespace":"42294eb8-8ecf-462d-a730-d00230db92fb",
    "nginxConfigPath":"/etc/nginx/nginx.conf",
    "nacosClusterName":"DEFAULT",
    "services":[
        {
            "nginxUpstreamValue":"serviceName1",
            "nacosServiceName":"serviceName1",
            "nacosGroupName":"group-dev"
        },
        {
            "nginxUpstreamValue":"serviceName2",
            "nacosServiceName":"dataresource2",
            "nacosGroupName":"group-dev"
        }
    ]
}
````
带描述的config.properties
````
{
    -- nginx的路径
    "nginxExe":"/usr/sbin/nginx",
    -- 修改nginx配置文件的线程数,默认是12  
    "refreshUpstreamThreadCount":10,
    -- 当服务发生变化时会主动更新nginx配置,同时会有一个定时器在后台定时更新nginx配置,此参数为定时器执行时间间隔,默认10000毫秒
    "scheduleRefreshTime":10000,
    -- nacos地址
    "nacosServerAddr":"127.0.0.1:8848",
    -- nacos命名空间id,默认public
    "nacosNamespace":"42294eb8-8ecf-462d-a730-d00230db92fb",
    -- nginx配置文件地址
    "nginxConfigPath":"/etc/nginx/nginx.conf",
    -- nacos集群名称,以逗号分隔,默认DEFAULT
    "nacosClusterName":"DEFAULT",
    "services":[
        {
            -- nginx配置文件内对应upstream的名字
            -- upstream serviceName1 {
            --      server: 192.168.1.12:9081;
            -- }
            "nginxUpstreamValue":"serviceName1",
            -- nacso对应服务名
            "nacosServiceName":"serviceName1",
            -- 服务对应的组名,默认DEFAULT_GROUP
            "nacosGroupName":"group-dev"
        },
        {
            "nginxUpstreamValue":"serviceName2",
            "nacosServiceName":"dataresource2",
            "nacosGroupName":"group-dev"
        }
    ]
}
````
#### Step 3: 启动
````
cd ../bin
sh startup.sh
````

## 联系方式
* 邮箱    1124440310@qq.com
* 微信    RRRRRReserved

![wechat.jpg](https://github.com/chenxuning1124/nginx-nacos-loadbalance/blob/develop/src/main/resources/wechat.jpg)

#### 如果你找到了bug,或者想加入此项目,欢迎联系作者(●'◡'●)(●'◡'●)(●'◡'●)






