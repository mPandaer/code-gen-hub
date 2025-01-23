package com.pandaer.maker.template.enums;


import cn.hutool.core.io.FileUtil;

import java.io.File;
import java.util.stream.Stream;

/**
 * 文件过滤范围
 */
public enum FileFilterRange {

    FILE_NAME("文件名","fileName"){
        @Override
        public String getContent(File file) {
            return file.getName();
        }
    },
    FILE_CONTENT("文件内容","fileContent") {
        @Override
        public String getContent(File file) {
            return FileUtil.readUtf8String(file);
        }
    };

    public final String desc;
    public final String value;

    FileFilterRange(String desc, String value) {
        this.desc = desc;
        this.value = value;
    }


    public static FileFilterRange getFileFilterRange(String value) {
        return Stream.of(values()).filter(it -> it.value.equals(value)).findFirst().orElseThrow(() -> new RuntimeException("文件范围不合法"));
    }

    public abstract String getContent(File file);
}
