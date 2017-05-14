package me.jesonlee.rpc.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import me.jesonlee.rpc.common.HessianUtil;
import me.jesonlee.rpc.common.ServiceResponse;

/**
 * Created by JesonLee
 * on 2017/5/13.
 */
public class ResponseHandler extends ChannelOutboundHandlerAdapter {
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        ServiceResponse result = (ServiceResponse) msg;
        byte[] response = HessianUtil.responseToBytes(result);
        ByteBuf resp = Unpooled.buffer(response.length + 4);
        resp.writeInt(response.length);

        resp.writeBytes(response);
        ctx.writeAndFlush(resp);
    }
}
