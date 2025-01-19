package ${basePackage}.acm.cli.command;


import cn.hutool.core.bean.BeanUtil;
import ${basePackage}.acm.generator.CodeFileGenerator;
import ${basePackage}.acm.model.DataModel;
import lombok.Data;
import picocli.CommandLine;

import java.io.File;

/**
 * 代码生成命令
 */
@Data
@CommandLine.Command(name = "generate", description = "生成模板代码", mixinStandardHelpOptions = true)
public class GenerateCommand implements Runnable {




<#list modelConfig.models as modelInfo>
    @CommandLine.Option(names = {<#if modelInfo.abbr??>"-${modelInfo.abbr}", </#if>"--${modelInfo.fieldName}"}<#if modelInfo.description??>, description = "${modelInfo.description}"</#if>,
            arity = "0..1", interactive = true, echo = true,required = true)
    private ${modelInfo.type} ${modelInfo.fieldName};
</#list>


    @Override
    public void run() {
        DataModel dataModel = BeanUtil.toBean(this, DataModel.class);
        CodeFileGenerator codeFileGenerator = new CodeFileGenerator(dataModel);
        // 原始项目
        String originProjectDirPath = "${fileConfig.originProjectPath}";
        File originProject = new File(originProjectDirPath);
        // 生成的项目
        String generateProjectDirPath = "${fileConfig.generatedProjectPath}";
        File generateProject = new File(generateProjectDirPath);
        // 生成代码
        codeFileGenerator.generator(originProject,generateProject);
    }
}
