package com.pandaer.web.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户收益明细表
 * @TableName user_income_detail
 */
@TableName(value ="user_income_detail")
@Data
public class UserIncomeDetail {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联用户
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 关联订单
     */
    @TableField(value = "order_id")
    private String orderId;

    /**
     * 收益类型（销售分成/打赏/奖励）
     */
    @TableField(value = "income_type")
    private String incomeType;

    /**
     * 收益金额
     */
    @TableField(value = "amount")
    private BigDecimal amount;

    /**
     * 平台抽成比例
     */
    @TableField(value = "commission_rate")
    private BigDecimal commissionRate;

    /**
     * 
     */
    @TableField(value = "create_time")
    private Date createTime;
}