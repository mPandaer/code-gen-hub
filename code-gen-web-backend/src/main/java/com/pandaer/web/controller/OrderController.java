package com.pandaer.web.controller;


import cn.hutool.core.util.StrUtil;
import com.alipay.api.AlipayApiException;
import com.pandaer.web.common.BaseResponse;
import com.pandaer.web.common.ErrorCode;
import com.pandaer.web.common.ResultUtils;
import com.pandaer.web.exception.BusinessException;
import com.pandaer.web.model.dto.PageResponse;
import com.pandaer.web.model.dto.order.EditOrderRemarkRequest;
import com.pandaer.web.model.dto.order.AddOrderRequest;
import com.pandaer.web.model.dto.order.SearchOrderParams;
import com.pandaer.web.model.dto.pay.PayRequest;
import com.pandaer.web.model.dto.pay.PayResponse;
import com.pandaer.web.model.vo.OrderVO;
import com.pandaer.web.service.OrderService;
import com.pandaer.web.service.PayService;
import com.pandaer.web.validate.ValidatedResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@Slf4j
public class OrderController {


    @Autowired
    private OrderService orderService;

    @Autowired
    private PayService payService;


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

}
