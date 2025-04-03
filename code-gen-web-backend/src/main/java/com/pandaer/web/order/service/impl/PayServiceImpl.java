package com.pandaer.web.order.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.diagnosis.DiagnosisUtils;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.pandaer.web.common.enums.ErrorCode;
import com.pandaer.web.common.exception.BusinessException;
import com.pandaer.web.common.utils.IdUtil;
import com.pandaer.web.order.dto.req.pay.PayRequest;
import com.pandaer.web.order.dto.req.pay.PayResponse;
import com.pandaer.web.order.entity.Order;
import com.pandaer.web.order.enums.AlipayTradeStatusEnum;
import com.pandaer.web.order.enums.OrderPayEnum;
import com.pandaer.web.order.enums.OrderStatusEnum;
import com.pandaer.web.order.mapper.OrderMapper;
import com.pandaer.web.order.service.PayService;
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
        String outTradeNo = IdUtil.genId("alipay");
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        AlipayTradePagePayModel model = new AlipayTradePagePayModel();
        model.setOutTradeNo(outTradeNo);
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
        order.setOutTradeNo(outTradeNo);
        // 持久化
        orderMapper.updateById(order);


        // 返回支付页面HTML
        return PayResponse.builder().htmlPage(pageRedirectionData).build();
    }


    // 查询订单状态
    @Override
    public Order queryAndUpdateOrderStatus(Order order, String paymentMethod) {

        OrderPayEnum payMethod = OrderPayEnum.getByCode(paymentMethod);

        if (payMethod == OrderPayEnum.Alipay) {
            // 构造请求参数以调用接口
            AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
            AlipayTradeQueryModel model = new AlipayTradeQueryModel();

            // 设置订单支付时传入的商户订单号
            model.setOutTradeNo(order.getOutTradeNo());
            request.setBizModel(model);

            AlipayTradeQueryResponse response = null;
            try {
                response = alipayClient.execute(request);
            } catch (AlipayApiException e) {
                throw new RuntimeException(e);
            }

            if (response.isSuccess()) {
                String code = response.getTradeStatus();
                AlipayTradeStatusEnum alipayTradeStatus = AlipayTradeStatusEnum.getByCode(code);
                OrderStatusEnum orderStatusEnum = alipayTradeStatus.mapToOrderStatus();
                if (orderStatusEnum == OrderStatusEnum.UNKNOWN || orderStatusEnum == OrderStatusEnum.PAY_FAIL ||
                        orderStatusEnum == OrderStatusEnum.PAY_SUCCESS || orderStatusEnum == OrderStatusEnum.CANCEL) {
                    order.setOrderStatus(orderStatusEnum.getCode());
                }

            } else {
                 String diagnosisUrl = DiagnosisUtils.getDiagnosisUrl(response);
                log.error("查询支付宝订单状态接口失败：{} {}", diagnosisUrl, response.getBody());
            }
        }else {
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"不支持的支付方式,目前仅仅支持支付宝支付");
        }

        return order;
    }
}
