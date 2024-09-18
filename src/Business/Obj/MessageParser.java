package Business.Obj;

import com.google.protobuf.*;
import com.ksg.core.support.Sys;
import com.lzp.netty.protobuf.Options;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class MessageParser {
    public static Map<Integer, Parser> idMapParser = new HashMap<>();
    public static Map<Class<?>, Integer> clazzMapID = new HashMap<>();


    public static void init() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // 这一块能不能进一步优化：
        idMapParser.clear();
        clazzMapID.clear();
        // 自动化id和消息显映射关系：使用类加载器进行初始化
        Set<Class<?>> set = getALLClass("com.lzp.netty.protobuf");
        List<Class<?>> list = set.stream().filter(clazz -> GeneratedMessage.class.isAssignableFrom(clazz)).sorted((a,b) -> a.getSimpleName().compareTo(b.getSimpleName())).collect(Collectors.toList());

    }
    public static Message parser(int msgId, byte[] bytes) {
        Parser parser = idMapParser.get(msgId);
        if(parser == null)  {
            System.out.println("未发现协议解析器");
            return null;
        }
        try {
            GeneratedMessage message = (GeneratedMessage) parser.parseFrom(bytes, 4, bytes.length);
            return message;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    // 优化：索引判断太慢了，能不能通过Map来进行索引
    // TODO 优化：必须实现自动化工具，不然无法保证每一次添加数据我们都得自己进行修改，增加劳动
    private static Parser getParser(int messageId) {
        switch (messageId) {
//            case 1 : return MsgOptions3.MyMessage.parser();
//            case 2 : return MsgOptions3.MySecondMessage.parser();
            default: return null;
        }
    }
    // 优化：索引判断太慢了，能不能通过Map来进行索引
    // TODO 优化：必须实现自动化工具，不然无法保证每一次添加数据我们都得自己进行修改，增加劳动
    public static int getMsgId(Class<?>clazz) {
        Integer integer = clazzMapID.get(clazz);
        if(integer == null) {
            return 0;
        }
        return integer;
    }

    public static int bytesToBigEndian32(byte[] bytes , int offset) {
        return  (((int)bytes[offset] & 0xFF) << 24) |
                (((int)bytes[offset + 1] & 0xFF) << 16) |
                (((int)bytes[offset + 2] & 0xFF) << 8) |
                (((int)bytes[offset] & 0xFF)        );
    }



    public static Set<Class<?>> getALLClass(String packName) {
        ClassLoader classLoader = Message.class.getClassLoader();
        Set<Class<?>> set = new HashSet<>();
        URL url = classLoader.getResource(packNameToFilePath(packName));
        if("file".equals(url.getProtocol())) {
            findByFile(classLoader, packName, set);
        }
        return set;
    }

    public static void findByFile(ClassLoader classLoader, String packName, Set<Class<?>> classSet) {
        String packFilePath = packNameToFilePath(packName);
        URL url = classLoader.getResource(packFilePath);
        File file = new File(url.getPath());
        for (File subFile : file.listFiles()) {
            if(subFile.isDirectory()) {
                findByFile(classLoader, packName+ "." + subFile.getName(), classSet);
            } else {
                boolean classFile = subFile.getName().endsWith(".class");
                if(classFile == false) {
                    continue;
                }
                String className = subFile.getName().substring(0, subFile.getName().length() - ".class".length());
                try {
                    Class<?> clazz = classLoader.loadClass(packName+"."+ className);
                    classSet.add(clazz);
                } catch (Exception e) {
                    System.out.println("未找到类 name = " + className);
                }
            }
        }
    }

    public static String packNameToFilePath(String packName) {
        return packName.replace(".", "/");
    }

    // protobuff文件的序列化和返序列化
    // 生成的protobuff文件中会有descript数据和类来帮助我们返序列化和序列化
    // FileDescriptor  Descriptor（Message的反射类） FieldDescriptor EnumDescriptor
    // 在静态中，会用DescriptData去初始化FileDescriptor，然后文件描述对象初始化后，会初始化Descriptor和FieldDescriptor
    // 然后再用ExtensionRegistry去初始化我们的扩展数据域
    // 所有扩展数据域descriptor.getOptions().getExtension(Options.msgid)这个获取
    // ProtoBuff的反射体系--通过class文件来
    public static void ProtoBuffReflex(List<Class> list) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        for (Class clazz : list) {
            // protobuff 消息的描述对象
            System.out.println("ClazzName = " + clazz.getSimpleName());
            Method method = clazz.getMethod("getDescriptor");
            Descriptors.Descriptor descriptor = (com.google.protobuf.Descriptors.Descriptor) method.invoke(null, null);
            // 域的描述对象
            List<Descriptors.FieldDescriptor> list1 = descriptor.getFields();
            for ( Descriptors.FieldDescriptor fieldDescriptor : list1) {
                System.out.println("name = " + fieldDescriptor.getName() + " number = " + fieldDescriptor.getNumber());
            }
            // 扩展域的描述对象
            List<Descriptors.FieldDescriptor> list2 = descriptor.getExtensions();
            for ( Descriptors.FieldDescriptor fieldDescriptor : list2) {
                System.out.println("name = " + fieldDescriptor.getName() + " number = " + fieldDescriptor.getNumber());
            }
            UnknownFieldSet fieldSet = descriptor.getOptions().getUnknownFields();
            System.out.println(fieldSet.hasField(10001));
            for ( Map.Entry<Integer, UnknownFieldSet.Field> fieldEntry : fieldSet.asMap().entrySet()) {
                System.out.println("key: " + fieldEntry.getKey() + " value" + fieldEntry.getValue());
            }
            System.out.println("Options.msgid = " + descriptor.getOptions().getExtension(Options.msgid));
        }
    }
    // 编写工具时的前置思想：1.运用反射思想获取信息 2.获取生成工具，运用生成工具来生成我想要的文件
    // ProtoBuff描述文件--反射思想，描述文件文件对应的java类对象
    // class文件的反射机制 文件和文件对应的对象类（class对象，method对象，field对象）
    // protobuff文件的反射机制：描述文件，File
    public static void ProtoBuffReflex2() throws IOException {
        // TODO descript描述文件反射desc2
        File file = new File("E:\\protobuf-26.1-win64\\bin\\desc2");
        FileInputStream fileInputStream = new FileInputStream(file);
        try{
            DescriptorProtos.FileDescriptorSet fileDescriptorSet = DescriptorProtos.FileDescriptorSet.parseFrom(fileInputStream);
            Iterator<DescriptorProtos.FileDescriptorProto> fdp = fileDescriptorSet.getFileList().iterator();
            while (fdp.hasNext()) {
                DescriptorProtos.FileDescriptorProto fileDescriptorProto = fdp.next();
                System.out.println("------------------------------------------");
                System.out.println("file:" + fileDescriptorProto.getName());
                java.util.List<DescriptorProtos.DescriptorProto> messagefpd = fileDescriptorProto.getMessageTypeList();
                for (DescriptorProtos.DescriptorProto descriptorProto: messagefpd) {
                    String name = descriptorProto.getName();
                    System.out.println("message: " + name);
                    List<DescriptorProtos.FieldDescriptorProto> fieldDescriptorList = descriptorProto.getFieldList();
                    for ( DescriptorProtos.FieldDescriptorProto fieldDescriptorProto : fieldDescriptorList) {
                        System.out.println("field: " + fieldDescriptorProto.getName());
                    }
                    List<DescriptorProtos.FieldDescriptorProto> fieldDescriptorList2 = descriptorProto.getExtensionList();
                    for ( DescriptorProtos.FieldDescriptorProto fieldDescriptorProto2 : fieldDescriptorList2) {
                        System.out.println("extend: " + fieldDescriptorProto2.getName());
                    }
                    DescriptorProtos.MessageOptions options = descriptorProto.getOptions();
                    System.out.println("hasFeatures:" + options.hasFeatures());
                    System.out.println("hasExtension" + options.hasExtension(Options.msgid));
                    System.out.println("extension" + options.getExtension(Options.msgid));

                    UnknownFieldSet unknownFields = options.getUnknownFields();
                    System.out.println("hasUnknownField 10001: " + unknownFields.hasField(10001));
                    if(unknownFields.hasField(10001)) {
                        UnknownFieldSet.Field field = unknownFields.getField(10001);
                        System.out.println(field.getVarintList().get(0));
                    }
                    System.out.println();

                    System.out.println();
                }

                System.out.println("------------------------------------------");
            }
        }catch(Exception e) {
            fileInputStream.close();
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws IOException {
        ProtoBuffReflex2();
    }

}
