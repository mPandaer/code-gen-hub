package com.pandaer.maker.generator.file;

import cn.hutool.core.io.FileUtil;
import com.pandaer.maker.meta.Meta;
import lombok.AllArgsConstructor;

import java.io.File;

/**
 * 代码生成器
 */

@AllArgsConstructor
public class FileGenerator {

    private Meta meta;

    /**
     * 遍历原始项目，获取文件信息，根据文件信息路由到具体的代码生成器
     * @param templatesDir 原始项目
     * @param madeGeneratorDir 生成的项目
     */
    public void generator(File templatesDir,File madeGeneratorDir) {

        // 生成DataModel.java
        String inputPath;
        String outputPath;


        // com.pandaer
        String basePackage = meta.getBasePackage();
        // com/pandaer
        String basePackagePath = basePackage.replace(".","/");

        // 生成 DataModel.java
        inputPath = templatesDir.getAbsolutePath() + File.separator + "java/model/DataModel.java.ftl";
        outputPath = madeGeneratorDir.getAbsolutePath() + File.separator + "src/main/java/" + basePackagePath + "/acm/model/DataModel.java";
        // 确保父目录存在
        FileUtil.mkParentDirs(outputPath);
        DynamicFileGenerator.generate(new File(inputPath),new File(outputPath),meta);


        // 生成 CodeFileGenerator.java
        inputPath = templatesDir.getAbsolutePath() + File.separator + "java/generator/CodeFileGenerator.java.ftl";
        outputPath = madeGeneratorDir.getAbsolutePath() + File.separator + "src/main/java/" + basePackagePath + "/acm/generator/CodeFileGenerator.java";
        // 确保父目录存在
        FileUtil.mkParentDirs(outputPath);
        DynamicFileGenerator.generate(new File(inputPath),new File(outputPath),meta);

        // 生成 DynamicFileGenerator.java
        inputPath = templatesDir.getAbsolutePath() + File.separator + "java/generator/DynamicFileGenerator.java.ftl";
        outputPath = madeGeneratorDir.getAbsolutePath() + File.separator + "src/main/java/" + basePackagePath + "/acm/generator/DynamicFileGenerator.java";
        // 确保父目录存在
        FileUtil.mkParentDirs(outputPath);
        DynamicFileGenerator.generate(new File(inputPath),new File(outputPath),meta);


        // 生成 StaticFileGenerator.java
        inputPath = templatesDir.getAbsolutePath() + File.separator + "java/generator/StaticFileGenerator.java.ftl";
        outputPath = madeGeneratorDir.getAbsolutePath() + File.separator + "src/main/java/" + basePackagePath + "/acm/generator/StaticFileGenerator.java";
        // 确保父目录存在
        FileUtil.mkParentDirs(outputPath);
        DynamicFileGenerator.generate(new File(inputPath),new File(outputPath),meta);


        // 生成 ListCommand.java
        inputPath = templatesDir.getAbsolutePath() + File.separator + "java/cli/command/ListCommand.java.ftl";
        outputPath = madeGeneratorDir.getAbsolutePath() + File.separator + "src/main/java/" + basePackagePath + "/acm/cli/command/ListCommand.java";
        // 确保父目录存在
        FileUtil.mkParentDirs(outputPath);
        DynamicFileGenerator.generate(new File(inputPath),new File(outputPath),meta);


        // 生成 ConfigCommand.java
        inputPath = templatesDir.getAbsolutePath() + File.separator + "java/cli/command/ConfigCommand.java.ftl";
        outputPath = madeGeneratorDir.getAbsolutePath() + File.separator + "src/main/java/" + basePackagePath + "/acm/cli/command/ConfigCommand.java";
        // 确保父目录存在
        FileUtil.mkParentDirs(outputPath);
        DynamicFileGenerator.generate(new File(inputPath),new File(outputPath),meta);


        // 生成 GenerateCommand.java
        inputPath = templatesDir.getAbsolutePath() + File.separator + "java/cli/command/GenerateCommand.java.ftl";
        outputPath = madeGeneratorDir.getAbsolutePath() + File.separator + "src/main/java/" + basePackagePath + "/acm/cli/command/GenerateCommand.java";
        // 确保父目录存在
        FileUtil.mkParentDirs(outputPath);
        DynamicFileGenerator.generate(new File(inputPath),new File(outputPath),meta);


        // 生成 CommandExecutor.java
        inputPath = templatesDir.getAbsolutePath() + File.separator + "java/cli/CommandExecutor.java.ftl";
        outputPath = madeGeneratorDir.getAbsolutePath() + File.separator + "src/main/java/" + basePackagePath + "/acm/cli/CommandExecutor.java";
        // 确保父目录存在
        FileUtil.mkParentDirs(outputPath);
        DynamicFileGenerator.generate(new File(inputPath),new File(outputPath),meta);

        // 生成 Main.java
        inputPath = templatesDir.getAbsolutePath() + File.separator + "java/Main.java.ftl";
        outputPath = madeGeneratorDir.getAbsolutePath() + File.separator + "src/main/java/" + basePackagePath + "/acm/Main.java";
        // 确保父目录存在
        FileUtil.mkParentDirs(outputPath);
        DynamicFileGenerator.generate(new File(inputPath),new File(outputPath),meta);

        // 生成 pom.xml
        inputPath = templatesDir.getAbsolutePath() + File.separator + "pom.xml.ftl";
        outputPath = madeGeneratorDir.getAbsolutePath() + File.separator + "pom.xml";
        // 确保父目录存在
        FileUtil.mkParentDirs(outputPath);
        DynamicFileGenerator.generate(new File(inputPath),new File(outputPath),meta);

        // 生成 .gitignore
        inputPath = templatesDir.getAbsolutePath() + File.separator + "static/.gitignore";
        outputPath = madeGeneratorDir.getAbsolutePath() + File.separator + ".gitignore";
        // 确保父目录存在
        FileUtil.mkParentDirs(outputPath);
        StaticFileGenerator.generate(new File(inputPath),new File(outputPath));

    }


}
