package me.jesonlee.rpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import me.jesonlee.rpc.common.HessianUtil;
import me.jesonlee.rpc.common.ServiceRegistry;
import me.jesonlee.rpc.common.ServiceRequest;
import me.jesonlee.rpc.common.ServiceResponse;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * RPC框架的调用端
 * Created by JesonLee
 * on 2017/5/12.
 */
public class RpcClient {
    private Bootstrap bootstrap;

    //TODO:使用Spring配置
    private ServiceRegistry serviceRegistry = new ServiceRegistry("192.168.56.101:2181");
    private Watcher providersWatcher = new ProvidersChangedWatcher();//注册在zooKeeper上的监听器
    private AtomicInteger round = new AtomicInteger(0);//轮询服务器列表的号码

    //TODO:合并services和serviceChannelMap
    private final Map<String, List<String>> services = new ConcurrentHashMap<>();

    //TODO：使每个服务可以连接的Channel个数可配置
    private final Map<String, Channel> serviceChannelMap = new ConcurrentHashMap<>();


    private final Map<Long, PromiseResponse> responseMap = new ConcurrentHashMap<>();


    private RpcContext rpcContext = RpcContext.getInstance();

    public RpcClient() {
        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup group = new NioEventLoopGroup(1);
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new RpcOutClientHandler());

                        pipeline.addFirst(new LengthFieldBasedFrameDecoder(
                                10000000,
                                0,
                                4,
                                0,
                                4));
                        pipeline.addLast("handler", new RpcInClientHandler());
                    }
                });
        this.bootstrap = bootstrap;
    }

    /**
     * 重要的一个方法，可以被多个线程调用，这里的多个线程应该由用户来定义
     * 这个方法是同步阻塞的，如果超过3秒则返回超时
     *
     * @param request
     * @return
     */
    public ServiceResponse send(ServiceRequest request) {

        Channel channel;
        String serviceName = request.getServiceName();
        try {
            //获得一个Channel
            if (serviceChannelMap.containsKey(serviceName)) {
                channel = serviceChannelMap.get(serviceName);
            } else {

                //从zookeeper拉取服务列表
                String providerAddress = getProviderAddress(serviceName);//提供者地址，由IP和端口号组成
                if (providerAddress == null) {
                    return ServiceResponse.ServiceNotFound(request.getId());
                }
                String[] address = providerAddress.split(":");
                String host = address[0];
                int port = Integer.parseInt(address[1]);

                //新建一条链路
                channel = bootstrap.connect(host, port).sync().channel();
            }


            ChannelPromise promise = (ChannelPromise) channel.write(request);
            PromiseResponse promiseResponse = new PromiseResponse(promise);
            responseMap.put(request.getId(), promiseResponse);

            //阻塞调用，如果返回null，说明服务器调用超时
            //TODO:remove
            long start = System.currentTimeMillis();
            ServiceResponse response = promiseResponse.getResponse();
            long end = System.currentTimeMillis();
            System.out.println("等待结果返回阻塞的时间："+(end - start));

            if (response == null) {
                return ServiceResponse.Timeout(request.getId());
            }

            responseMap.remove(request.getId());
            //如果服务器返回的状态是200则将服务名和通道放入serviceChannel
            if (response.getStatus() == 200) {
                serviceChannelMap.put(serviceName, channel);
            }
            return response;

        } catch (InterruptedException e) {
            //TODO：处理异常
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 异步调用，客户端如果想要获得结果则要通过RpcContext获取PromiseResponse
     * @param request
     * @return
     */
    public ServiceResponse sendAsync(ServiceRequest request) {
        try {
            Channel channel;
            String serviceName = request.getServiceName();
            //获得一个Channel
            if (serviceChannelMap.containsKey(serviceName)) {
                channel = serviceChannelMap.get(serviceName);
            } else {

                //从zookeeper拉取服务列表
                String providerAddress = getProviderAddress(serviceName);//提供者地址，由IP和端口号组成
                if (providerAddress == null) {
                    return ServiceResponse.ServiceNotFound(request.getId());
                }
                String[] address = providerAddress.split(":");
                String host = address[0];
                int port = Integer.parseInt(address[1]);

                //新建一条链路
                channel = bootstrap.connect(host, port).sync().channel();
            }
            ChannelPromise promise = (ChannelPromise) channel.write(request);
            PromiseResponse promiseResponse = new PromiseResponse(promise);
            promiseResponse.addListener(() -> serviceChannelMap.put(serviceName, channel));
            responseMap.put(request.getId(), promiseResponse);
            rpcContext.addPromise(promiseResponse);
            return null;
        } catch (InterruptedException e) {
            //TODO：处理异常
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 获得指定服务的一个服务器地址，这里使用了轮询法
     *
     * @param serviceName 服务的名称
     * @return 一个服务器地址，没有对应的服务提供者时返回null
     */
    private String getProviderAddress(String serviceName) {
        List<String> providerList = services.get(serviceName);
        if (providerList == null) {
            try {
                providerList = serviceRegistry.getProviderAddress(serviceName, providersWatcher);
                synchronized (services) {
                    if (services.get(serviceName) == null) {
                        services.put(serviceName, providerList);
                    }
                }
            } catch (KeeperException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (providerList == null || providerList.size() == 0) {
            return null;
        }
        int serverIndex = (round.getAndIncrement() % providerList.size());
        return providerList.get(serverIndex);

    }

    /**
     * 监听器，一旦服务节点的子节点发生了变化，说明有机器上线或下线
     * 这个时候需要更新services和servicesChannelMap
     */
    class ProvidersChangedWatcher implements Watcher {
        @Override
        public void process(WatchedEvent event) {
            if (event.getType() == Event.EventType.NodeChildrenChanged) {
                String servicePath = event.getWrapper().getPath();
                String serviceName = StringUtil.getServiceName(servicePath);
                try {
                    List<String> providerAddresses = serviceRegistry.getProviderAddress(serviceName, this);
                    services.put(serviceName, providerAddresses);
                    serviceChannelMap.remove(serviceName);
                } catch (KeeperException | InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("服务子节点被改变");//TODO:log
            }
        }
    }

    class RpcInClientHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf buf = (ByteBuf) msg;
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);

            ServiceResponse response = HessianUtil.bytesToResponse(bytes);
            long id = response.getId();
            PromiseResponse promiseResponse = responseMap.get(id);

            if (promiseResponse != null) {
                promiseResponse.setResponse(response);
            }
        }
    }
}
