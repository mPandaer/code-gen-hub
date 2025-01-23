package com.pandaer.maker.template.enums;

import cn.hutool.core.util.StrUtil;

import java.util.stream.Stream;

/**
 * 文件过滤规则
 */
public enum FileFilterRule {

    CONTAINS("包含","contains") {
        @Override
        public boolean apply(String content, String value) {
            if (StrUtil.hasBlank(content,value)) {
                return false;
            }
            return content.contains(value);
        }
    },
    STARTS_WITH("前缀匹配","startsWith") {
        @Override
        public boolean apply(String content, String value) {
            if (StrUtil.hasBlank(content,value)) {
                return false;
            }
            return content.startsWith(value);
        }
    },
    ENDS_WITH("后缀匹配","endsWith") {
        @Override
        public boolean apply(String content, String value) {
            if (StrUtil.hasBlank(content,value)) {
                return false;
            }
            return content.endsWith(value);
        }
    },
    REGEX("正则匹配","regex") {
        @Override
        public boolean apply(String content, String value) {
            if (StrUtil.hasBlank(content,value)) {
                return false;
            }
            return content.matches(value);
        }
    },
    EQUALS("相等","equals") {
        @Override
        public boolean apply(String content, String value) {
            if (StrUtil.hasBlank(content,value)) {
                return false;
            }
            return content.equals(value);
        }
    },
    ;

    public final String desc;
    public final String value;

    FileFilterRule(String desc, String value) {
        this.desc = desc;
        this.value = value;
    }


    public static FileFilterRule getFileFilterRule(String value) {
        return Stream.of(values()).filter(it -> it.value.equals(value)).findFirst().orElseThrow(() -> new RuntimeException("文件过滤规则不合法"));
    }

    public abstract boolean apply(String content,String value);
}
