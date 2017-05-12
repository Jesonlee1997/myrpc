package version1.rpc.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import version1.rpc.HessianUtil;
import version1.rpc.ServiceRequest;
import version1.rpc.ServiceResponse;

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
        ServiceResponse result = serviceManager.invokeService(serviceRequest);
        byte[] response = HessianUtil.responseToBytes(result);
        ByteBuf resp = Unpooled.buffer(response.length);
        resp.writeBytes(response);
        ctx.writeAndFlush(resp);
    }
}
