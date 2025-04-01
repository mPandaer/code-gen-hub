package com.pandaer.web.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 用户经验值变更日志
 * @TableName exp_change_log
 */
@TableName(value ="exp_change_log")
@Data
public class ExpChangeLog {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 经验值变动类型
     */
    @TableField(value = "change_type")
    private String changeType;

    /**
     * 变动值
     */
    @TableField(value = "change_exp")
    private Integer changeExp;

    /**
     * 来源ID（生成器/订单等）
     */
    @TableField(value = "source_id")
    private Long sourceId;

    /**
     * 
     */
    @TableField(value = "create_time")
    private Date createTime;
}