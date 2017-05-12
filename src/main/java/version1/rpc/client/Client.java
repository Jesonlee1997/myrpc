package version1.rpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import version1.rpc.ServiceRequest;
import version1.rpc.ServiceResponse;

import java.io.IOException;

/**
 * RPC框架的实现端
 * Created by JesonLee
 * on 2017/5/12.
 */
public class Client {
    private String host = "127.0.0.1";
    private int port = 8080;

    private RpcInClientHandler handler = new RpcInClientHandler();

    private Bootstrap bootstrap;

    public Client(String host, int port) {
        this();
        this.host = host;
        this.port = port;
    }

    public Client() {
        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new RpcOutClientHandler());

                        pipeline.addLast(handler);
                    }
                });
        this.bootstrap = bootstrap;
    }

    public static void main(String[] args) {
        ServiceRequest request = new ServiceRequest();
        request.setServiceName("version1.rpc.Calculator");
        request.setMethodName("add");
        request.setArgs(10, 20);
        Client client = new Client();
        ServiceResponse response = client.send(request);
        System.out.println(response.getResult());
    }

    public ServiceResponse send(ServiceRequest request) {
        try {
            bootstrap.connect(host, port).sync();
            ChannelPromise promise = handler.send(request);
            promise.await();
        } catch (IOException e) {
            System.out.println("发送消息失败");
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return handler.getResponse();
    }
}
