package ${basePackage}.acm.cli.command;


import cn.hutool.core.bean.BeanUtil;
import ${basePackage}.acm.generator.CodeFileGenerator;
import ${basePackage}.acm.model.DataModel;
import lombok.Data;
import picocli.CommandLine;

import java.io.File;


<#macro generateOption indent modelInfo>
${indent}@CommandLine.Option(names = {<#if modelInfo.abbr??>"-${modelInfo.abbr}", </#if>"--${modelInfo.fieldName}"}<#if modelInfo.description??>, description = "${modelInfo.description}"</#if>,
${indent}        arity = "0..1", interactive = true, echo = true,required = true)
${indent}private ${modelInfo.type} ${modelInfo.fieldName};
</#macro>






/**
 * 代码生成命令
 */
@Data
@CommandLine.Command(name = "generate", description = "生成模板代码", mixinStandardHelpOptions = true)
public class GenerateCommand implements Runnable {




<#list modelConfig.models as modelInfo>
<#if modelInfo.groupKey??>
    private static DataModel.${modelInfo.type} ${modelInfo.groupKey} = new DataModel.${modelInfo.type}();

    @CommandLine.Command(name = "${modelInfo.groupKey}", description = "${modelInfo.groupName}", mixinStandardHelpOptions = true)
    public static class ${modelInfo.type}Command implements Runnable {
        <#list modelInfo.models as subModelInfo>
            <@generateOption indent="        " modelInfo=subModelInfo/>
        </#list>


        @Override
        public void run() {
            <#list modelInfo.models as subModelInfo>
                ${modelInfo.groupKey}.${subModelInfo.fieldName} = ${subModelInfo.fieldName};
            </#list>
        }
    }
<#else>
    <@generateOption indent="    " modelInfo=modelInfo/>

</#if>

</#list>


    @Override
    public void run() {
        DataModel dataModel = BeanUtil.toBean(this, DataModel.class);

        <#list modelConfig.models as modelInfo>
        <#if modelInfo.groupKey??>
        <#if modelInfo.condition??>
        if (${modelInfo.condition}) {
            new CommandLine(new ${modelInfo.type}Command()).execute("${modelInfo.groupArgsStr}".split(" "));
            dataModel.set${modelInfo.type}(${modelInfo.groupKey});
        }
        <#else>
        new CommandLine(new ${modelInfo.type}Command()).execute("${modelInfo.groupArgsStr}".split(" "));
        dataModel.set${modelInfo.type}(${modelInfo.groupKey});
        </#if>

        </#if>
        </#list>
        // 开始生成代码
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
