package me.jesonlee.rpc.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import me.jesonlee.rpc.common.Calculator;

/**
 * Created by JesonLee
 * on 2017/5/11.
 */
public class RpcProvider {
    private ServiceManager serviceManager = ServiceManager.getInstance();

    /**
     * 启动服务提供者
     * @param port 服务器运行的端口号
     * @throws InterruptedException
     */
    public void startServer(int port) throws InterruptedException {
        ServerBootstrap bootstrap = new ServerBootstrap();
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        bootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new ResponseHandler());

                        pipeline.addFirst(new LengthFieldBasedFrameDecoder(
                                10000000,
                                0,
                                4,
                                0,
                                4));
                        pipeline.addLast(new RpcHandler());
                    }
                });
        ChannelFuture future = bootstrap.bind(port);

        Service service = new Service(Calculator.class, CalculatorImpl.class);
        serviceManager.registerService(service, port);
        future.addListener((ChannelFutureListener) future1 -> System.out.println("服务器绑定到" + port + "端口"));
    }
}
