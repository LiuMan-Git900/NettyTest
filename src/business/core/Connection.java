package business.core;

import com.google.protobuf.Message;
import gen.MsgIds;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

import java.util.concurrent.LinkedBlockingQueue;

public class Connection {
    /** 计数用 */
    private long id;
    /** 通缉基类 */
    private Channel channel;
    /** 交互数据缓存 */
    private LinkedBlockingQueue<byte[]> linkedBlockingQueue = new LinkedBlockingQueue<>();
    public Connection(long id, Channel channel) {
        this.id = id;
        this.channel = channel;
    }



    /** 数据入队 */
    public void puMessage(byte[] message) {
        try {
            linkedBlockingQueue.put(message);
        } catch (Exception e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }
    /** 数据出队 */
    public byte[] pollMessage() {
        return linkedBlockingQueue.poll();
    }
    /** 发送数据 */
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

    public long getId() {
        return id;
    }
}

