package me.jesonlee.rpc.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import me.jesonlee.rpc.common.ServiceRequest;
import me.jesonlee.rpc.common.serialize.SerializeUtil;

/**
 * Created by JesonLee
 * on 2017/5/12.
 */
public class RequestEncoder extends ChannelOutboundHandlerAdapter {
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        ServiceRequest request = (ServiceRequest) msg;
        //将请求序列化为byte数组
        byte[] bytes = SerializeUtil.getBytes(request);
        ByteBuf buf = Unpooled.buffer(bytes.length + 4);
        buf.writeInt(bytes.length);
        buf.writeBytes(bytes);
        ctx.writeAndFlush(buf);

        //buf.release();TODO
    }
}
