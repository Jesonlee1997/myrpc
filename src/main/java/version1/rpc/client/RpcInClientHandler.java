package version1.rpc.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import version1.rpc.HessianUtil;
import version1.rpc.ServiceRequest;
import version1.rpc.ServiceResponse;

import java.io.IOException;

/**
 * Created by JesonLee
 * on 2017/5/12.
 */
public class RpcInClientHandler extends ChannelInboundHandlerAdapter {

    private ServiceResponse response;
    private ChannelPromise promise;
    private ChannelHandlerContext context;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        context = ctx;
        System.out.println("连接成功");
    }

    public ChannelPromise send(ServiceRequest request) throws IOException {
        byte[] bytes = HessianUtil.requestToBytes(request);
        ByteBuf buf = Unpooled.buffer(bytes.length);
        buf.writeBytes(bytes);
        promise = context.write(buf).channel().newPromise();
        return promise;
    }

    @Override//从服务端接收返回结果
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


}
