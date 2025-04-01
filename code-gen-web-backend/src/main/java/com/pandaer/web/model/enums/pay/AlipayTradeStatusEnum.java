package com.pandaer.web.model.enums.pay;

import com.pandaer.web.model.enums.OrderPayEnum;
import com.pandaer.web.model.enums.OrderStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AlipayTradeStatusEnum {

    WAIT_BUYER_PAY("WAIT_BUYER_PAY", "交易创建，等待买家付款"),
    TRADE_CLOSED("TRADE_CLOSED", "未付款交易超时关闭，或支付完成后全额退款"),
    TRADE_SUCCESS("TRADE_SUCCESS", "交易支付成功"),
    TRADE_FINISHED("TRADE_FINISHED", "交易结束，不可退款"),
    TRADE_ERROR("TRADE_ERROR", "交易错误，联系管理员")

    ;
    private final String code;
    private final String desc;


    public static AlipayTradeStatusEnum getByCode(String code) {
        for (AlipayTradeStatusEnum tradeStatusEnum : AlipayTradeStatusEnum.values()) {
            if (tradeStatusEnum.getCode().equals(code)) {
                return tradeStatusEnum;
            }
        }
        return TRADE_ERROR;
    }

    public OrderStatusEnum mapToOrderStatus() {
        if (this == WAIT_BUYER_PAY) {
            return OrderStatusEnum.WAIT_PAY;
        } else if (this == TRADE_SUCCESS) {
            return OrderStatusEnum.PAY_SUCCESS;
        } else if (this == TRADE_CLOSED) {
            return OrderStatusEnum.CANCEL;
        } else if (this == TRADE_FINISHED) {
            return OrderStatusEnum.PAY_SUCCESS;
        } else {
            return OrderStatusEnum.UNKNOWN;
        }
    }

}
