package com.pandaer.web.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 
 * @TableName user_generator
 */
@TableName(value ="user_generator")
@Data
public class UserGenerator {
    /**
     * 用户ID
     */
    @TableId(value = "user_id")
    private Long userId;

    /**
     * 代码生成器ID
     */
    @TableField(value = "generator_id")
    private Long generatorId;

    /**
     * 购买时间
     */
    @TableField(value = "purchase_time")
    private Date purchaseTime;

    /**
     * 权限过期时间（如永久则为NULL）
     */
    @TableField(value = "expire_time")
    private Date expireTime;

    /**
     * 状态：1-有效，2-已过期，3-已退款
     */
    @TableField(value = "status")
    private Integer status;
}