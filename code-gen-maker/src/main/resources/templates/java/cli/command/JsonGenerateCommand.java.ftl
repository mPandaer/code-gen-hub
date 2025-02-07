package ${basePackage}.acm.cli.command;


import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import ${basePackage}.acm.generator.CodeFileGenerator;
import ${basePackage}.acm.model.DataModel;
import lombok.Data;
import picocli.CommandLine;

import java.io.File;

/**
 * 代码生成命令 基于JSON文件生成
 */
@Data
@CommandLine.Command(name = "jsonGenerate", description = "利用JSON文件生成代码", mixinStandardHelpOptions = true)
public class JsonGenerateCommand implements Runnable {

    @CommandLine.Option(names = {"-f", "--file"}, description = "Json文件路径",
            arity = "0..1", interactive = true, echo = true,required = true)
    private String jsonFilePath;



    @Override
    public void run() {
        // 读取json文件
        String dataModelJsonStr = FileUtil.readUtf8String(jsonFilePath);
        DataModel dataModel = JSONUtil.toBean(dataModelJsonStr, DataModel.class);

        CodeFileGenerator codeFileGenerator = new CodeFileGenerator(dataModel);
        // 原始项目
        String originProjectDirPath = "${fileConfig.originProjectPath}";
        File originProject = new File(originProjectDirPath);
        // 生成的项目
        String generateProjectDirPath = "generated";
        File generateProject = new File(generateProjectDirPath);
        // 生成代码
        codeFileGenerator.generator(originProject,generateProject);
    }
}
