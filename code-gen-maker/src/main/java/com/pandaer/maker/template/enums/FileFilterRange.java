package com.pandaer.maker.template.enums;


import cn.hutool.core.io.FileUtil;

import java.io.File;

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

    public abstract String getContent(File file);
}
