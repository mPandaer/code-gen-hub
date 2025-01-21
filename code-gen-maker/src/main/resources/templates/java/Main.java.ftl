package ${basePackage}.acm;


import cn.hutool.core.util.ReflectUtil;
import ${basePackage}.acm.cli.CommandExecutor;
import ${basePackage}.acm.cli.command.GenerateCommand;
import picocli.CommandLine;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 主程序
 */
public class Main {
    public static void main(String[] args) {
        // 格式化用户输入的参数
        args = commandNormalize(args);
        int exitCode = new CommandLine(new CommandExecutor()).execute(args);
        System.exit(exitCode);
    }

    private static String[] commandNormalize(String[] args) {
        if (args.length == 0) return args;
        if (!args[0].equals("generate")) return args;
        List<String> argList = Arrays.stream(args).collect(Collectors.toList());
        Field[] fields = ReflectUtil.getFields(GenerateCommand.class);
        List<Field> optionFields = Arrays.stream(fields).filter(field -> field.isAnnotationPresent(CommandLine.Option.class)).collect(Collectors.toList());
        for (Field optionField : optionFields) {
            CommandLine.Option optionAnnotation = optionField.getAnnotation(CommandLine.Option.class);
            String[] names = optionAnnotation.names();
            boolean required = optionAnnotation.required();
            if (!required) {
                continue;
            }
            boolean isExist = argList.stream().anyMatch(arg -> {
                for (String name : names) {
                    if (arg.startsWith(name)) {
                        return true;
                    }
                }
                return false;
            });
            if (!isExist && names.length > 0) {
                argList.add(names[0]);
            }
        }

        return argList.toArray(new String[0]);

    }
}
