package echo;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Created by JesonLee
 * on 2017/5/7.
 */
public class EchoClient {
    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new EchoClientHandler());
                    }
                });
        String host = "127.0.0.1";
        int port = 8080;
        bootstrap.connect(host, port);
    }
}

class EchoClientHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("成功连接到服务器");
        byte[] firstMessage = "hello".getBytes();//发送初始消息
        ByteBuf buffer = Unpooled.buffer(firstMessage.length);
        buffer.writeBytes(firstMessage);
        ChannelFuture future = ctx.writeAndFlush(buffer);
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                System.out.println("发送成功");
            }
        });
        ctx.flush();
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf resp = (ByteBuf) msg;
        byte[] bytes = new byte[resp.readableBytes()];
        resp.readBytes(bytes);
        String s = new String(bytes);
        System.out.println("来自服务端的消息："+s);
    }
}
