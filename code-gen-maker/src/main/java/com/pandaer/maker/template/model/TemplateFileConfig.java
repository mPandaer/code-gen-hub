package com.pandaer.maker.template.model;

import com.pandaer.maker.template.enums.FileFilterRange;
import com.pandaer.maker.template.enums.FileFilterRule;
import lombok.Data;

import java.util.List;

@Data
public class TemplateFileConfig {

    // 保证相对路径
    private String path;

    private List<FileFilterConfig> fileFilterConfigs;

    @Data
    public static class FileFilterConfig {

        private FileFilterRange fileFilterRange;

        private FileFilterRule fileFilterRule;

        private String value;
    }
}
