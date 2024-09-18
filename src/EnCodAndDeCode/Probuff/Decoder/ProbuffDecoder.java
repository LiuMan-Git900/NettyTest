package EnCodAndDeCode.Probuff.Decoder;

import EnCodAndDeCode.Probuff.MessageHandler;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.Parser;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

public class ProbuffDecoder extends MessageToMessageDecoder<ByteBuf> {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        int byteLength = msg.array().length;
        byte[] bytes = new byte[byteLength - Integer.BYTES];
        // 这里我们length是大端方式进行编码的，所以解码需要大端方式
        // protobuf对象的编码方式是按自己来的，所以我们不用去管大端小端
        // 大端小端，protobuf都是编解码方式

        // 长度读取，java默认是大端。
        int messageId = msg.readInt();
        msg.readBytes(bytes);
        // 解析器的获取
        Parser parser = GetParser(messageId);
        if(parser == null) {
            return;
        }
        GeneratedMessage message = (GeneratedMessage) parser.parseFrom(bytes);
        // 解析完成，交给消息分发器，让消息分发器交给功能处理系统
        // 消息分发器可以用监听者模式设计
        MessageHandler.Handler(messageId, message);
    }

    private Parser GetParser(int messageId) {
        switch (messageId) {
//            case 1 : return MsgOptions3.MyMessage.parser();
//            case 2 : return MsgOptions3.MySecondMessage.parser();
            default: return null;
        }
    }

}
