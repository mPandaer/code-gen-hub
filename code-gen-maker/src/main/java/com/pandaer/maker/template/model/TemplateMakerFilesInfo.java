package com.pandaer.maker.template.model;

import lombok.Data;

import java.util.List;

@Data
public class TemplateMakerFilesInfo {

    private List<TemplateFileConfig> files;


    /**
     * 文件组配置信息
     */
    private FileGroupConfig fileGroupConfig;

}
