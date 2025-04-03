package com.pandaer.web.order.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

/**
 * 订单状态：0-待支付，1-已支付，2-已取消，3-支付失败
 */


@AllArgsConstructor
@Getter
public enum OrderStatusEnum {

    WAIT_PAY(0,"待支付"),
    PAY_SUCCESS(1,"已支付"),
    CANCEL(2,"已取消"),
    PAY_FAIL(3,"支付失败"),
    PAYING(4,"支付中"),
    UNKNOWN(9999,"非法状态")
    ;

    private final Integer code;
    private final String desc;


    public static OrderStatusEnum getByCode(Integer code) {
        return Stream.of(values()).filter(e -> e.getCode().equals(code)).findFirst().orElse(UNKNOWN);
    }

}
