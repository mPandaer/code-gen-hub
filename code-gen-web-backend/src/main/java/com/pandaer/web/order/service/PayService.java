package com.pandaer.web.order.service;

import com.alipay.api.AlipayApiException;
import com.pandaer.web.order.dto.req.pay.PayRequest;
import com.pandaer.web.order.dto.req.pay.PayResponse;
import com.pandaer.web.order.entity.Order;

public interface PayService {


    PayResponse doAlipay(PayRequest payRequest) throws AlipayApiException;


    // 查询订单状态
    Order queryAndUpdateOrderStatus(Order order, String paymentMethod);
}
