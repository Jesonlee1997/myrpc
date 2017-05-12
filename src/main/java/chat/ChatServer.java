package chat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

/**
 * Created by JesonLee
 * on 2017/5/11.
 */
public class ChatServer {

    private static Logger logger = LoggerFactory.getLogger(ChatServer.class);
    private Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        new ChatServer().startServer(8080);
    }

    public void startServer(int port) {
        logger.info("服务器开始启动");
        ServerBootstrap bootstrap = new ServerBootstrap();
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        bootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new FirstHandler());
                        pipeline.addLast(new OutputHandler());
                        pipeline.addLast(new InputHandler());

                    }
                });
        try {
            bootstrap.bind(port).sync();
            logger.info("服务器启动成功");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class FirstHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            String s = "hello, nice to meet you";
            ByteBuf buf = Unpooled.buffer(s.length());
            buf.writeBytes(s.getBytes());
            ctx.writeAndFlush(buf);
        }
    }

    private class InputHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf buf = (ByteBuf) msg;//原始二进制
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            logger.info("来自客户端的消息：" + new String(bytes));
            String resp = scanner.nextLine();
            logger.info("消息发往客户端：" + resp);
            ctx.write(resp);
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            ctx.flush();
        }
    }

    private class OutputHandler extends ChannelOutboundHandlerAdapter {
        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            String resp = (String) msg;
            byte[] response = resp.getBytes();
            ByteBuf buffer = Unpooled.buffer(response.length);
            buffer.writeBytes(response);
            ctx.writeAndFlush(buffer);
        }
    }
}



