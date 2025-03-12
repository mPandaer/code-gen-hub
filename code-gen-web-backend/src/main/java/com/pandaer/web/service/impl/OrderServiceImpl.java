package com.pandaer.web.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pandaer.web.model.entity.Order;
import com.pandaer.web.service.OrderService;
import com.pandaer.web.mapper.OrderMapper;
import org.springframework.stereotype.Service;

/**
* @author pandaer
* @description 针对表【order】的数据库操作Service实现
* @createDate 2025-03-12 10:00:41
*/
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order>
    implements OrderService{

}




