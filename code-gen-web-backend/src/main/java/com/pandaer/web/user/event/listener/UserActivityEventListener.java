package com.pandaer.web.user.event.listener;

import com.pandaer.web.user.entity.User;
import com.pandaer.web.user.entity.UserLevelPrivilege;
import com.pandaer.web.user.enums.UserActivityTypeEnum;
import com.pandaer.web.user.event.UserActivityEvent;
import com.pandaer.web.user.service.UserLevelPrivilegeService;
import com.pandaer.web.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class UserActivityEventListener {


    @Autowired
    private UserService userService;

    @Autowired
    private UserLevelPrivilegeService userLevelPrivilegeService;

    /**
     * 更新用户的经验值
     * @param event
     */
    @Async
//    @TransactionalEventListener(classes = UserActivityEvent.class)
    @EventListener(classes = UserActivityEvent.class)
    public void onUserActivityEvent(UserActivityEvent event){
        log.info("用户活动事件监听器监听到事件：{}",event);
        Long userId = event.getUserId();
        UserActivityTypeEnum userActivityType = event.getUserActivityType();

        // 获取用户信息
        User user = userService.getById(userId);
        if (user == null) {
            log.info("用户ID：{} 对应的用户不存在",userId);
            return;
        }

        List<UserLevelPrivilege> privileges = userLevelPrivilegeService.list();

        Map<Integer, UserLevelPrivilege> levelPrivilegeMap = privileges.stream().collect(Collectors.toMap(UserLevelPrivilege::getLevel, Function.identity()));

        // 更新经验值，以及用户等级，但是必须同步更新
        synchronized (userId.toString().intern()) {
            String desc = userActivityType.getDesc();
            Integer exp = userActivityType.getExp();
            Integer userExp = user.getExperience() == null ? 0 : user.getExperience();
            Integer latestExp = userExp + exp;
            user.setExperience(latestExp);

            // 判断是否可以更新用户等级
            Optional<UserLevelPrivilege> privilege = privileges.stream()
                    .filter(it -> latestExp >= it.getMinExp() && latestExp <= it.getMaxExp())
                    .findFirst();

            if (privilege.isPresent()) {
                int currentUserLevel = user.getUserLevel() == null ? 0 : user.getUserLevel();
                Integer level = privilege.get().getLevel();

                // 升级，权益也需要更新
                if (currentUserLevel < level) {
                    user.setUserLevel(level);

                    // 更新用户的剩余提现额度
                    UserLevelPrivilege currentPrivilege = levelPrivilegeMap.get(currentUserLevel);
                    UserLevelPrivilege latestPrivilege = levelPrivilegeMap.get(level);

                    BigDecimal monthlyQuota = user.getMonthlyQuota();
                    if (monthlyQuota == null) {
                        monthlyQuota = latestPrivilege.getMonthlyQuota();
                    }else {
                        BigDecimal res = monthlyQuota.add(latestPrivilege.getMonthlyQuota().subtract(currentPrivilege.getMonthlyQuota()));
                        user.setMonthlyQuota(res);
                    }



                }
            }

            // 持久化到数据库
            userService.updateById(user);
            log.info("用户ID：{} 完成了{} 经验值更新为：{}",userId,desc,latestExp);
        }




    }
}
