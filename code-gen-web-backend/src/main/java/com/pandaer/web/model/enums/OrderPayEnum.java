package com.pandaer.web.model.enums;

// 支付方式（如：支付宝/微信）

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum OrderPayEnum {

    Wechat("wechat","微信支付"),
    Alipay("alipay","支付宝支付"),
    UNKNOWN("unknown","非法支付手段")
    ;
    private final String code;
    private final String desc;


    public static OrderPayEnum getByCode(String code) {
        return Stream.of(OrderPayEnum.values()).filter(e -> e.code.equals(code)).findFirst().orElse(UNKNOWN);
    }
}
