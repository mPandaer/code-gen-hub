package com.pandaer.web.user.init;

import cn.hutool.json.JSONUtil;
import com.pandaer.web.user.entity.UserLevelPrivilege;
import com.pandaer.web.user.service.UserLevelPrivilegeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class UserLevelPrivilegeRunner implements CommandLineRunner {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private UserLevelPrivilegeService userLevelPrivilegeService;

    private static final String USER_LEVEL_PRIVILEGE_KEY = "user:level:privilege";

    @Override
    public void run(String... args) throws Exception {
        List<UserLevelPrivilege> list = userLevelPrivilegeService.list();
        String jsonStr = JSONUtil.toJsonStr(list);
        stringRedisTemplate.opsForValue().setIfAbsent(USER_LEVEL_PRIVILEGE_KEY,jsonStr);
        log.info("加载用户权益信息到Redis成功！");
    }
}
