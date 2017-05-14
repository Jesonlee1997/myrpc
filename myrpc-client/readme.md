服务的同步调用：  
业务线程发起一个调用，阻塞，返回结果时通过messageId  
requestId-promise。关联数组  
在业务线程中调用promise的await方法，在IO线程中调用setSuccess  

怎样实现长连接  
两个以上的线程 

Netty中一个channel只会注册到一个线程上，只要不在其他地方使用这个channel，channel中的

IO线程和channel一对多
对一个channel的读写都是用同一个线程来完成
一个服务和channel一对一。
多线程调用同时服务时需要对channel进行同步。
每次发送请求就把id-promise加入队列中
发送的请求和对应的promise被保存至一个Map中。

服务返回结果后如何将结果传递给对应的线程？
线程如何根据
