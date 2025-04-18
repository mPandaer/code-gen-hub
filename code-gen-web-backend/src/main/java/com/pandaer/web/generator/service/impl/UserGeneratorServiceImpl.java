package com.pandaer.web.generator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pandaer.web.generator.entity.UserGenerator;
import com.pandaer.web.generator.mapper.UserGeneratorMapper;
import com.pandaer.web.generator.service.UserGeneratorService;
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




