package network.client;

import enCodAndDeCode.mesasagePack.deCoder.MsgpackDecoder;
import enCodAndDeCode.mesasagePack.enCoder.MsgpackEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;

public class FirstClient {
    public void Connection(int Port) {
        // 配置客户端的nio线程组
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        try{
            Bootstrap clientBootStrap = new Bootstrap();
            clientBootStrap.group(eventLoopGroup)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            InitChannelOfMessagePack(ch);
                        }
                    });
            ChannelFuture f = clientBootStrap.connect("localhost",8080).sync();
            f.channel().closeFuture().sync();
        } catch (Exception e){
            System.out.println("客户段链接失败");
        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }

    public static void InitChannel1(Channel channel) {
        ByteBuf delimiter =  Unpooled.copiedBuffer("#_".getBytes());
        channel.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, delimiter))
                .addLast(new StringDecoder())
                .addLast(new MyClientChannelHandler());
    }

    public static void InitChannelOfMessagePack(Channel channel) {
        channel.pipeline()
                .addLast(new LengthFieldBasedFrameDecoder(65536,0,2,0,2))
                .addLast(new MsgpackDecoder())
                .addLast(new LengthFieldPrepender(2))
                .addLast(new MsgpackEncoder())
                .addLast(new MyClientChannelHandler());
    }

    public static void InitChannelOfProtoBuff(Channel channel) {
        channel.pipeline()
                .addLast(new LengthFieldBasedFrameDecoder(65536,0,2,0,2))
                .addLast(new LengthFieldPrepender(2))
                .addLast(new MyClientChannelHandler());
    }
}
