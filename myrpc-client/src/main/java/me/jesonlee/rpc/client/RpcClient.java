package me.jesonlee.rpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import me.jesonlee.rpc.common.ServiceRequest;
import me.jesonlee.rpc.common.ServiceResponse;

import java.io.IOException;

/**
 * RPC框架的实现端
 * Created by JesonLee
 * on 2017/5/12.
 */
public class RpcClient {
    private String host = "127.0.0.1";
    private int port = 8080;

    private Bootstrap bootstrap;


    public RpcClient(String host, int port) {
        this();
        this.host = host;
        this.port = port;
    }

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

                        pipeline.addLast("handler", new RpcInClientHandler());
                    }
                });
        this.bootstrap = bootstrap;
    }

    public ServiceResponse send(ServiceRequest request) {
        Channel channel = null;
        RpcInClientHandler handler = null;
        try {
            //重新建立一条链路
            channel = bootstrap.connect(host, port).sync().channel();

            handler = (RpcInClientHandler) channel.pipeline().get("handler");
            handler.setChannel(channel);
            ChannelPromise promise = handler.send(request);
            boolean success = promise.await(2000);//如果为false说明调用超时
            if (success) {//只有调用成功才会保存服务名和服务器列表的映射
                //TODO:将服务名和
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            if (channel != null) {
                try {
                    channel.close().sync();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return handler.getResponse();
    }
}
