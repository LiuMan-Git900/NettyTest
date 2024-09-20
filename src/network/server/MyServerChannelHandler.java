package network.server;

import business.obj.CallInfo;
import business.service.server.SHumanObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.util.concurrent.atomic.AtomicInteger;
/**
 * 每一个
 * */
public class MyServerChannelHandler extends ChannelInboundHandlerAdapter {
    // ID 分配器
    public static AtomicInteger ID = new AtomicInteger(1);
    // 商业服务
    public SHumanObject service;
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("服务器创建链接成功  ChannelID= " + ctx.channel().id().toString() + " ID=" + ID.get());
        service = new SHumanObject(ctx.channel(), ID.getAndIncrement());
        service.initModule();
        // 因为没有业务逻辑支撑(自己的商业业务逻辑支持，service自己要做的一系列事情)，所以我们只能搭建简单的命令模式，服务器和客户端的一问一答模式
        // 商业服务器框架部署，商业服务器商业服务提供方式
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 在这里我们提供商业服务
        service.handlerTestInfo((CallInfo) msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println("连接关闭 Server channelInactive Success ChannelID = " + ctx.channel().id().toString());
        ctx.channel().close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        try {
            cause.printStackTrace();
            ctx.close();
        } finally {
//             异常被放弃处理，不再进入下一个处理器
            ReferenceCountUtil.release(cause);
        }
    }
}
