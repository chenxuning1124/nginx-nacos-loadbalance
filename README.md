# nginx-nacos-loadbalance

 [![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
 
中文文档 [CHINESE-README](./CHINESE-README.md)
 
## What's the features
##### Base on nacos and let nginx has dynamic load balancing capability
Register all the services on nacos.And the nginx-nacos-loadbalance will subscribe the specific namespace.
If one of the service's healthy condition turns to unhealthy.The nginx-nacos-loadbalance will feel it in time,
and will depends on the services List to modify nginx config.That can let nginx has the dynamic load balancing capability.

## Operating principle
````
When start the program then scan the config.properties
--subscribe services[i].nacosServiceName from nacos
    1.When services[i].nginxUpstreamValue is not exists in the nginx config,will create
        upstream services[i].nginxUpstreamValue {
            
        }
    2.If find any healthy instance,will modify the upstream,add server nodes
        upstream services[i].nginxUpstreamValue {
            server: 192.168.1.12:9081;
            server: 192.168.1.11:9081;
        }
    3.If find any unhealthy instance,will modify upstream,delete server nodes;
        upstream services[i].nginxUpstreamValue {
            server: 192.168.1.12:9081;
        }
    4.If find none healthy instance,will delete upstream
````

## Quick Start
It is super easy to get started with your first project.

#### Step 1: Download the binary package 

You can download the package from the [latest stable release](https://github.com/chenxuning1124/nginx-nacos-loadbalance/releases).  

```
tar -zxf nginx-nacos-loadbalance.tar.gz 
cd nginx-nacos-loadbalance
``` 

#### Step 2: Modify config.properties
````
cd conf
vim config.properties
````
The format of the config is JSON.Here is the example of the config.
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
With description
````
{
    -- The path of your nginx
    "nginxExe":"/usr/sbin/nginx",
    -- The number of the thread that is waitting for update your nginx config(Default 12)  
    "refreshUpstreamThreadCount":10,
    -- The timer interval execution time(Default 10000 milliseconds).
    "scheduleRefreshTime":10000,
    -- The nacos address
    "nacosServerAddr":"127.0.0.1:8848",
    -- Nacos namespace id(Default public)
    "nacosNamespace":"42294eb8-8ecf-462d-a730-d00230db92fb",
    -- Nginx config path
    "nginxConfigPath":"/etc/nginx/nginx.conf",
    -- Nacos cluster name,the value separated by comma(Default 'DEFAULT')
    "nacosClusterName":"DEFAULT",
    "services":[
        {
            -- The nginx config upstream key
            -- upstream serviceName1 {
            --      server: 192.168.1.12:9081;
            -- }
            "nginxUpstreamValue":"serviceName1",
            -- Nacos service name
            "nacosServiceName":"serviceName1",
            -- The service group name(Default 'DEFAULT_GROUP')
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
#### Step 3: Start Server
````
cd ../bin
sh startup.sh
````

## Contact
* Email    1124440310@qq.com
* Wechat   RRRRRReserved

![wechat](https://github.com/chenxuning1124/nginx-nacos-loadbalance/blob/develop/src/main/resources/wechat.jpg)

#### Welcome to contact the author,if you find any bug or you have meaningful suggestion.Thanks(●'◡'●)(●'◡'●)(●'◡'●)






