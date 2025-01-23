package com.pandaer.maker.template.model;

import com.pandaer.maker.meta.Meta;
import lombok.Data;

/**
 * 制作代码生成器依赖的动态模板文件需要的配置信息
 */
@Data
public class MakingTemplatesConfig {

    /**
     * 制作模板文件时依赖的工作ID
     */
    String workspaceId;

    /**
     * 制作模板文件时，简单的元信息配置 比如 （name,description）
     */
    Meta meta;

    /**
     * 制作模板文件时，原始工程路径
     */
    String originProjectPath;

    /**
     * 制作动态模板文件时，一些模板文件配置信息
     */
    MakingTemplateFilesInfo makingTemplateFilesInfo;

    /**
     * 制作动态模板文件时，依赖的模型参数信息
     */
    MakingTemplateModelsConfig makingTemplateModelsConfig;

    /**
     * 制作动态模板文件时，元信息配置文件的输出规则
     */
    MakingTemplateOutputConfig makingTemplateOutputConfig = new MakingTemplateOutputConfig();
}
