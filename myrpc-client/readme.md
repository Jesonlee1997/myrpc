服务的同步调用：  
数据结构：requestId-PromiseResponse-关联数组
服务调用者调用服务发布的接口，由动态代理发起远程服务调用
将每个请求赋予一个请求id，并将这个id与PromiseResponse管理起来
消费者线程调用promise的await方法，同步阻塞等待应答，并设置了超时时间
IO线程收到来自服务器端的ServiceResponse时，根据这个Response中的id找到对应的PromiseResponse，把Response注入，同时唤醒阻塞线程
消费者线程被唤醒，获得ServiceResponse，将对应的PromiseResponse从map中移除。

服务的异步调用：

怎样实现长连接  
长连接就是一旦客户端与服务器连接上，就保持连接，以后再有访问该服务的客户端，则直接通过之前建立的Channel访问
具体实现：
客户端中维护一个serviceName-Channel的map，每当访问一个Service时就查询Map中是否包含包含此Service的


IO线程和channel一对多
对一个channel的读写都是用同一个线程来完成
一个服务和channel一对一。
多线程调用同时服务时需要对channel进行同步。
每次发送请求就把id-promise加入队列中
发送的请求和对应的promise被保存至一个Map中。

服务返回结果后如何将结果传递给对应的线程？
线程如何根据

业务线程在调用Channel的write方法后，就会得到一个Promise，用这个promise和requestId构造一个
不需要传递promise，IO线程拿到返回结果后直接去寻找对应id的PromiseResponse，将promise设为success

关于几种异常情况：  
线程等待调用结果超时：这个时候有两种可能情况  
服务器端处理速度过慢  
网络故障  
都将返回TimeOut。

注册中心中没有请求的服务。
注册中心中请求的服务没有对应的节点。
与对应的节点连接却拒绝连接（对应节点宕机或者改变了端口号）
都将返回ServiceNotFound

channel被强制关闭

在能与服务器连接的情况下，服务器一定会返回一个结果
有可能的结果：
调用成功
调用失败：对应的服务器上没有指定的Service或method
调用失败：服务器调用过程中出错（严重到影响程序运行的错误）

线程阻塞状态被打断(只针对客户端的线程)
