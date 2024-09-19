package gen;


import freemarker.core.Configurable;
import freemarker.template.Configuration;

// 我们使用的是模板生成
public class MsgGen {
    // 普通的文件生成
    // freemarkker模板生成
    Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
    cfg.setCacheStorage();
}
