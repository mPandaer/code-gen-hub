package com.pandaer.web.user.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户活动类型枚举
 */

@AllArgsConstructor
@Getter
public enum UserActivityTypeEnum {

    CREATE_GENERATOR(50,"创建代码生成器"),
    REVIEW_OK_GENERATOR(100,"代码生成器审核通过"),
    USED_GENERATOR(2,"在线使用代码生成器"),
    DOWNLOAD_GENERATOR(2,"下载代码生成器"),
    PURCHASE_GENERATOR(20,"购买代码生成器"),
    PUBLISH_COMMENT(5,"发布评论"),
    USER_REGISTER(100,"新用户注册")
    ;
    private final Integer exp;

    private final String desc;
}
