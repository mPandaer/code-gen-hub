package com.pandaer.maker.template.model;

import lombok.Data;

import java.util.List;

/**
 * 一个包装类，记录用户在一次制作过程中，需要制作哪些模板文件，并标记这次制作是否为一个文件组的制作
 */
@Data
public class MakingTemplateFilesInfo {

    private List<TemplateFileConfig> files;


    /**
     * 文件组配置信息
     */
    private FileGroupConfig fileGroupConfig;

}
