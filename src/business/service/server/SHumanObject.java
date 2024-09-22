package business.service.server;

import business.core.Connection;
import business.core.HumanWork;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.Parser;
import com.lzp.netty.protobuf.ProtobufTest;
import gen.MsgIds;
import gen.tool.GenUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

import java.util.concurrent.atomic.AtomicLong;

public class SHumanObject {
    public static AtomicLong IDAllocator = new AtomicLong(1);
    /** ID */
    public final long id;
    /** netty的链接对象-中间层：netty将数据放入到这，humanObject去取去处理。分离netty和我门商业服务器。
     * 有很多好处    1：组件分离：netty代码不持有humanObject对象，netty组件可以打包，netty只做数据传输
     *             2：线程分离：netty线程不持有我们humanObject对象 逻辑线程和netty线程不同时控制这个大额对象。
     *             3：门对象：可以创建门对象，对登录进行过滤，登录和具体服务功能分流*/
    private Connection connection;
    private HumanWork ownerHumanWork;




    public SHumanObject(Connection connection) {
        this.id= IDAllocator.incrementAndGet();
        this.connection = connection;

    }

    public void pulse() {
        // 消息处理
        byte[] msgBytes ;
        while ((msgBytes = connection.pollMessage()) != null) {
            handlerMsg(msgBytes);
        }
    }
    public void initModule() {
    }
    /** 编解码和分发 */
    public void handlerMsg(byte[] bytes){
        try {
            // 编解码技术
            if(bytes == null || bytes.length == 0) {
                return;
            }
//        ByteBuf byteBuf = Unpooled.copiedBuffer(bytes);
//        int msgId = byteBuf.readInt();
//        byte[] des = new byte[bytes.length - 4];
//        byteBuf.readBytes(des);
//        Parser parser = (Parser) MsgIds.getParser(msgId);
//        if(parser == null) {
//            return;
//        }
//        GeneratedMessage message = (GeneratedMessage) parser.parseFrom(des,0, des.length);
//        // 消息分发
//        HumanObjectHandlerMsg(msgId, message);

            int msgId = GenUtils.bytesToBigEndian32(bytes, 0);
            Parser parser = (Parser) MsgIds.getParser(msgId);
            if(parser == null) {
                return;
            }
            GeneratedMessage message = (GeneratedMessage) parser.parseFrom(bytes,4, bytes.length - 4);
            // 消息分发
            HumanObjectHandlerMsg(msgId, message);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    // -------------------------------------- 发送器
    public void sendMsg(Message message) {
        connection.sendMsg(message);
    }


    // TODO 优化-将解析器从humanObject中抽离出来
    // -------------------------------解析器 start---------------------------------------------------------------------

    // -------------------------------解析器 end---------------------------------------------------------------------
    // ---------------------------------处理方法 start-------------------------------------------------------------------
    // TODO 问题：每次轮询太慢了，可不可以抽离出来做一个map映射。注册map，调用map
    // 将功能处理从humanObject中抽离出去，因为太多了
    private void HumanObjectHandlerMsg(int msgId , GeneratedMessage message) {
        if (msgId == MsgIds.CSSceneEnter) {
            onCSSceneEnter(message);
            return;
        }

    }
    private void onCSSceneEnter(GeneratedMessage message) {
        ProtobufTest.CSSceneEnter csSceneEnter = (ProtobufTest.CSSceneEnter) message;
        ProtobufTest.DVector3 vector3 = csSceneEnter.getPos();
        System.out.println("服务器接受到客户端位置:" + "X=" + vector3.getX() + "Y=" + vector3.getX() + "Z=" + vector3.getZ());
        sendTest();
    }
    private void sendTest() {
        ProtobufTest.SCSceneEnter.Builder builder = ProtobufTest.SCSceneEnter.newBuilder();
        builder.setEnter(true);
        sendMsg(builder.build());
    }



    public long getId() {
        return id;
    }
}
