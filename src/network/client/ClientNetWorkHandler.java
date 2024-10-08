package network.client;

import business.service.client.CHumanObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ClientNetWorkHandler extends ChannelInboundHandlerAdapter {
    public CHumanObject humanService;
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client ChannelRegistered Success ChannelID = " + ctx.channel().id().toString());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        humanService = new CHumanObject(ctx.channel());
        humanService.initModule();
        System.out.println("链接服务器成功，请求服务器命令，其实这个时候需要用客户端界面来write数据了");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        byte[] copy = ByteBufUtil.getBytes(buf);
        humanService.handlerMsg(copy);
    }
}
