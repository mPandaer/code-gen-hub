package com.pandaer.acm.cli.command;


import cn.hutool.core.bean.BeanUtil;
import com.pandaer.acm.generator.CodeFileGenerator;
import com.pandaer.acm.model.DataModel;
import lombok.Data;
import picocli.CommandLine;

import java.io.File;

/**
 * 代码生成命令
 */
@Data
@CommandLine.Command(name = "generate", description = "生成模板代码", mixinStandardHelpOptions = true)
public class GenerateCommand implements Runnable {

    @CommandLine.Option(names = {"-a", "--author"}, description = "作者信息",
            arity = "0..1", interactive = true, echo = true,required = true)
    private String author;

    @CommandLine.Option(names = {"-l", "--loop"}, description = "是否循环",
            arity = "0..1", interactive = true, echo = true,required = true)
    private boolean loop;

    @CommandLine.Option(names = {"-o", "--outputText"}, description = "输出文本提示信息",
            arity = "0..1", interactive = true, echo = true,required = true)
    private String outputText;


    @Override
    public void run() {
        DataModel dataModel = BeanUtil.toBean(this, DataModel.class);
        CodeFileGenerator codeFileGenerator = new CodeFileGenerator(dataModel);
        // 原始项目
        String currentDir = System.getProperty("user.dir");
        String originProjectDirPath = currentDir + File.separator + "origin-project-demo" + File.separator + "acm-template";
        File originProject = new File(originProjectDirPath);
        // 生成的项目
        String generateProjectDirPath = currentDir + File.separator + "generated";
        File generateProject = new File(generateProjectDirPath);
        // 生成代码
        codeFileGenerator.generator(originProject,generateProject);
    }
}
