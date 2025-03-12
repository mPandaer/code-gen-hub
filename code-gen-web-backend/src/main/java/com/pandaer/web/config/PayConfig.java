package com.pandaer.web.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@EnableConfigurationProperties(PayConfig.AlipayConfigProperties.class)
@Data
@Configuration
public class PayConfig {


    @Autowired
    private AlipayConfigProperties alipayConfigProperties;




    @Data
    @ConfigurationProperties(prefix = "pay.alipay")
    public static class AlipayConfigProperties {

        // 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
        private String appId;

        // 商户私钥，您的PKCS8格式RSA2私钥
        private String merchantPrivateKey;

        // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
        private String alipayPublicKey;

        // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
        private String notifyUrl;

        // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
        private String returnUrl;

        // 签名方式
        private String signType;

        // 字符编码格式
        private String charset;

        // 支付宝网关
        private String gatewayUrl;

    }



    @Bean
    public AlipayConfig alipayConfig() {
        AlipayConfig alipayConfig = new AlipayConfig();
        alipayConfig.setServerUrl(alipayConfigProperties.getGatewayUrl());
        alipayConfig.setAppId(alipayConfigProperties.getAppId());
        alipayConfig.setPrivateKey(alipayConfigProperties.getMerchantPrivateKey());
        alipayConfig.setFormat("json");
        alipayConfig.setAlipayPublicKey(alipayConfigProperties.getAlipayPublicKey());
        alipayConfig.setCharset("UTF-8");
        alipayConfig.setSignType("RSA2");
        return alipayConfig;
    }


    @Bean
    public AlipayClient alipayClient() throws AlipayApiException {
        return new DefaultAlipayClient(alipayConfig());
    }

}
