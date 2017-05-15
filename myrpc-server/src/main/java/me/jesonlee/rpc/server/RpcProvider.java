package me.jesonlee.rpc.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import me.jesonlee.rpc.common.Calculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by JesonLee
 * on 2017/5/11.
 */
public class RpcProvider {
    private ServiceManager serviceManager = ServiceManager.getInstance();
    private static Logger logger = LoggerFactory.getLogger(RpcProvider.class);

    /**
     * 启动服务提供者
     * @param port 服务器运行的端口号
     */
    public void startServer(int port) {
        ServerBootstrap bootstrap = new ServerBootstrap();
        EventLoopGroup boss = new NioEventLoopGroup(2);
        EventLoopGroup worker = new NioEventLoopGroup();
        bootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.SO_BACKLOG, 8192)
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
        try {
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
