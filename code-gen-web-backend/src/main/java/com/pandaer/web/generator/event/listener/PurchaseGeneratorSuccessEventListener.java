package com.pandaer.web.generator.event.listener;

import com.pandaer.web.generator.entity.Generator;
import com.pandaer.web.generator.event.PurchaseGeneratorSuccessEvent;
import com.pandaer.web.generator.service.GeneratorService;
import com.pandaer.web.order.entity.Order;
import com.pandaer.web.order.service.OrderService;
import com.pandaer.web.user.entity.User;
import com.pandaer.web.user.entity.UserLevelPrivilege;
import com.pandaer.web.user.service.UserLevelPrivilegeService;
import com.pandaer.web.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
public class PurchaseGeneratorSuccessEventListener {


    @Autowired
    private UserService userService;


    @Autowired
    private OrderService orderService;


    @Autowired
    private GeneratorService generatorService;

    @Autowired
    private UserLevelPrivilegeService userLevelPrivilegeService;




    @Async
    @EventListener(classes = PurchaseGeneratorSuccessEvent.class)
    public void onPurchaseGeneratorSuccessEvent(PurchaseGeneratorSuccessEvent purchaseGeneratorSuccessEvent) {
        log.info("订单{} 支付成功,进行收益计算！", purchaseGeneratorSuccessEvent.getOrderId());
        Order order = orderService.getById(purchaseGeneratorSuccessEvent.getOrderId());
        Generator generator = generatorService.getById(order.getGeneratorId());
        // 没有找到对应的代码生成器，无法进行收益计算
        if (generator == null) {
            return;
        }

        // 获取创造者信息
        Long creatorId = generator.getUserId();
        User creator = userService.getById(creatorId);

        // 获取对应的等级信息
        UserLevelPrivilege privilege = userLevelPrivilegeService.getById(creator.getUserLevel());

        // 佣金率 比如30%
        Integer commissionRate = privilege.getCommissionRate();

        // 按照分成收益进行金币分成
        BigDecimal amount = order.getAmount();
        // TODO 可能需要额外的一张日志表来记录平台的收益记录方便后台管理 这里暂时不做记录，直接修改用户的金币数量

        BigDecimal userAmount = amount.multiply(new BigDecimal(commissionRate / 100D));
        BigDecimal originCoins = creator.getGoldCoins();
        creator.setGoldCoins(originCoins.add(userAmount));
        // 持久化用户信息
        userService.updateById(creator);


    }
}
