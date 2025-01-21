package com.pandaer.maker.template.model;

import lombok.Data;

/**
 *         "groupKey": "mainTemplate",
 *         "groupName": "核心模版代码内容数据组",
 *         "type": "MainTemplate",
 *         "condition": "needCodeFile",
 */
@Data
public class ModelGroupConfig {
    private String groupKey;
    private String groupName;
    private String condition;
}
