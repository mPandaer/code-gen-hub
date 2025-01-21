package com.pandaer.maker.enums;

/**
 * 文件类型枚举
 */
public enum FileTypeEnum {
    DIR("目录","dir"),
    GROUP("文件分组","group"),
    FILE("文件","file");


    public final String desc;

    public final String value;

    FileTypeEnum(String desc, String value) {
        this.desc = desc;
        this.value = value;
    }
}
