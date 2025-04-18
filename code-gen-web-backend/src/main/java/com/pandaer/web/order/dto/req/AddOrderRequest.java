package com.pandaer.web.order.dto.req;

import cn.hutool.core.util.ObjectUtil;
import com.pandaer.web.common.utils.IdUtil;
import com.pandaer.web.common.validate.Validatable;
import com.pandaer.web.common.validate.ValidatedResult;
import com.pandaer.web.order.entity.Order;
import com.pandaer.web.order.enums.OrderStatusEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class AddOrderRequest implements Validatable {


    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 代码生成器ID
     */
    private Long generatorId;

    /**
     * 订单金额（单位：元）
     */
    private BigDecimal amount;


    public Order mapToOrder() {
        Order order = new Order();
        order.setOrderId(IdUtil.genId("order"));
        order.setUserId(userId);
        order.setGeneratorId(generatorId);
        order.setAmount(amount);
        order.setOrderStatus(OrderStatusEnum.WAIT_PAY.getCode());
        order.setCreateTime(new Date());
        return order;

    }

    @Override
    public ValidatedResult validate() {
        if (ObjectUtil.hasNull(userId, generatorId, amount)) {
            return ValidatedResult.fail("参数不完整");
        }
        if (amount.doubleValue() <= 0) {
            return ValidatedResult.fail("订单金额必须大于0");
        }

        return ValidatedResult.success();
    }
}
