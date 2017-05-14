package me.jesonlee.rpc.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import me.jesonlee.rpc.common.HessianUtil;
import me.jesonlee.rpc.common.ServiceRequest;

/**
 * Created by JesonLee
 * on 2017/5/12.
 */
public class RpcHandler extends ChannelInboundHandlerAdapter {
    private ServiceManager serviceManager = ServiceManager.getInstance();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        ServiceRequest serviceRequest = HessianUtil.bytesToRequest(bytes);
        serviceManager.addServiceTask(serviceRequest, ctx.channel());
    }
}
