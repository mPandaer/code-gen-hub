package com.pandaer.web.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 等级权益配置表
 * @TableName user_level_privilege
 */
@TableName(value ="user_level_privilege")
@Data
public class UserLevelPrivilege {
    /**
     * 等级
     */
    @TableId(value = "level")
    private Integer level;

    /**
     * 等级名称
     */
    @TableField(value = "level_name")
    private String levelName;

    /**
     * 所需最低经验值
     */
    @TableField(value = "min_exp")
    private Integer minExp;

    /**
     * 最大经验值
     */
    @TableField(value = "max_exp")
    private Integer maxExp;

    /**
     * 提现手续费率
     */
    @TableField(value = "withdraw_fee_rate")
    private BigDecimal withdrawFeeRate;

    /**
     * 月提现额度
     */
    @TableField(value = "monthly_quota")
    private BigDecimal monthlyQuota;

    /**
     * 特权说明
     */
    @TableField(value = "privileges")
    private String privileges;
}