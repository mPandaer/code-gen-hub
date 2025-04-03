package com.pandaer.web.generator.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pandaer.web.generator.entity.UserGenerator;
import org.apache.ibatis.annotations.Mapper;

/**
* @author pandaer
* @description 针对表【user_generator】的数据库操作Mapper
* @createDate 2025-03-12 10:00:41
* @Entity com.pandaer.web.modules.generator.entity.UserGenerator
*/
@Mapper
public interface UserGeneratorMapper extends BaseMapper<UserGenerator> {

}




