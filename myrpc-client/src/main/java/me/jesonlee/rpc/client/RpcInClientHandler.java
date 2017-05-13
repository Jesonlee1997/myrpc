package me.jesonlee.rpc.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import me.jesonlee.rpc.common.HessianUtil;
import me.jesonlee.rpc.common.ServiceRequest;
import me.jesonlee.rpc.common.ServiceResponse;

import java.io.IOException;

/**
 * Created by JesonLee
 * on 2017/5/12.
 */
public class RpcInClientHandler extends ChannelInboundHandlerAdapter {

    private ServiceResponse response;
    private ChannelPromise promise;
    private Channel channel;

    /**
     * 发送请求到Server
     * @param request
     * @return
     * @throws IOException
     */
    public ChannelPromise send(ServiceRequest request) throws IOException {
        synchronized (this) {
            promise = channel.newPromise();
            channel.write(request, promise);
            return promise;
        }

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);

        response = HessianUtil.bytesToResponse(bytes);
        promise.setSuccess();
    }

    public ServiceResponse getResponse() {
        return response;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
