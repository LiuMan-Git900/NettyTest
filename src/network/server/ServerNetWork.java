package network.server;

import mesasagePack.deCoder.MsgpackDecoder;
import mesasagePack.enCoder.MsgpackEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import network.client.ClientNetWorkHandler;

import java.nio.charset.Charset;
/**
 * 服务器通信组件
 * */
public class ServerNetWork {
    public void bind(int port) {
        // nio的线程组
        EventLoopGroup boos = new NioEventLoopGroup();
        EventLoopGroup works = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boos, works)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ChannelOfMessageProtoBuff(ch);
                        }
                    });
            ChannelFuture future = serverBootstrap.bind(port).sync();
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            System.out.println("Exception");
        } finally {
            boos.shutdownGracefully();
            works.shutdownGracefully();
        }
    }

    public static void Channel1(Channel channel){
        ByteBuf delimiter =  Unpooled.copiedBuffer("#_".getBytes());
        channel.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
        channel.pipeline().addLast(new StringDecoder(Charset.forName("UTF-8")));
        channel.pipeline().addLast(new ServerNetWorkHandler());
    }
    public static void ChannelOfMessagePack(Channel channel) {
        channel.pipeline().addLast(new LengthFieldBasedFrameDecoder(65535,0,2,0,2));
        channel.pipeline().addLast(new MsgpackDecoder());
        channel.pipeline().addLast(new LengthFieldPrepender(2));
        channel.pipeline().addLast(new MsgpackEncoder());
        channel.pipeline().addLast(new ServerNetWorkHandler());
    }

    public static void ChannelOfMessageProtoBuff(Channel channel) {
        channel.pipeline()
                .addLast(new LengthFieldBasedFrameDecoder(65536,0,2,0,2))
                .addLast(new LengthFieldPrepender(2))
                .addLast(new ServerNetWorkHandler());

    }
}
