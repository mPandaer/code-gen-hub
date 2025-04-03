package com.pandaer.web.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pandaer.web.user.entity.UserLevelPrivilege;
import org.apache.ibatis.annotations.Mapper;

/**
* @author pandaer
* @description 针对表【user_level_privilege(等级权益配置表)】的数据库操作Mapper
* @createDate 2025-03-29 10:05:55
* @Entity com.pandaer.web.modules.user.entity.UserLevelPrivilege
*/
@Mapper
public interface UserLevelPrivilegeMapper extends BaseMapper<UserLevelPrivilege> {

}




