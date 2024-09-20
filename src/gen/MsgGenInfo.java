package gen;

public class MsgGenInfo {
    public String name;
    public Integer msgId;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Integer getMsgId() {
        return msgId;
    }

    public void setMsgId(Integer msgId) {
        this.msgId = msgId;
    }

    @Override
    public String toString() {
        return "MsgGenInfo{" +
                "name='" + name + '\'' +
                ", msgId=" + msgId +
                '}';
    }
}
