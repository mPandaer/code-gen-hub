package ${basePackage}.acm.cli.command;

import cn.hutool.core.util.ReflectUtil;
import ${basePackage}.acm.model.DataModel;
import picocli.CommandLine;

import java.lang.reflect.Field;

/**
 * 获取数据模型配置信息
 */
@CommandLine.Command(name = "config", description = "数据模型配置信息", mixinStandardHelpOptions = true)
public class ConfigCommand implements Runnable {
    @Override
    public void run() {
        Field[] fields = ReflectUtil.getFields(DataModel.class);
        for (Field field : fields) {
            String desc = String.format("类型：%s 属性名：%s",field.getType().getSimpleName(),field.getName());
            System.out.println(desc);
        }
    }
}
