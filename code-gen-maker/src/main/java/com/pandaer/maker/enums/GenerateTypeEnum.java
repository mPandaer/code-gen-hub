package com.pandaer.maker.enums;

public enum GenerateTypeEnum {
    DYNAMIC("动态生成","dynamic"),
    STATIC("静态生成","static");
    ;
    public final String desc;

    public final String value;

    GenerateTypeEnum(String desc, String value) {
        this.desc = desc;
        this.value = value;
    }
}
