package echo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Created by JesonLee
 * on 2017/5/7.
 */
public class EchoServer {
    public static void main(String[] args) {
        EventLoopGroup waiter = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        final int port = 8080;
        bootstrap.group(waiter, worker)
                .channel(NioServerSocketChannel.class)
                .handler(new ChannelInitializer<ServerSocketChannel>() {
                    @Override
                    protected void initChannel(ServerSocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new ConnectHandler());
                    }
                })
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();

                        pipeline.addLast(new HelloHandler());
                    }
                });
        ChannelFuture future = bootstrap.bind(port);
        future.addListener((ChannelFutureListener) future1 -> System.out.println("服务器绑定到" + port + "端口"));
    }
}

class ConnectHandler extends ChannelInboundHandlerAdapter {

    @Override//在Channel连接成功时调用
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //System.out.println("有一个客户端连接到本机");
    }
}

class HelloHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        String s = new String(req);
        System.out.println("来自客户端的消息："+s);

        String response = "hi";
        byte[] resp = response.getBytes();
        ByteBuf buffer = Unpooled.buffer(req.length);
        buffer.writeBytes(resp);
        ChannelFuture future = ctx.write(buffer);
        //System.out.println("向客户端发送消息中");
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                //System.out.println("发送消息完毕");
            }
        });
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //System.out.println("Read完毕，刷新中");
        ctx.flush();//只有调用flush方法才能写入数组中
    }
}
