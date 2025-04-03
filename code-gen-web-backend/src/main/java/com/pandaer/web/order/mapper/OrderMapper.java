package com.pandaer.web.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pandaer.web.order.entity.Order;
import org.apache.ibatis.annotations.Mapper;

/**
* @author pandaer
* @description 针对表【order】的数据库操作Mapper
* @createDate 2025-03-12 10:00:41
* @Entity com.pandaer.web.modules.order.entity.Order
*/
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

}




