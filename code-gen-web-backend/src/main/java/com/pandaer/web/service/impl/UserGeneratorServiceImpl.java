package com.pandaer.web.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pandaer.web.common.ErrorCode;
import com.pandaer.web.exception.BusinessException;
import com.pandaer.web.model.entity.UserGenerator;
import com.pandaer.web.service.UserGeneratorService;
import com.pandaer.web.mapper.UserGeneratorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author pandaer
* @description 针对表【user_generator】的数据库操作Service实现
* @createDate 2025-03-12 10:00:41
*/
@Service
public class UserGeneratorServiceImpl extends ServiceImpl<UserGeneratorMapper, UserGenerator>
    implements UserGeneratorService{


    @Override
    public UserGenerator getGeneratorForPurchase(Long userId, Long generatorId) {
        // 直接根据用户ID和代码生成器ID查询对应的记录
        UserGenerator purchasedGenerator = lambdaQuery()
                .eq(UserGenerator::getUserId, userId).eq(UserGenerator::getGeneratorId, generatorId).one();

//        if (purchasedGenerator == null) {
//            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"您没有购买这个代码生成器");
//        }

        return purchasedGenerator;
    }
}




