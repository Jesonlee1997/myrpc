package me.jesonlee.rpc.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import me.jesonlee.rpc.common.ServiceResponse;
import me.jesonlee.rpc.common.serialize.Serialization;

/**
 * Created by JesonLee
 * on 2017/5/13.
 */
public class ResponseEncoder extends ChannelOutboundHandlerAdapter {
    private static Serialization serialization = Serialization.getDefaultSerialization();

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        ServiceResponse result = (ServiceResponse) msg;
        byte[] response = serialization.getBytes(result);
        ByteBuf resp = Unpooled.buffer(response.length + 4);

        resp.writeInt(response.length);
        resp.writeBytes(response);
        ctx.writeAndFlush(resp);
    }
}
