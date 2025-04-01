package com.pandaer.web.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户提现记录表
 * @TableName withdraw_record
 */
@TableName(value ="withdraw_record")
@Data
public class WithdrawRecord {
    /**
     * 提现单号WD202406010001
     */
    @TableId(value = "withdraw_id")
    private String withdrawId;

    /**
     * 
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 申请金额
     */
    @TableField(value = "amount")
    private BigDecimal amount;

    /**
     * 手续费
     */
    @TableField(value = "service_fee")
    private BigDecimal serviceFee;

    /**
     * 实际到账
     */
    @TableField(value = "actual_amount")
    private BigDecimal actualAmount;

    /**
     * 0-待审核 1-已打款 2-已驳回
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 支付平台流水号
     */
    @TableField(value = "payment_no")
    private String paymentNo;

    /**
     * 审核意见
     */
    @TableField(value = "audit_comment")
    private String auditComment;

    /**
     * 
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 
     */
    @TableField(value = "complete_time")
    private Date completeTime;
}