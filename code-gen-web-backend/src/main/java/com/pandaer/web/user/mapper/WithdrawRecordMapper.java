package com.pandaer.web.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pandaer.web.user.entity.WithdrawRecord;
import org.apache.ibatis.annotations.Mapper;

/**
* @author pandaer
* @description 针对表【withdraw_record(用户提现记录表)】的数据库操作Mapper
* @createDate 2025-03-29 10:05:55
* @Entity com.pandaer.web.modules.user.entity.WithdrawRecord
*/
@Mapper
public interface WithdrawRecordMapper extends BaseMapper<WithdrawRecord> {

}




