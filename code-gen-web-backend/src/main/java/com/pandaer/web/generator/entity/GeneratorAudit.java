package com.pandaer.web.generator.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 生成器审核记录表
 * @TableName generator_audit
 */
@TableName(value ="generator_audit")
@Data
public class GeneratorAudit {
    /**
     * 
     */
    @TableId(value = "audit_id", type = IdType.AUTO)
    private Long auditId;

    /**
     * 
     */
    @TableField(value = "generator_id")
    private Long generatorId;

    /**
     * 审核员ID
     */
    @TableField(value = "auditor_id")
    private Long auditorId;

    /**
     * 原始状态
     */
    @TableField(value = "original_status")
    private Integer originalStatus;

    /**
     * 审核后状态
     */
    @TableField(value = "new_status")
    private Integer newStatus;

    /**
     * 审核意见
     */
    @TableField(value = "audit_comment")
    private String auditComment;

    /**
     * 
     */
    @TableField(value = "audit_time")
    private Date auditTime;

    /**
     * 补充材料路径
     */
    @TableField(value = "attachment")
    private String attachment;
}