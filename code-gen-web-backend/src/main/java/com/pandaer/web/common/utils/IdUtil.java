package com.pandaer.web.common.utils;


import cn.hutool.core.util.RandomUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class IdUtil {

    public static String genId(String prefix) {
        LocalDateTime now = LocalDateTime.now();
        String time = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String randomStr = RandomUtil.randomString(8);
        return String.format("%s_%s%s",prefix,time,randomStr);
    }
}
