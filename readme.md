JesonLee的RPC框架，使用Netty+Hessian+zookeeper完成  
-----

common包中包括  
通用的ServiceRequest的定义  
通用的ServiceResponse的定义  
序列化的工具（目前只有Hessian）  
注册中心ServiceRegistry，提供服务列表查询和服务注册的功能。  
注册中心的IP地址可配置  
注册中心服务信息的存储方式：  
服务信息的根路径是/services    
对于每一个新注册的服务，都会在/services节点下建立一个新的持久节点，表示这个服务的名字。  
如果服务已经被注册过，那么会在这个服务对应的持久节点下，建立一个临时节点，这个节点表示服务器的地址  
当服务器宕机后，临时节点被删除，ServiceRegistry会通知这个服务的消费者更新服务器列表  
<br>
---
server包的使用  
在Spring的配置文件中配置需要发布的Service
配置注册中心ServiceRegistry的zookeeper服务器的地址  
然后启动server和Spring容器  

服务端实现原理：
1. 在Spring配置文件中配置需要注册的服务名及其相应的实现类
2. 先启动Spring容器，生成这些服务实例的同时，会将这些服务注册到ServiceManager
4. 每一个服务都是一个Service实体，由ServiceManager来管理
3. 在服务器启动阶段会将ServiceManager中的所有的服务都注册到zooKeeper上
4. 当有来自客户端的ServiceRequest请求时，获得request中的服务名
5. 由ServiceManager对request进行路由，寻找本地是否有对应的服务
6. 调用本地服务，将调用结果包装成ServiceResponse返回给调用方

额外信息：  
在一个应用中ServiceManager是单例的，ServiceManager需要注入ServiceRegistry，ServiceRegistry也是单例的  
需配置Server实例的port  
<br>

---
client包的使用  
配置注册中心ServiceRegistry  
使用Spring配置需要引用的的服务   
然后就可以直接在程序中使用这些远程服务  

客户端实现：  
在Spring中配置服务的名称（接口的名字） TODO  
实现服务的动态代理（透明的）。  
具体流程：
1. 在Spring容器启动时，会生成服务的远程代理  
1. 创建一个RpcClient实例，每个代理中都拥有RpcClient的引用
2. rpcClient实例连接远程ZooKeeper服务器，并在本地保存ZooKeeper服务器提供的服务列表
2. 注册监听器监听服务器节点的变化
3. 当需要调用远程服务时，直接从本地的服务列表中查找
4. 如果没有找到可以直接返回没找到
3. 根据远程服务器的地址建立TCP短连接，创建一个ServiceRequest对象，转由rpcClient去调用
4. rpcClient从线程池中拿取一个线程进行调用，直到结果返回前该线程都处于阻塞状态
5. 将服务器返回的数据序列化为ServiceResponse对象，判断状态，接收返回结果

调用服务时会先从ServiceRegistry中拉取服务器列表  
随机选择一个Server进行访问，发送一个ServiceRequest请求，同步接收结果  
判断结果的status，获得结果。  

额外信息：  
与server端建立的是长连接  
实现服务的同步调用依靠的是netty的promise机制  
实现服务的异步调用则用到了Future  

myrpc-test中包含了Netty的一个简单的聊天服务器和Netty回显服务器
以及一些测试程序