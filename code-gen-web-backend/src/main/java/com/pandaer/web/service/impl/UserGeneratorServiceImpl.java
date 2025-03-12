package com.pandaer.web.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pandaer.web.model.entity.UserGenerator;
import com.pandaer.web.service.UserGeneratorService;
import com.pandaer.web.mapper.UserGeneratorMapper;
import org.springframework.stereotype.Service;

/**
* @author pandaer
* @description 针对表【user_generator】的数据库操作Service实现
* @createDate 2025-03-12 10:00:41
*/
@Service
public class UserGeneratorServiceImpl extends ServiceImpl<UserGeneratorMapper, UserGenerator>
    implements UserGeneratorService{

}




