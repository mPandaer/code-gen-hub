package com.pandaer.maker.template.model;

import lombok.Data;

import java.util.List;

@Data
public class TemplateFileConfig {

    // 保证相对路径
    private String path;

    private List<FileFilterConfig> fileFilterConfigs;

    private String condition;

    @Data
    public static class FileFilterConfig {

        private String range;

        private String rule;

        private String value;
    }
}
