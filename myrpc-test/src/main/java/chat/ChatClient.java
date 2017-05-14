package chat;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

/**
 * Created by JesonLee
 * on 2017/5/11.
 */
public class ChatClient {
    private Scanner scanner = new Scanner(System.in);
    private static Logger logger = LoggerFactory.getLogger(ChatClient.class);

    public static void main(String[] args) {
        new ChatClient().startClient("127.0.0.1", 8080);
    }

    private void startClient(String remoteAddress, int port) {
        logger.info("客户端开始启动");
        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new OutputHandler());
                        pipeline.addLast(new InputHandler());
                    }
                });
        try {
            bootstrap.connect(remoteAddress, port).sync();
            logger.info("客户端启动成功");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class InputHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf buf = (ByteBuf) msg;//原始二进制
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            logger.info("来自服务端的消息：" + new String(bytes));

            String resp = scanner.nextLine();
            logger.info("消息发往服务端：" + resp);
            ctx.write(resp);
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
