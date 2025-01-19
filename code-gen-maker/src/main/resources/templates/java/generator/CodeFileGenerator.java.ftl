package ${basePackage}.acm.generator;

import cn.hutool.core.io.FileUtil;
import ${basePackage}.acm.model.DataModel;
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
        String inputPath;
        String outputPath;

    <#list fileConfig.files as fileInfo>
        // 生成动态文件
        inputPath = originProject.getAbsolutePath() + File.separator + "${fileInfo.inputPath}";
        outputPath = generateProject.getAbsolutePath() + File.separator + "${fileInfo.outputPath}";
        // 保证outputPath的父目录存在
        FileUtil.mkParentDirs(outputPath);
    <#if fileInfo.generateType == "dynamic">
        DynamicFileGenerator.generate(FileUtil.file(inputPath),FileUtil.file(outputPath),dataModel);
    <#else>
        StaticFileGenerator.generate(FileUtil.file(inputPath),FileUtil.file(outputPath));
    </#if>

    </#list>

    }
    
}
