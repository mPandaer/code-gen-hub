package com.pandaer.web.order.controller;


import cn.hutool.core.util.StrUtil;
import com.alipay.api.AlipayApiException;
import com.pandaer.web.common.dto.resp.BaseResponse;
import com.pandaer.web.common.dto.resp.PageResponse;
import com.pandaer.web.common.enums.ErrorCode;
import com.pandaer.web.common.exception.BusinessException;
import com.pandaer.web.common.utils.ResultUtils;
import com.pandaer.web.common.validate.ValidatedResult;
import com.pandaer.web.order.dto.req.AddOrderRequest;
import com.pandaer.web.order.dto.req.EditOrderRemarkRequest;
import com.pandaer.web.order.dto.req.SearchOrderParams;
import com.pandaer.web.order.dto.req.pay.PayRequest;
import com.pandaer.web.order.dto.req.pay.PayResponse;
import com.pandaer.web.order.dto.resp.OrderVO;
import com.pandaer.web.order.service.OrderService;
import com.pandaer.web.order.service.PayService;
import com.pandaer.web.user.enums.UserActivityTypeEnum;
import com.pandaer.web.user.event.UserActivityEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@Slf4j
public class OrderController {


    @Autowired
    private OrderService orderService;

    @Autowired
    private PayService payService;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;


    @PostMapping
    public BaseResponse<OrderVO> addOrder(@RequestBody AddOrderRequest addOrderRequest) {
        // 校验参数
        if (addOrderRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ValidatedResult validateRes = addOrderRequest.validate();
        if (!validateRes.isSuccess()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,validateRes.getMessage());
        }
        OrderVO orderVO = orderService.addOrder(addOrderRequest);
        return ResultUtils.success(orderVO);

    }


    @GetMapping("{orderId}")
    public BaseResponse<OrderVO> getOrderById(@PathVariable String orderId) {
        // 校验参数
        if (StrUtil.isBlankIfStr(orderId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        OrderVO orderVO = orderService.getOrderById(orderId);
        return ResultUtils.success(orderVO);

    }

    @PostMapping("pay")
    public BaseResponse<PayResponse> payOrder(@RequestBody PayRequest payRequest) {

        if (payRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        ValidatedResult validateRes = payRequest.validate();

        if (!validateRes.isSuccess()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,validateRes.getMessage());
        }

        PayResponse payResponse;
        try {
            payResponse = payService.doAlipay(payRequest);
            // 应该发布两个事件，一个给代码生成器的作者加经验，一个是当购买成功后给购买者加经验
            OrderVO order = orderService.getOrderById(payRequest.getOrderId());
            Long authorUserId = order.getGenerator().getUserId();
            Long buyUserId = order.getUser().getId();
            applicationEventPublisher.publishEvent(new UserActivityEvent(authorUserId, UserActivityTypeEnum.PURCHASE_GENERATOR,this));

            // TODO 需要真正购买成功之后再加经验
            applicationEventPublisher.publishEvent(new UserActivityEvent(buyUserId, UserActivityTypeEnum.PURCHASE_GENERATOR,this));

        } catch (AlipayApiException e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR,e.getMessage());
        }

        return ResultUtils.success(payResponse);
    }

    @GetMapping
    public BaseResponse<PageResponse<OrderVO>> pageOrders(SearchOrderParams searchOrderRequest) {
        // 查询数据
        PageResponse<OrderVO> page = orderService.pageOrders(searchOrderRequest);

        // 返回结果
        return ResultUtils.success(page);

    }


    @PostMapping("remark")
    public BaseResponse<?> editOrderRemark(@RequestBody EditOrderRemarkRequest editOrderRemarkRequest) {
        // 校验参数
        if (editOrderRemarkRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        ValidatedResult validateRes = editOrderRemarkRequest.validate();

        if (!validateRes.isSuccess()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,validateRes.getMessage());
        }

        // 更新备注信息
        orderService.editOrderRemark(editOrderRemarkRequest);

        // 返回结果
        return ResultUtils.success(null);
    }


    @GetMapping("{id}/status")
    public BaseResponse<OrderVO> queryOrderStatus(@PathVariable("id") String orderId) {
        // 校验参数
        if (StrUtil.isBlankIfStr(orderId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 查询状态
        OrderVO orderVO = orderService.queryOrderStatus(orderId);

        // 返回结果
        return ResultUtils.success(orderVO);
    }

}
