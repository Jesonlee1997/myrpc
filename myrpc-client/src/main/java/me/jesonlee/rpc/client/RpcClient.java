package me.jesonlee.rpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import me.jesonlee.rpc.common.ServiceRegistry;
import me.jesonlee.rpc.common.ServiceRequest;
import me.jesonlee.rpc.common.ServiceResponse;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * RPC框架的调用端
 * Created by JesonLee
 * on 2017/5/12.
 */
public class RpcClient {
    private Bootstrap bootstrap;
    private final Map<String, List<String>> services = new ConcurrentHashMap<>();
    private ServiceRegistry serviceRegistry = new ServiceRegistry("192.168.56.101:2181");
    private Watcher providersWatcher = new ProvidersChangedWatcher();//注册在zooKeeper上的监听器
    private AtomicInteger round = new AtomicInteger(0);//轮询的号码
    private AtomicLong requestId = new AtomicLong(0);
    private final Map<String, Channel> seviceChannels = new ConcurrentHashMap<>();

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

    public ServiceResponse send(ServiceRequest request) {
        request.setId(requestId.getAndIncrement());

        Channel channel;
        RpcInClientHandler handler = null;
        String serviceName = request.getServiceName();
        //TODO:路由
        String providerAddress = getProviderAddress(serviceName);//提供者地址，由IP和端口号组成
        if (providerAddress == null) {
            return ServiceResponse.ServiceNotFound(request.getId());
        }
        String[] address = providerAddress.split(":");
        String host = address[0];
        int port = Integer.parseInt(address[1]);
        try {
            //新建一条链路
            channel = bootstrap.connect(host, port).sync().channel();
            handler = (RpcInClientHandler) channel.pipeline().get("handler");
            handler.setChannel(channel);
            ChannelPromise promise = handler.send(request);
            boolean success = promise.await(2000);//如果为false说明调用超时
            if (!success) {//只有调用成功才会保存服务名和服务器列表的映射
                return ServiceResponse.ServiceNotFound(request.getId());
            }
            //TODO:将服务名和通道放入调用列表中
            seviceChannels.put(serviceName, channel);

        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        return handler.getResponse();
    }

    /**
     * 获得指定服务的一个服务器地址，这里使用了轮询法
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
     * 监听器，一旦服务节点的子节点发生了变化，说明有机器上线或下线，这个时候需要更新
     */
    class ProvidersChangedWatcher implements Watcher {
        @Override
        public void process(WatchedEvent event) {
            if (event.getType() == Event.EventType.NodeChildrenChanged) {
                String servicePath = event.getWrapper().getPath();
                try {
                    List<String> providerAddresses = serviceRegistry.getProviderAddress(servicePath, this);
                    services.put(servicePath, providerAddresses);
                } catch (KeeperException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
