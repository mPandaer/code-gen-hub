package com.pandaer.web.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pandaer.web.common.ErrorCode;
import com.pandaer.web.converter.UserConverter;
import com.pandaer.web.exception.BusinessException;
import com.pandaer.web.mapper.GeneratorMapper;
import com.pandaer.web.mapper.OrderMapper;
import com.pandaer.web.mapper.UserMapper;
import com.pandaer.web.model.dto.PageResponse;
import com.pandaer.web.model.dto.order.AddOrderRequest;
import com.pandaer.web.model.dto.order.EditOrderRemarkRequest;
import com.pandaer.web.model.dto.order.SearchOrderParams;
import com.pandaer.web.model.entity.*;
import com.pandaer.web.model.enums.OrderStatusEnum;
import com.pandaer.web.model.vo.GeneratorFeeVO;
import com.pandaer.web.model.vo.GeneratorVO;
import com.pandaer.web.model.vo.OrderVO;
import com.pandaer.web.model.vo.UserVO;
import com.pandaer.web.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    private UserConverter userConverter;


    @Autowired
    private GeneratorMapper generatorMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private GeneratorService generatorService;
    @Autowired
    private GeneratorFeeService generatorFeeService;
    @Autowired
    private PayService payService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private UserGeneratorService userGeneratorService;

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

    @Override
    public PageResponse<OrderVO> pageOrders(SearchOrderParams searchOrderRequest) {

        Page<Order> page = lambdaQuery().eq(searchOrderRequest.getOrderId() != null, Order::getOrderId, searchOrderRequest.getOrderId())
                .eq(searchOrderRequest.getUserId() != null, Order::getUserId, searchOrderRequest.getUserId())
                .eq(searchOrderRequest.getGeneratorId() != null, Order::getGeneratorId, searchOrderRequest.getGeneratorId())
                .page(Page.of(searchOrderRequest.getPageNum(), searchOrderRequest.getPageSize()));

        List<Order> orders = page.getRecords();
        Set<Long> userIdSet = orders.stream().map(Order::getUserId).collect(Collectors.toSet());
        Set<Long> generatorIdSet = orders.stream().map(Order::getGeneratorId).collect(Collectors.toSet());
        Map<Long, User> userIdMapping = userService.listByIds(userIdSet).stream().collect(Collectors.toMap(User::getId, Function.identity()));
        Map<Long, Generator> generatorIdMapping = generatorService.listByIds(generatorIdSet).stream().collect(Collectors.toMap(Generator::getId, Function.identity()));
        Map<Long, GeneratorFee> generatorFeeIdMapping = generatorFeeService.listByIds(generatorIdSet).stream().collect(Collectors.toMap(GeneratorFee::getGeneratorId, Function.identity()));


        // 转换对象
        List<OrderVO> data = orders.stream().map(it -> {
            OrderVO bean = BeanUtil.toBean(it, OrderVO.class);
            Long userId = it.getUserId();
            User user = userIdMapping.get(userId);
            if (user != null) {
                UserVO userVO = BeanUtil.toBean(user, UserVO.class);
                bean.setUser(userVO);
            }
            Long generatorId = it.getGeneratorId();
            Generator generator = generatorIdMapping.get(generatorId);
            if (generator != null) {
                GeneratorVO generatorVO = GeneratorVO.objToVo(generator);
                generatorVO.setGeneratorFee(BeanUtil.toBean(generatorFeeIdMapping.get(generatorId), GeneratorFeeVO.class));
                bean.setGenerator(generatorVO);
            }

            return bean;
        }).collect(Collectors.toList());

        PageResponse<OrderVO> orderVOPageResponse = new PageResponse<>();
        orderVOPageResponse.setPageSize((int)page.getSize());
        orderVOPageResponse.setTotal((int)page.getTotal());
        orderVOPageResponse.setPageNum((int)page.getCurrent());
        orderVOPageResponse.setData(data);

        return orderVOPageResponse;


    }

    @Override
    public void editOrderRemark(EditOrderRemarkRequest editOrderRemarkRequest) {
        Order order = getById(editOrderRemarkRequest.getOrderId());
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"订单不存在");
        }

        order.setRemark(editOrderRemarkRequest.getRemark());
        boolean update = updateById(order);
        if (!update) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"修改订单备注失败");
        }
    }

    @Override
    public OrderVO queryOrderStatus(String orderId) {
        Order order = getById(orderId);
        OrderVO orderVO = BeanUtil.toBean(order, OrderVO.class);
        // 异步查询支付宝的订单状态
        if (order.getOrderStatus().equals(OrderStatusEnum.PAYING.getCode())) {
            Boolean res = stringRedisTemplate.opsForValue().setIfAbsent(order.getOrderId(), "doing", 15, TimeUnit.SECONDS);
            if (Boolean.TRUE.equals(res)) {
                CompletableFuture.runAsync(() -> {
                    doQueryOrderStatus(order);
                });
            }

        }

        return orderVO;
    }

    private void doQueryOrderStatus(Order order) {
        String paymentMethod = order.getPaymentMethod();
        Order updatedOrder = payService.queryAndUpdateOrderStatus(order, paymentMethod);
        updateById(updatedOrder);
        Integer orderStatus = updatedOrder.getOrderStatus();
        if (OrderStatusEnum.PAY_SUCCESS.getCode().equals(orderStatus)) {
            // 记录用户购买了这个代码生成器
            UserGenerator userGenerator = new UserGenerator();
            userGenerator.setUserId(order.getUserId());
            userGenerator.setGeneratorId(order.getGeneratorId());
            userGeneratorService.save(userGenerator);
        }

    }


    // TODO 需要提取出去。
    private OrderVO mapToVO(Order order) {
        OrderVO orderVo = BeanUtil.toBean(order, OrderVO.class);
        User user = userMapper.selectById(order.getUserId());
        UserVO userVO = userConverter.entityMapToVO(user);
        orderVo.setUser(userVO);
        Generator generator = generatorMapper.selectById(order.getGeneratorId());
        orderVo.setGenerator(GeneratorVO.objToVo(generator));
        return orderVo;

    }
}




