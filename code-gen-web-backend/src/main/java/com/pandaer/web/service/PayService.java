package com.pandaer.web.service;

import com.alipay.api.AlipayApiException;
import com.pandaer.web.model.dto.pay.PayRequest;
import com.pandaer.web.model.dto.pay.PayResponse;
import com.pandaer.web.model.entity.Order;

public interface PayService {


    PayResponse doAlipay(PayRequest payRequest) throws AlipayApiException;


    // 查询订单状态
    Order queryAndUpdateOrderStatus(Order order, String paymentMethod);
}
