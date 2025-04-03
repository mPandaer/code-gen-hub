package com.pandaer.web.generator.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum GeneratorStatus {

    WAIT_AUDIT("待审核", 0),
    AUDIT_PASS("审核通过", 1),
    AUDIT_FAIL("审核不通过", 2),
    ;
    private final String desc;

    private final Integer value;
}
