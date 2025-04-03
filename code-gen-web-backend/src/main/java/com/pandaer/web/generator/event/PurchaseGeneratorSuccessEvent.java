package com.pandaer.web.generator.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 代码生成器被成功购买事件
 */

@Getter
public class PurchaseGeneratorSuccessEvent extends ApplicationEvent {
    private final String orderId;

    public PurchaseGeneratorSuccessEvent(String orderId,Object source) {
        super(source);
        this.orderId = orderId;
    }
}
