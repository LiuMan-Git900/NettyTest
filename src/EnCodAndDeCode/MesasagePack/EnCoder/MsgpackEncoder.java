package EnCodAndDeCode.MesasagePack.EnCoder;

import Business.Obj.CallInfo;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;

/**
 * 编码器：将object对象变成byte数组。编码后的byte数组用ByteBuf存储，协议层面的代码
 * */
public class MsgpackEncoder extends MessageToByteEncoder<CallInfo> {

    @Override
    protected void encode(ChannelHandlerContext ctx, CallInfo msg, ByteBuf out) throws Exception {
        // 将TestInfo作为编解吗技术的统一传输对象
        // MessageBufferPacker 这编码器将内嵌了一个outputstream用于存储序和管理列化后的byte[]，先创建出stream然后将流内buffer放出来给Packer直接用
        // 对象层面层面的编码技术
        MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
        // 开头文件
        msg.EncodeByMsgPack(packer);
        byte[] bytes = packer.toByteArray();
        // netty传输层面统一用ByteBuf，我们项目框架也是在Object-byte[]-ByteBuf-ByteBuf-byte[]-Object这样转化
        // 编解码技术，协议层（object-byte[]-byte[]-object）
        // 传输层,ByteBuf，这个是netty统一的传输层面的处理
        out.writeBytes(bytes);
    }
}
