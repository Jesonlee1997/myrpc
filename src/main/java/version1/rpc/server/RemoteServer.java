package version1.rpc.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import version1.rpc.Calculator;

/**
 * Created by JesonLee
 * on 2017/5/11.
 */
public class RemoteServer {
    private static ServiceManager serviceManager = ServiceManager.getInstance();

    public static void main(String[] args) {
        //TODO:注册服务
        serviceManager.registerService(Calculator.class, CalculatorImpl.class);
        try {
            new RemoteServer().startServer(8080);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public  void startServer(int port) throws InterruptedException {
        ServerBootstrap bootstrap = new ServerBootstrap();
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        bootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new RpcHandler());
                    }
                });
        ChannelFuture future = bootstrap.bind(port);
        future.addListener((ChannelFutureListener) future1 -> System.out.println("服务器绑定到" + port + "端口"));
    }

}
