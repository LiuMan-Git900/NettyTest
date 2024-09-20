package network.server;

import enCodAndDeCode.mesasagePack.deCoder.MsgpackDecoder;
import enCodAndDeCode.mesasagePack.enCoder.MsgpackEncoder;
import enCodAndDeCode.Probuff.Decoder.ProbuffDecoder;
import enCodAndDeCode.Probuff.Encoder.ProbuffEncoder;
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

import java.nio.charset.Charset;

public class FirstServer {
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
                            ChannelOfMessagePack(ch);
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
        channel.pipeline().addLast(new MyServerChannelHandler());
    }
    public static void ChannelOfMessagePack(Channel channel) {
        channel.pipeline().addLast(new LengthFieldBasedFrameDecoder(65535,0,2,0,2));
        channel.pipeline().addLast(new MsgpackDecoder());
        channel.pipeline().addLast(new LengthFieldPrepender(2));
        channel.pipeline().addLast(new MsgpackEncoder());
        channel.pipeline().addLast(new MyServerChannelHandler());
    }

    public static void ChannelOfMessageProtoBuff(Channel channel) {
        // 我们的protobuff编码和解码不放在nettychannnel里面
        // 因为protobuff协议会发生变动，而传输数据是不变动的，所以可以将netty变成一个传输byte序列的不变的工具
        // 而变动的协议处理和协议处理后的功能处理放在我们自己的商业服务器上
        channel.pipeline().addLast(new LengthFieldBasedFrameDecoder(65535, 0 ,2 , 0, 2));
        channel.pipeline().addLast(new LengthFieldPrepender(2));

    }
}
