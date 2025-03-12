package com.pandaer.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pandaer.web.model.entity.UserGenerator;

/**
* @author pandaer
* @description 针对表【user_generator】的数据库操作Service
* @createDate 2025-03-12 10:00:41
*/
public interface UserGeneratorService extends IService<UserGenerator> {

    UserGenerator getGeneratorForPurchase(Long userId,Long generatorId);
}
