package ${basePackage}.acm.generator;

import cn.hutool.core.io.FileUtil;
import ${basePackage}.acm.model.DataModel;
import lombok.AllArgsConstructor;

import java.io.File;


<#macro generateFile indent fileInfo>
${indent}// 生成文件
${indent}inputPath = originProject.getAbsolutePath() + File.separator + "${fileInfo.inputPath}";
${indent}outputPath = generateProject.getAbsolutePath() + File.separator + "${fileInfo.outputPath}";
${indent}// 保证outputPath的父目录存在
${indent}FileUtil.mkParentDirs(outputPath);
<#if fileInfo.generateType == "dynamic">
${indent}DynamicFileGenerator.generate(FileUtil.file(inputPath),FileUtil.file(outputPath),dataModel);
<#else>
${indent}StaticFileGenerator.generate(FileUtil.file(inputPath),FileUtil.file(outputPath));
</#if>

</#macro>



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
        <#list modelConfig.models as modelInfo>
        <#if modelInfo.groupKey??>
        <#list modelInfo.models as subModelInfo>
        ${subModelInfo.type} ${subModelInfo.fieldName} = dataModel.${modelInfo.groupKey}.${subModelInfo.fieldName};
        </#list>
        <#else>
        ${modelInfo.type} ${modelInfo.fieldName} = dataModel.${modelInfo.fieldName};
        </#if>

        </#list>

        String inputPath;
        String outputPath;

    <#list fileConfig.files as fileInfo>
        <#if fileInfo.groupKey??>
        if (${fileInfo.condition}) {
            <#list fileInfo.files as subFileInfo>
            <@generateFile indent="            " fileInfo=subFileInfo/>
            </#list>
        }
        <#else>
        <#if fileInfo.condition??>
        if (${fileInfo.condition}) {
            <@generateFile indent="            " fileInfo=fileInfo/>
        }
        <#else>
            <@generateFile indent="        " fileInfo=fileInfo/>
        </#if>

        </#if>


    </#list>

    }
    
}
