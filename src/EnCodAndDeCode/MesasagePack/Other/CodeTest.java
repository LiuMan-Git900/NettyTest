package EnCodAndDeCode.MesasagePack.Other;

import Business.Obj.CallInfo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/***
 * 测试一下java自带的编解码技术和nio的bytebuffer编解码技术的对比
 * 结果：java自带的编解码技术太差了
 *  java自带
 *  // bytes length = 93
 * // Timer = 2248
 *  nio-ByteBuff
 * // bytes length = 18
 * // Timer = 16
 */

public class CodeTest {
    public static void main(String[] args) {
        try {
            javaSelfTestSizeAndTime();
            byteBufferTestSizeAndTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void javaSelfTestSizeAndTime() throws IOException {
        CallInfo testInfo = new CallInfo();
        testInfo.moduleName = "liuzhupeng";
        testInfo.methodID = 27;
        // 一个帮助我们自动扩展byte的输出流类-byteArray类-byte自动管理类
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // 一个帮我们将object对象转化成byte[]的输出流类-将object转化成byte类
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(testInfo);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        System.out.println("bytes length = "  + bytes.length);

        // 打时间戳10000次运行结果
        long StartTime = System.currentTimeMillis();
        for (int i = 1; i < 100000; i ++) {
            CallInfo testInfo2 = new CallInfo();
            testInfo.moduleName = "liuzhupeng";
            testInfo.methodID = 27;
            objectOutputStream.writeObject(testInfo);
            byte[] bytes2 = byteArrayOutputStream.toByteArray();
        }
        long EndTime = System.currentTimeMillis();
        System.out.println("Timer = " + (EndTime - StartTime));
        // 单次运行结果：bytes length = 93

    }

    public static void byteBufferTestSizeAndTime() {
        // 我们用nio的ByteBuffer来代替对象字节化和字节数组的自动管理
        CallInfo testInfo = new CallInfo();
        testInfo.moduleName = "liuzhupeng";
        testInfo.methodID = 27;
        byte[] bytes = testInfo.EncodeByBuff();
        System.out.println("bytes length = "  + bytes.length);

        long StartTime = System.currentTimeMillis();
        for (int i = 1; i < 100000; i ++) {
            CallInfo testInfo2 = new CallInfo();
            testInfo.moduleName = "liuzhupeng";
            testInfo.methodID = 27;
            byte[] bytes2 = testInfo.EncodeByBuff();
        }
        long EndTime = System.currentTimeMillis();
        System.out.println("Timer = " + (EndTime - StartTime));
        // 单次运行结果：bytes length = 18
        // bytes length = 93
        // Timer = 2248
        // bytes length = 18
        // Timer = 16
        // 相差太远了
    }
}
