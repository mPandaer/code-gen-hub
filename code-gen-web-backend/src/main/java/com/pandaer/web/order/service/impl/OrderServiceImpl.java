package com.pandaer.web.order.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pandaer.web.common.dto.resp.PageResponse;
import com.pandaer.web.common.enums.ErrorCode;
import com.pandaer.web.common.exception.BusinessException;
import com.pandaer.web.generator.dto.resp.GeneratorFeeVO;
import com.pandaer.web.generator.dto.resp.GeneratorVO;
import com.pandaer.web.generator.entity.Generator;
import com.pandaer.web.generator.entity.GeneratorFee;
import com.pandaer.web.generator.entity.UserGenerator;
import com.pandaer.web.generator.event.PurchaseGeneratorSuccessEvent;
import com.pandaer.web.generator.mapper.GeneratorMapper;
import com.pandaer.web.generator.service.GeneratorFeeService;
import com.pandaer.web.generator.service.GeneratorService;
import com.pandaer.web.generator.service.UserGeneratorService;
import com.pandaer.web.order.dto.req.AddOrderRequest;
import com.pandaer.web.order.dto.req.EditOrderRemarkRequest;
import com.pandaer.web.order.dto.req.SearchOrderParams;
import com.pandaer.web.order.dto.resp.OrderVO;
import com.pandaer.web.order.entity.Order;
import com.pandaer.web.order.enums.OrderStatusEnum;
import com.pandaer.web.order.mapper.OrderMapper;
import com.pandaer.web.order.service.OrderService;
import com.pandaer.web.order.service.PayService;
import com.pandaer.web.user.converter.UserConverter;
import com.pandaer.web.user.dto.resp.UserVO;
import com.pandaer.web.user.entity.User;
import com.pandaer.web.user.entity.UserLevelPrivilege;
import com.pandaer.web.user.enums.UserActivityTypeEnum;
import com.pandaer.web.user.event.UserActivityEvent;
import com.pandaer.web.user.mapper.UserMapper;
import com.pandaer.web.user.service.UserLevelPrivilegeService;
import com.pandaer.web.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
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
    implements OrderService {


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

    @Autowired
    private UserLevelPrivilegeService userLevelPrivilegeService;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

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
            // TODO 发布一个事件，表示某个用户支付成功了一个代码生成器，需要进行金币分成
            applicationEventPublisher.publishEvent(new UserActivityEvent(order.getUserId(), UserActivityTypeEnum.PURCHASE_GENERATOR,this));
            applicationEventPublisher.publishEvent(new PurchaseGeneratorSuccessEvent(order.getOrderId(), this));
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




