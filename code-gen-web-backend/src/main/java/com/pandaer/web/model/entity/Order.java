package com.pandaer.web.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName order
 */
@TableName(value ="order")
@Data
public class Order {
    /**
     * 订单号（唯一主键，格式如：ORDER_20240517123456）
     */
    @TableId(value = "order_id")
    private String orderId;

    /**
     * 用户ID
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 代码生成器ID
     */
    @TableField(value = "generator_id")
    private Long generatorId;

    /**
     * 订单金额（单位：元）
     */
    @TableField(value = "amount")
    private BigDecimal amount;

    /**
     * 订单状态：0-待支付，1-已支付，2-已取消，3-支付失败
     */
    @TableField(value = "order_status")
    private Integer orderStatus;

    /**
     * 支付方式（如：支付宝/微信）
     */
    @TableField(value = "payment_method")
    private String paymentMethod;

    /**
     * 第三方支付流水号
     */
    @TableField(value = "payment_no")
    private String paymentNo;

    /**
     * 商户订单号（与order_id可合并，但保留方便对账）
     */
    @TableField(value = "out_trade_no")
    private String outTradeNo;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 支付时间
     */
    @TableField(value = "pay_time")
    private Date payTime;

    /**
     * 订单过期时间（如30分钟未支付自动关闭）
     */
    @TableField(value = "expire_time")
    private Date expireTime;

    /**
     * 备注信息
     */
    @TableField(value = "remark")
    private String remark;
}