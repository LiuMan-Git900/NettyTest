package business.service.client;

import business.obj.CallInfo;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

import gen.tool.GenUtils;
import gen.MsgIds;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;


import java.util.concurrent.atomic.AtomicInteger;

public class CHumanObject {
    public static AtomicInteger ID = new AtomicInteger(1);
    private Channel channel;
    public final int id;

    public CHumanObject(Channel channel) {
        this.channel = channel;
        this.id= ID.incrementAndGet();
    }

    public void initModule() {
    }
    /**
     * 与客户端通讯的处理接口
     * 外部通讯处理接口，因为没有商业服务器本身的线程，所以使用Netty的IO线程，接收到数据后直接处理
     * 优化：使用商业服务器自己的线程来处理，将IO线程通讯的对象，放入到存储器中，等商业服务器自己线程处理
     * IO通信线程和商业服务器的服务器线程分离
     * */
    public void handlerTestInfo(CallInfo testInfo) {

    }
    // 动静分离
    // 把消息解码和消息编码放在HumanObject这里，是因为变化的是协议和协议处理，不变的是协议变成byte序列后的传输，所以netty只负责传输byte协议
    // 而编码解码放在我们自己的逻辑对象上，我们自己可编写部分上，动静分离
    // 所以我们消息处理和消息发生都是自己的逻辑，真正走到netty上的其实是已经序列化后的byte序列
    // netty在设计中不涉及到编解码技术，支付中传输，编解码技术机制放在我们自己这对象
    public void handlerMsg(byte[] bytes) throws InvalidProtocolBufferException {
        if(bytes == null || bytes.length == 0) {
            return;
        }
        int msgId = GenUtils.bytesToBigEndian32(bytes, 0);
        GeneratedMessage message = (GeneratedMessage) MsgIds.getParser(msgId);
        if(message == null) {
            return;
        }
        HumanObjectHandlerMsg(msgId, message);
    }
    // -------------------------------------- 发送器
    public void sendMsg(Message message) {
        int msgId = MsgIds.getMsgId(message.getClass());
        if(msgId == 0) {
            System.out.println("未找到对应的消息id");
            return;
        }
        if (this.channel.isActive() && this.channel.isWritable()) {
            byte[] bytes = message.toByteArray();
            ByteBuf buf = Unpooled.buffer(bytes.length + 4);
            buf.writeInt(msgId);
            buf.writeBytes(bytes);
            channel.write(buf);
            channel.flush();
        }
    }


    // TODO 优化-将解析器从humanObject中抽离出来
    // -------------------------------解析器 start---------------------------------------------------------------------

    // -------------------------------解析器 end---------------------------------------------------------------------
    // ---------------------------------处理方法 start-------------------------------------------------------------------
    // TODO 每次沦陷太慢了，可不可以抽离出来做一个map映射。注册map，调用map
    // 将功能处理从humanObject中抽离出去，因为太多了
    // 这么设计是解决什么问题，有什么精妙之处
    private void HumanObjectHandlerMsg(int msgId , GeneratedMessage message) {
        if (msgId == 1) {
            OnMyMessage(message);
            return;
        }
        if(msgId == 2) {
            OnMySecondMessage(message);
            return;
        }
    }
    private void OnMyMessage(GeneratedMessage message) {
//        MsgOptions3.MyMessage msg  = (MsgOptions3.MyMessage) message;
//        long objId = msg.getObjId();
//        System.out.println("server:netty网络通信接收到 MyMessage 协议，反序列化正确，交给功能系统处理协议. objId = " + objId);
        // TODO
        // 服务器接受到MyMessage协议就给客户端发送MySecondMessage协议
        // 客户端接受到MySecondMessage协议就给服务器发送MyMessage协议
        // 然后客户端先发送MyMessage协议给服务器
    }

    private void OnMySecondMessage(GeneratedMessage message) {
//        MsgOptions3.MySecondMessage msg = (MsgOptions3.MySecondMessage) message;
//        long objId = msg.getObjId();
//        System.out.println("server:netty网络通信接收到 MySecondMessage 协议，反序列化正确，交给功能系统处理协议. objId = " + objId);
    }

    // ---------------------------------处理方法 end-------------------------------------------------------------------
}
