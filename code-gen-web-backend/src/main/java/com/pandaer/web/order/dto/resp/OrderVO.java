package com.pandaer.web.order.dto.resp;


import com.pandaer.web.generator.dto.resp.GeneratorVO;
import com.pandaer.web.user.dto.resp.UserVO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class OrderVO {


    /**
     * 订单号（唯一主键，格式如：ORDER_20240517123456）
     */
    private String orderId;

    /**
     * 用户ID信息
     */
    private UserVO user;

    /**
     * 代码生成器信息
     */
    private GeneratorVO generator;

    /**
     * 订单金额（单位：元）
     */
    private BigDecimal amount;

    /**
     * 订单状态：0-待支付，1-已支付，2-已取消，3-支付失败
     */
    private Integer orderStatus;

    /**
     * 支付方式（如：支付宝/微信）
     */
    private String paymentMethod;

    /**
     * 第三方支付流水号
     */
    private String paymentNo;

    /**
     * 商户订单号（与order_id可合并，但保留方便对账）
     */
    private String outTradeNo;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 支付时间
     */
    private Date payTime;

    /**
     * 订单过期时间（如30分钟未支付自动关闭）
     */
    private Date expireTime;

    /**
     * 备注信息
     */
    private String remark;
}
