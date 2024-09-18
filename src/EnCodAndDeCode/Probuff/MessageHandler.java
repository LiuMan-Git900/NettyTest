package EnCodAndDeCode.Probuff;

import com.google.protobuf.GeneratedMessage;


public class MessageHandler {

    public static void Handler(int msgId, GeneratedMessage message) {
        switch (msgId) {
            case 1:
                MessageHandler.OnMyMessage(message);
                break;
            case 2:
                MessageHandler.OnMySecondMessage(message);
                break;
            default:
                System.out.println("未知的数据");
                break;
        }
    }

    private static void OnMyMessage(GeneratedMessage message) {

    }

    private static void OnMySecondMessage(GeneratedMessage message) {

    }
}
