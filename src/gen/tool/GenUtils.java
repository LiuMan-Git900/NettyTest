package gen.tool;


import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import com.google.protobuf.UnknownFieldSet;
import com.lzp.netty.protobuf.Options;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import gen.MsgGenInfo;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

// 我们使用的是模板生成
public class GenUtils {
    public static void main(String[] args) throws IOException, IllegalAccessException {
        genMesId();
    }

    public static void genMesId() throws IOException {
        // 读取所有的desc文件
        File file = new File("E:\\protobuf-26.1-win64\\bin\\desc2");
        List<MsgGenInfo> MsgInfo = new ArrayList<>();
        getMsgGenInfoList(MsgInfo, file);
        try {
            //配置-模板所在目录
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
            cfg.setDirectoryForTemplateLoading(new File("src/gen//templates"));
            cfg.setDefaultEncoding("UTF-8");
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            // 获取模板
            Template template = cfg.getTemplate("MsgId.ftl");
            // 数据模型搭建
            Map root = new HashMap();
            root.put("MsgInfo", MsgInfo);
            // 生成
            File outFile = new File("src/gen/MsgIds.java");
            if(outFile.exists() == false) {
                outFile.createNewFile();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(outFile);
            Writer out = new OutputStreamWriter(fileOutputStream);
            template.process(root, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void getMsgGenInfoList(List<MsgGenInfo> list, File file) throws IOException {

        FileInputStream fileInputStream = new FileInputStream(file);
        try{
            DescriptorProtos.FileDescriptorSet fileDescriptorSet = DescriptorProtos.FileDescriptorSet.parseFrom(fileInputStream);
            Iterator<DescriptorProtos.FileDescriptorProto> fdp = fileDescriptorSet.getFileList().iterator();
            while (fdp.hasNext()) {
                DescriptorProtos.FileDescriptorProto fileDescriptorProto = fdp.next();
                System.out.println("file:" + fileDescriptorProto.getName());
                java.util.List<DescriptorProtos.DescriptorProto> messagefpd = fileDescriptorProto.getMessageTypeList();
                for (DescriptorProtos.DescriptorProto descriptorProto: messagefpd) {
                    String name = descriptorProto.getName();
                    System.out.println("message: " + name);
                    DescriptorProtos.MessageOptions options = descriptorProto.getOptions();
                    UnknownFieldSet unknownFields = options.getUnknownFields();
                    if(unknownFields.hasField(10001)) {
                        UnknownFieldSet.Field field = unknownFields.getField(10001);
                        MsgGenInfo msg = new MsgGenInfo();
                        msg.setName(name);
                        Integer msgId = Math.toIntExact(field.getVarintList().get(0));
                        msg.setMsgId(msgId);
                        list.add(msg);
                    }
                }
            }
        }catch(Exception e) {
            fileInputStream.close();
            e.printStackTrace();
        }
    }

    private static void TempleGenTest() {
        try {
            //配置-模板所在目录
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
            cfg.setDirectoryForTemplateLoading(new File("src/gen//templates"));
            cfg.setDefaultEncoding("UTF-8");
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            // 获取模板
            Template template = cfg.getTemplate("GenTest.ftl");
            // 数据模型搭建
            Map root = new HashMap();
            root.put("Name", "liuzhipeng");
            root.put("Age", 20);
            List<String> food = new ArrayList<>();
            food.add("lajiao1");
            food.add("lajiao2");
            root.put("LikeFood",food);
            // 生成
            Writer out = new OutputStreamWriter(System.out);
            template.process(root, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void PathTest() {
        // 当前工作目录
        String currentDir = System.getProperty("user.dir");
        System.out.println(currentDir);
        // 相对路径
        // 文件系统找到相对路径
        File file = new File("src/gen//templates");
        if (file.exists()) {
            System.out.println("Exit");
        }
        // 绝对路径
        System.out.println(file.getAbsolutePath());
        // 相对路径
        System.out.println(file.getPath());
//        File file = new File("D:\\untitled\\src\\gen\\GenTest.ftl");
        // 类加载机制找到相对路径
        URL url = GenUtils.class.getClassLoader().getResource("/gen/templates");
        File file2 = new File("src/gen//templates");
        if (file2.exists()) {
            System.out.println("Exit2");
        }
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

    public static int bytesToBigEndian32(byte[] bytes , int offset) {
        return  (((int)bytes[offset] & 0xFF) << 24) |
                (((int)bytes[offset + 1] & 0xFF) << 16) |
                (((int)bytes[offset + 2] & 0xFF) << 8) |
                (((int)bytes[offset] & 0xFF)        );
    }

}
