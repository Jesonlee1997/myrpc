package me.jesonlee.rpc.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import me.jesonlee.rpc.common.ServiceRequest;
import me.jesonlee.rpc.common.serialize.Serialization;

/**
 * Created by JesonLee
 * on 2017/5/12.
 */
public class RpcHandler extends ChannelInboundHandlerAdapter {

    private ServiceManager serviceManager = ServiceManager.getInstance();
    private Serialization serialization = Serialization.getDefaultSerialization();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        ServiceRequest serviceRequest = serialization.getRequest(bytes);

        //只处理ServiceRequest类型的数据
        if (serviceRequest != null) {
            serviceManager.addServiceTask(serviceRequest, ctx.channel());
        }

        buf.release();
    }
}
