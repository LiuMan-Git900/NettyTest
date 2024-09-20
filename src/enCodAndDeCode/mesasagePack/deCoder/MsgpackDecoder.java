package enCodAndDeCode.mesasagePack.deCoder;

import enCodAndDeCode.mesasagePack.other.MsgToObjFunction;
import business.obj.CallInfo;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;

import java.util.List;
/**
 * 编解码技术-解码器
 * 将传输层传输过来的byteBuf【byte[]】的数据解码成为我们自己的Object数据
 * */
public class MsgpackDecoder extends MessageToMessageDecoder<ByteBuf> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        byte[] buff = new byte[msg.readableBytes()];
        msg.readBytes(buff);
        MessageUnpacker messageUnpacker = MessagePack.newDefaultUnpacker(buff);
        CallInfo testInfo = MsgToObjFunction.GetObjectById(1);
        testInfo.DecodeByMsgPack(messageUnpacker);
        out.add(testInfo);
    }
}
