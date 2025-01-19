package com.pandaer.acm.generator;

import cn.hutool.core.io.FileUtil;
import com.pandaer.acm.model.DataModel;
import lombok.AllArgsConstructor;

import java.io.File;

/**
 * 代码生成器
 */

@AllArgsConstructor
public class CodeFileGenerator {

    private DataModel dataModel;

    /**
     * 遍历原始项目，获取文件信息，根据文件信息路由到具体的代码生成器
     * @param originProject 原始项目
     * @param generateProject 生成的项目
     */
    public void generator(File originProject,File generateProject) {
        // 改造代码 适配动态模板文件

        String inputPath;
        String outputPath;

        // 生成动态文件
        inputPath = originProject.getAbsolutePath() + File.separator + "src/main/java/com/pandaer/acm/template/MainTemplate.java.ftl";
        outputPath = generateProject.getAbsolutePath() + File.separator + "src/main/java/com/pandaer/acm/template/MainTemplate.java";
        // 保证outputPath的父目录存在
        File generatedFile = FileUtil.file(outputPath);
        FileUtil.mkParentDirs(generatedFile);
        DynamicFileGenerator.generate(FileUtil.file(inputPath),generatedFile,dataModel);

        // 生成静态文件
        inputPath = originProject.getAbsolutePath() + File.separator + ".gitignore";
        outputPath = generateProject.getAbsolutePath() + File.separator + ".gitignore";
        StaticFileGenerator.generate(FileUtil.file(inputPath),FileUtil.file(outputPath));

        inputPath = originProject.getAbsolutePath() + File.separator + "pom.xml";
        outputPath = generateProject.getAbsolutePath() + File.separator + "pom.xml";
        StaticFileGenerator.generate(FileUtil.file(inputPath),FileUtil.file(outputPath));

        inputPath = originProject.getAbsolutePath() + File.separator + "src/main/java/com/pandaer/acm/template/HelloWorld.java";
        outputPath = generateProject.getAbsolutePath() + File.separator + "src/main/java/com/pandaer/acm/template/HelloWorld.java";
        StaticFileGenerator.generate(FileUtil.file(inputPath),FileUtil.file(outputPath));
        System.out.println("代码生成完毕！");
    }

}
