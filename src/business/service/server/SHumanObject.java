package business.service.server;

import business.obj.CallInfo;
import io.netty.channel.Channel;

public class SHumanObject {
    private Channel channel;
    public final int id;
    // 功能模块类

    public SHumanObject(Channel channel, int id) {
        this.channel = channel;
        this.id= id;
    }

    public void initModule() {

    }

    public void handlerTestInfo(CallInfo testInfo) {

    }

    public void sendMsg(CallInfo callInfo) {
        channel.writeAndFlush(callInfo);
    }
}
