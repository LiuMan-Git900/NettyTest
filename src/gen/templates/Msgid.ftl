package gen;

import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.Parser;
import com.ksg.core.support.SysException;
import gen.tool.GenUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MsgIds {
    <#list MsgInfo as info>
    <#if info.msgId??>
    public static int ${info.name} = ${info.msgId};
    </#if>
    </#list>

    /** clazz映射Id */
    public static Map<Class, Integer> clazzMapId = new HashMap<>();
    /**ID因素class对象*/
    public static Map<Integer, Class> idMapClazz = new HashMap<>();
    /** ID映射解析器 */
    public static Map<Integer, Parser> idMapParser = new HashMap<>();

    static {
        try {
            init();
        } catch (Exception e) {
            throw new SysException(e);
        }
    }
    public static void init() throws IllegalAccessException, NoSuchFieldException {
        Map<String, Integer> nameMapName = new HashMap<>();
        Field[] fields = MsgIds.class.getFields();
        for (Field field: fields) {
            if(field.getType().equals(int.class)) {
                String name = field.getName();
                nameMapName.put(name, (Integer) field.get(null));
            }

        }
        // 自动化id和消息显映射关系：使用类加载器进行初始化
        Set<Class<?>> set = GenUtils.getALLClass("com.lzp.netty.protobuf");
        for (Class clazz : set) {
            if(!GeneratedMessage.class.isAssignableFrom(clazz)) {
                continue;
            }
            Integer msgId = nameMapName.get(clazz.getSimpleName());
            if (msgId == null) {
                continue;
            }
            idMapClazz.put(msgId, clazz);
            idMapParser.put(msgId, (Parser) clazz.getField("PARSER").get(null));
            clazzMapId.put(clazz, msgId);
        }
    }

    public static Parser getParser(int msgId) {
        return idMapParser.get(msgId);
    }

    public static int getMsgId(Class clazz) {
        return clazzMapId.get(clazz);
    }
}
