package Business.Obj;

import org.msgpack.core.MessagePacker;
import org.msgpack.core.MessageUnpacker;

import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
/**
 * 核心问题一：多个自由参数如何在服务器和客户端上自由转移。自由的序列化和反序列化
 *          1.基本数据类型可以快速的序列化和反序列化，多个对象呢？List对象呢？或者说Object[]呢？
 * */
public class CallInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    public String moduleName;
    public int methodID;
    public long param;
    public CallInfo() {}

    public void setMethodID(int methodID) {
        this.methodID = methodID;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public void setParam(long param) {
        this.param = param;
    }

    public byte[] EncodeByBuff() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byte[] nameBytes = moduleName.getBytes();
        byteBuffer.putInt(nameBytes.length);
        byteBuffer.put(nameBytes);
        byteBuffer.putInt(methodID);
        byteBuffer.putLong(param);
        byteBuffer.flip();
        byte[] ret  = new byte[byteBuffer.remaining()];
        byteBuffer.get(ret);
        return ret;
    }
    public void EncodeByMsgPack(MessagePacker messagePacker) throws IOException {
        messagePacker.packString(moduleName);
        messagePacker.packInt(methodID);
        messagePacker.packLong(param);
    }

    public void DecodeByMsgPack(MessageUnpacker messageUnPacker) throws IOException {
        moduleName = messageUnPacker.unpackString();
        methodID = messageUnPacker.unpackInt();
        param = messageUnPacker.unpackLong();
    }

}
