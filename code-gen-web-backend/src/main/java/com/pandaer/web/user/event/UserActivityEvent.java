package com.pandaer.web.user.event;

import com.pandaer.web.user.enums.UserActivityTypeEnum;
import lombok.Getter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

/**
 * 用户活动事件
 */
@ToString
@Getter
public class UserActivityEvent extends ApplicationEvent {

    private final Long userId;

    private final UserActivityTypeEnum userActivityType;

    public UserActivityEvent(Long userId, UserActivityTypeEnum userActivityType,Object source) {
        super(source);
        this.userId = userId;
        this.userActivityType = userActivityType;
    }
}
