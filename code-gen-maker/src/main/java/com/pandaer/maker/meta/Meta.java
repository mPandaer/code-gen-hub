package com.pandaer.maker.meta;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 生成器的元信息
 */
@NoArgsConstructor
@Data
public class Meta {

    private String name;
    private String description;
    private String basePackage;
    private String version;
    private String author;
    private String createTime;
    private FileConfig fileConfig;
    private ModelConfig modelConfig;

    @NoArgsConstructor
    @Data
    public static class FileConfig {
        /**
         * 原始模板项目路径
         */
        private String originProjectPath;

        /**
         * 生成定制化项目的路径
         */
        private String generatedProjectPath;

        /**
         * 制作好的生成器路径
         */
        private String generatorPath;
        private String type;
        private List<FileInfo> files;

        @NoArgsConstructor
        @Data
        public static class FileInfo {
            private String inputPath;
            private String outputPath;
            private String type;
            private String generateType;
            private String condition;

            // 分组信息
            private String groupKey;
            private String groupName;
            private List<FileInfo> files;
        }
    }

    @NoArgsConstructor
    @Data
    public static class ModelConfig {
        private List<ModelInfo> models;

        @NoArgsConstructor
        @Data
        public static class ModelInfo {
            private String fieldName;
            private String type;
            private String description;
            private Object defaultValue;
            private String abbr;

            // 分组信息
            private String groupKey;
            private String groupName;
            private String condition;
            private List<ModelInfo> models;

            // 临时参数
            private String groupArgsStr;







        }
    }
}
