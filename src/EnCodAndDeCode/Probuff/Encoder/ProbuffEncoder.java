package EnCodAndDeCode.Probuff.Encoder;

import com.google.protobuf.GeneratedMessage;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class ProbuffEncoder extends MessageToByteEncoder<GeneratedMessage> {
    @Override
    protected void encode(ChannelHandlerContext ctx, GeneratedMessage msg, ByteBuf out) throws Exception {
        // 先写然后去暗对我们项目的编码器，看我们是否正确
        byte[] msgBytes = msg.toByteArray();
        int msgId = GetMesId(msg);
        if(msgId == 0) {
            return;
        }
        // 将int数据编码-大端编码
        byte[] msgIdBytes = new byte[4];
        bigEndian32ToBytes(msgIdBytes, 0, msgId);
        out.writeBytes(msgIdBytes);
        out.writeBytes(msgBytes);
    }

    public static void bigEndian32ToBytes(byte[] bytes,int offset, int value) {
        bytes[offset] = (byte) ((value >> 24) & 0xff);
        bytes[offset + 1] = (byte) ((value >> 16) & 0xff);
        bytes[offset + 2] = (byte) ((value >> 8) & 0xff);
        bytes[offset + 3] = (byte)  ((value >> 0 ) & 0xff);
    }

    public static int GetMesId(GeneratedMessage message) {
//        if(message instanceof MsgOptions3.MyMessage) {
//            return 1;
//        }
//        if(message instanceof MsgOptions3.MySecondMessage) {
//            return 2;
//        }
        return 0;
    }
}
