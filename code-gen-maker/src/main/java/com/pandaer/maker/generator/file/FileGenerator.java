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
     * @param generatorTemplatesDir 生成器模板文件所在
     * @param madeGeneratorDir 生成的项目
     */
    public void generator(File generatorTemplatesDir,File madeGeneratorDir) {

        // 制作原始项目的模板文件
        TemplateFileGenerator templateFileGenerator = new TemplateFileGenerator();
        // TODO 目前原始项目路径就是模板文件所在的目录 （因为我们手动制作好了模板文件）
        templateFileGenerator.generator(new File(meta.getFileConfig().getOriginProjectPath()),madeGeneratorDir);


        // 生成DataModel.java
        String inputPath;
        String outputPath;

        // com.pandaer
        String basePackage = meta.getBasePackage();
        // com/pandaer
        String basePackagePath = basePackage.replace(".","/");

        // 生成 DataModel.java
        inputPath = "/templates/java/model/DataModel.java.ftl";
        outputPath = madeGeneratorDir.getAbsolutePath() + File.separator + "src/main/java/" + basePackagePath + "/acm/model/DataModel.java";
        // 确保父目录存在
        FileUtil.mkParentDirs(outputPath);
        DynamicFileGenerator.generateByClassPath(inputPath,new File(outputPath),meta);


        // 生成 CodeFileGenerator.java
        inputPath = "/templates/java/generator/CodeFileGenerator.java.ftl";
        outputPath = madeGeneratorDir.getAbsolutePath() + File.separator + "src/main/java/" + basePackagePath + "/acm/generator/CodeFileGenerator.java";
        // 确保父目录存在
        FileUtil.mkParentDirs(outputPath);
        DynamicFileGenerator.generateByClassPath(inputPath,new File(outputPath),meta);

        // 生成 DynamicFileGenerator.java
        inputPath = "/templates/java/generator/DynamicFileGenerator.java.ftl";
        outputPath = madeGeneratorDir.getAbsolutePath() + File.separator + "src/main/java/" + basePackagePath + "/acm/generator/DynamicFileGenerator.java";
        // 确保父目录存在
        FileUtil.mkParentDirs(outputPath);
        DynamicFileGenerator.generateByClassPath(inputPath,new File(outputPath),meta);


        // 生成 StaticFileGenerator.java
        inputPath = "/templates/java/generator/StaticFileGenerator.java.ftl";
        outputPath = madeGeneratorDir.getAbsolutePath() + File.separator + "src/main/java/" + basePackagePath + "/acm/generator/StaticFileGenerator.java";
        // 确保父目录存在
        FileUtil.mkParentDirs(outputPath);
        DynamicFileGenerator.generateByClassPath(inputPath,new File(outputPath),meta);


        // 生成 ListCommand.java
        inputPath = "/templates/java/cli/command/ListCommand.java.ftl";
        outputPath = madeGeneratorDir.getAbsolutePath() + File.separator + "src/main/java/" + basePackagePath + "/acm/cli/command/ListCommand.java";
        // 确保父目录存在
        FileUtil.mkParentDirs(outputPath);
        DynamicFileGenerator.generateByClassPath(inputPath,new File(outputPath),meta);


        // 生成 ConfigCommand.java
        inputPath = "/templates/java/cli/command/ConfigCommand.java.ftl";
        outputPath = madeGeneratorDir.getAbsolutePath() + File.separator + "src/main/java/" + basePackagePath + "/acm/cli/command/ConfigCommand.java";
        // 确保父目录存在
        FileUtil.mkParentDirs(outputPath);
        DynamicFileGenerator.generateByClassPath(inputPath,new File(outputPath),meta);


        // 生成 GenerateCommand.java
        inputPath = "/templates/java/cli/command/GenerateCommand.java.ftl";
        outputPath = madeGeneratorDir.getAbsolutePath() + File.separator + "src/main/java/" + basePackagePath + "/acm/cli/command/GenerateCommand.java";
        // 确保父目录存在
        FileUtil.mkParentDirs(outputPath);
        DynamicFileGenerator.generateByClassPath(inputPath,new File(outputPath),meta);

        // 生成 JsonGenerateCommand.java
        inputPath = "/templates/java/cli/command/JsonGenerateCommand.java.ftl";
        outputPath = madeGeneratorDir.getAbsolutePath() + File.separator + "src/main/java/" + basePackagePath + "/acm/cli/command/JsonGenerateCommand.java";
        // 确保父目录存在
        FileUtil.mkParentDirs(outputPath);
        DynamicFileGenerator.generateByClassPath(inputPath,new File(outputPath),meta);


        // 生成 CommandExecutor.java
        inputPath = "/templates/java/cli/CommandExecutor.java.ftl";
        outputPath = madeGeneratorDir.getAbsolutePath() + File.separator + "src/main/java/" + basePackagePath + "/acm/cli/CommandExecutor.java";
        // 确保父目录存在
        FileUtil.mkParentDirs(outputPath);
        DynamicFileGenerator.generateByClassPath(inputPath,new File(outputPath),meta);

        // 生成 Main.java
        inputPath = "/templates/java/Main.java.ftl";
        outputPath = madeGeneratorDir.getAbsolutePath() + File.separator + "src/main/java/" + basePackagePath + "/acm/Main.java";
        // 确保父目录存在
        FileUtil.mkParentDirs(outputPath);
        DynamicFileGenerator.generateByClassPath(inputPath,new File(outputPath),meta);

        // 生成 pom.xml
        inputPath = "/templates/pom.xml.ftl";
        outputPath = madeGeneratorDir.getAbsolutePath() + File.separator + "pom.xml";
        // 确保父目录存在
        FileUtil.mkParentDirs(outputPath);
        DynamicFileGenerator.generateByClassPath(inputPath,new File(outputPath),meta);

        // 生成 .gitignore
//        inputPath = "/templates/static/.gitignore";
//        outputPath = madeGeneratorDir.getAbsolutePath() + File.separator + ".gitignore";
//        // 确保父目录存在
//        FileUtil.mkParentDirs(outputPath);
//        DynamicFileGenerator.generateByClassPath(inputPath,new File(outputPath),meta);
//        StaticFileGenerator.generateByClassPath(inputPath,new File(outputPath));


        // 生成 README.md
        inputPath = "/templates/README.md.ftl";
        outputPath = madeGeneratorDir.getAbsolutePath() + File.separator + "README.md";
        // 确保父目录存在
        FileUtil.mkParentDirs(outputPath);
        DynamicFileGenerator.generateByClassPath(inputPath,new File(outputPath),meta);


    }


}
