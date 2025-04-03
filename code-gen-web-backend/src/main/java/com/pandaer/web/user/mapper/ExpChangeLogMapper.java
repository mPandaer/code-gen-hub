package com.pandaer.web.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pandaer.web.user.entity.ExpChangeLog;
import org.apache.ibatis.annotations.Mapper;

/**
* @author pandaer
* @description 针对表【exp_change_log(用户经验值变更日志)】的数据库操作Mapper
* @createDate 2025-03-29 10:05:55
* @Entity com.pandaer.web.modules.user.entity.ExpChangeLog
*/
@Mapper
public interface ExpChangeLogMapper extends BaseMapper<ExpChangeLog> {

}




