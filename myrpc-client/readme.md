服务的同步调用  
同步调用的使用方法：在代服务调用的代码块开始前调用ServiceProxy.setSync()
数据结构：requestId-PromiseResponse-关联数组
服务调用者调用服务发布的接口，由动态代理发起远程服务调用
将每个请求赋予一个请求id，并将这个id与PromiseResponse管理起来
消费者线程调用promise的await方法，同步阻塞等待应答，并设置了超时时间
IO线程收到来自服务器端的ServiceResponse时，根据这个Response中的id找到对应的PromiseResponse，把Response注入，同时唤醒阻塞线程
消费者线程被唤醒，获得ServiceResponse，将对应的PromiseResponse从map中移除。


服务的异步调用  
异步调用的使用方法：在代服务调用的代码块开始前调用ServiceProxy.setAsync()  
数据结构同样是：数据结构：requestId-PromiseResponse-关联数组  
与同步调用不同的部分，调用同步服务的方法会立即返回null，然后线程需在下一个服务调用之前把PromiseResponse从RpcContext中拉取下来。  
因为RpcContext中以Thread-PromiseResponse保存，直接调用下一个服务会覆盖上一个服务的Promise。
当IO线程完成时，根据id取出PromiseResponse，调用setResponse方法，把response放入PromiseResponse，并将promise设为success。


我们可以看到，异步调用和同步调用使用的是同样的数据结构，并且对于netty来说是透明的。
netty只需要负责收到数据时将PromiseResponse取出，把Response放进去，对于同步或者异步的线程而言都能获得结果。


怎样实现长连接  
长连接就是一旦客户端与服务器连接上，就保持连接，以后再有访问该服务的客户端，则直接通过之前建立的Channel访问
具体实现：  
客户端中维护一个serviceName-Channel的map，每当访问一个Service时就查询Map中是否包含此Service的Channel，如果此Channel是打开的就直接使用这个Channel。  



关于几种异常情况：  
线程等待调用结果超时：这个时候有两种可能情况  
服务器端处理速度过慢  
网络故障  
都将返回TimeOut。

注册中心中没有请求的服务：返回ServiceNotFound
注册中心中请求的服务没有对应的节点或者与对应的节点连接却拒绝连接返回ProviderNotFound
（对应节点宕机或者改变了端口号）


在能与服务器连接的情况下，服务器有可能返回的结果返回一个结果
有可能的结果：
调用成功
调用失败：对应的服务器上没有指定的Service或method，返回ServiceOffline
调用失败：服务器调用过程中出错（严重到影响程序运行的错误）

线程阻塞状态被打断(只针对客户端的线程)
