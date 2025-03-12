package com.pandaer.web.service;

import com.alipay.api.AlipayApiException;
import com.pandaer.web.model.dto.pay.PayRequest;
import com.pandaer.web.model.dto.pay.PayResponse;

public interface PayService {


    PayResponse doAlipay(PayRequest payRequest) throws AlipayApiException;
}
