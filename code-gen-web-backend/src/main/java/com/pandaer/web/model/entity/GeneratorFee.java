package com.pandaer.web.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * @TableName generator_fee
 */
@TableName(value ="generator_fee")
@Data
public class GeneratorFee {
    /**
     * 代码生成器ID（外键关联generator表）
     */
    @TableId(value = "generator_id")
    private Long generatorId;

    /**
     * 价格（单位：元）
     */
    @TableField(value = "price")
    private BigDecimal price;

    /**
     * 是否免费：0-否，1-是
     */
    @TableField(value = "is_free")
    private Integer isFree;

    /**
     * 有效期（如：永久/30天）
     */
    @TableField(value = "validity")
    private String validity;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;



}