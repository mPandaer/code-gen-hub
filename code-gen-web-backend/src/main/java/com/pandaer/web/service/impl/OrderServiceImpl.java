package com.pandaer.web.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pandaer.web.common.ErrorCode;
import com.pandaer.web.exception.BusinessException;
import com.pandaer.web.mapper.GeneratorMapper;
import com.pandaer.web.mapper.UserMapper;
import com.pandaer.web.model.dto.order.AddOrderRequest;
import com.pandaer.web.model.entity.Generator;
import com.pandaer.web.model.entity.Order;
import com.pandaer.web.model.entity.User;
import com.pandaer.web.model.vo.GeneratorVO;
import com.pandaer.web.model.vo.OrderVO;
import com.pandaer.web.service.OrderService;
import com.pandaer.web.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author pandaer
* @description 针对表【order】的数据库操作Service实现
* @createDate 2025-03-12 10:00:41
*/
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order>
    implements OrderService{


    @Autowired
    private UserMapper userMapper;


    @Autowired
    private GeneratorMapper generatorMapper;

    @Override
    public OrderVO addOrder(AddOrderRequest addOrderRequest) {
        // 直接保存
        Order order = addOrderRequest.mapToOrder();
        boolean saveRes = save(order);
        if (!saveRes) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"创建订单失败");
        }

        // 将Order转换成一个OrderVO
        return mapToVO(order);
    }

    @Override
    public OrderVO getOrderById(String orderId) {
        // 直接查找
        Order order = lambdaQuery().eq(Order::getOrderId, orderId).one();
        // 将Order转换成一个OrderVO TODO 后期优化成一个转换
        return mapToVO(order);
    }


    // TODO 需要提取出去。
    private OrderVO mapToVO(Order order) {
        OrderVO orderVo = BeanUtil.toBean(order, OrderVO.class);
        User user = userMapper.selectById(order.getUserId());
        orderVo.setUser(user.mapToUserVO());
        Generator generator = generatorMapper.selectById(order.getGeneratorId());
        orderVo.setGenerator(GeneratorVO.objToVo(generator));
        return orderVo;

    }
}




