package network.server;

import business.core.Connection;
import business.core.HumanWork;
import business.service.server.SHumanObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import startup.ServerStartup;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 每一个
 * */
public class ServerNetWorkHandler extends ChannelInboundHandlerAdapter {
    private static AtomicInteger integer = new AtomicInteger(1);
    // 商业服务
    public Connection connection;
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("服务器创建链接成功  ChannelID= " + ctx.channel().id().toString());
        Connection connection = new Connection(integer.getAndIncrement(), ctx.channel());
        this.connection = connection;
        // Connection创建成功了，需要将connection绑定到humanObect中，同时将humanObject绑定到HumanWork中
        // 这里目前没有门对象，也没有DB数据加载所以跳过一些逻辑直接创建HumanObject
        SHumanObject humanObject = new SHumanObject(connection);
        // 将humanobject加入到心跳中
        Collection collection = ServerStartup.currentNode.getAllHumanWork();
        int index = (int) connection.getId() % collection.size();
        HumanWork humanWork = ServerStartup.currentNode.getHumanWork("HumanWork"+index);
        humanWork.addHumanObject(humanObject);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        byte[] copy = ByteBufUtil.getBytes(buf);
        connection.puMessage(copy);
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
