package business.service.client;

import business.obj.CallInfo;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

import com.google.protobuf.Parser;
import com.lzp.netty.protobuf.ProtobufTest;
import gen.tool.GenUtils;
import gen.MsgIds;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;


import java.util.concurrent.atomic.AtomicInteger;
/**
 * 为什么商业服务器要考虑线程模型，因为很简单，我们有心跳，需要提供心跳服务,心跳服务就是将humanObject挂载在服务器线程上。引出逻辑线程的必要性
 *  这个时候就需要考虑线程负载均衡了，将不同的humanObject挂载到线程上去。
 *  同时设置一个connection对象作为中间对象：netty线程将byte数据存到connection对象中
 *  服务器逻辑线程将humanObject心脏中处理connection中的数据。linkeBolckingQueue<byte[]>
 *  connection就是横跨两个线程的中间对象。目的是为了保证线程的工作区分：io线程只做数据传输，服务器逻辑线程进行编码解码
 *  编解码完成后就进行数据的分发，开始设计到功能模块了，也就是我们程序的价值了
 * */
public class CHumanObject {
    public static AtomicInteger ID = new AtomicInteger(1);
    public static AtomicInteger pos = new AtomicInteger(1);
    private Channel channel;
    public final int id;

    public CHumanObject(Channel channel) {
        this.channel = channel;
        this.id= ID.incrementAndGet();
    }
    // TODO
    public void pulse() {

    }
    public void initModule() {
        sendTest();
    }
    /** 编解码和分发 */
    public void handlerMsg(byte[] bytes) throws InvalidProtocolBufferException {
        // 编解码技术
        if(bytes == null || bytes.length == 0) {
            return;
        }
        int msgId = GenUtils.bytesToBigEndian32(bytes, 0);
        Parser parser = (Parser) MsgIds.getParser(msgId);
        if(parser == null) {
            return;
        }
        GeneratedMessage message = (GeneratedMessage) parser.parseFrom(bytes,4, bytes.length - 4);

        // 消息分发
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
    // TODO 问题：每次轮询太慢了，可不可以抽离出来做一个map映射。注册map，调用map
    // 将功能处理从humanObject中抽离出去，因为太多了
    private void HumanObjectHandlerMsg(int msgId , GeneratedMessage message) {
        if(msgId == MsgIds.SCSceneEnter) {
            onSCSceneEnter(message);
            return;
        }
    }


    private void onSCSceneEnter(GeneratedMessage message) {
        System.out.println("客户端：接收到服务器数据");
        sendTest();
    }

    private void sendTest() {
        ProtobufTest.CSSceneEnter.Builder builder = ProtobufTest.CSSceneEnter.newBuilder();
        ProtobufTest.DVector3.Builder vector = ProtobufTest.DVector3.newBuilder();
        vector.setX(pos.intValue());
        vector.setY(pos.intValue());
        vector.setZ(pos.intValue());
        builder.setPos(vector);
        sendMsg(builder.build());
        pos.incrementAndGet();
    }

    // ---------------------------------处理方法 end-------------------------------------------------------------------
}
