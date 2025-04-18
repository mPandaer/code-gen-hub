package com.pandaer.web.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pandaer.web.user.entity.UserIncomeDetail;
import org.apache.ibatis.annotations.Mapper;

/**
* @author pandaer
* @description 针对表【user_income_detail(用户收益明细表)】的数据库操作Mapper
* @createDate 2025-03-29 10:05:55
* @Entity com.pandaer.web.modules.user.entity.UserIncomeDetail
*/
@Mapper
public interface UserIncomeDetailMapper extends BaseMapper<UserIncomeDetail> {

}




