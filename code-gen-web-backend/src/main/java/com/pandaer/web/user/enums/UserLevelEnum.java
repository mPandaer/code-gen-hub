package com.pandaer.web.user.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserLevelEnum {
    LEVEL_1("新手开发者", 1),
    LEVEL_2("进阶开发者", 2),
    LEVEL_3("专业开发者", 3),
    LEVEL_4("资深开发者", 4),
    LEVEL_5("专家开发者", 5),
    LEVEL_6("大师开发者", 6),
    ;
    private final String desc;
    private final Integer level;


}
