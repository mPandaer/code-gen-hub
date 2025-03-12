package com.pandaer.web.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.diagnosis.DiagnosisUtils;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.pandaer.web.common.ErrorCode;
import com.pandaer.web.exception.BusinessException;
import com.pandaer.web.mapper.OrderMapper;
import com.pandaer.web.model.dto.pay.PayRequest;
import com.pandaer.web.model.dto.pay.PayResponse;
import com.pandaer.web.model.entity.Order;
import com.pandaer.web.model.enums.OrderPayEnum;
import com.pandaer.web.model.enums.OrderStatusEnum;
import com.pandaer.web.service.PayService;
import com.pandaer.web.utils.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class PayServiceImpl implements PayService {


    @Autowired
    private AlipayClient alipayClient;

    @Autowired
    private OrderMapper orderMapper;



    @Override
    public PayResponse doAlipay(PayRequest payRequest) throws AlipayApiException {
        // 查询订单信息
        Order order = orderMapper.selectById(payRequest.getOrderId());
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"订单不存在");
        }

        // 构造支付请求

        // 构造请求参数以调用接口
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        AlipayTradePagePayModel model = new AlipayTradePagePayModel();
        model.setOutTradeNo(IdUtil.genId("alipay"));
        model.setTotalAmount(order.getAmount().toString());
        model.setSubject(payRequest.getSubject());
        model.setProductCode("FAST_INSTANT_TRADE_PAY");
        request.setBizModel(model);


        // 发起支付流程
        AlipayTradePagePayResponse response = alipayClient.pageExecute(request, "POST");
        String pageRedirectionData = response.getBody();
        if (!response.isSuccess()) {
            String diagnosisUrl = DiagnosisUtils.getDiagnosisUrl(response);
            log.info("发起支付失败,response: {}, 官方诊断地址: {}",pageRedirectionData,diagnosisUrl);
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"支付失败");
        }

        // 修改订单信息
        order.setPaymentMethod(OrderPayEnum.Alipay.getCode());
        order.setOrderStatus(OrderStatusEnum.PAYING.getCode());
        order.setPayTime(new Date());
        // 持久化
        orderMapper.updateById(order);


        // 返回支付页面HTML
        return PayResponse.builder().htmlPage(pageRedirectionData).build();
    }
}
