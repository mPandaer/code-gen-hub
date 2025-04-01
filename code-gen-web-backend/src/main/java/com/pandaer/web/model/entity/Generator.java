package com.pandaer.web.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 代码生成器
 * @TableName generator
 */
@TableName(value ="generator")
@Data
public class Generator {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 描述
     */
    @TableField(value = "description")
    private String description;

    /**
     * 基础包
     */
    @TableField(value = "basePackage")
    private String basePackage;

    /**
     * 版本
     */
    @TableField(value = "version")
    private String version;

    /**
     * 作者
     */
    @TableField(value = "author")
    private String author;

    /**
     * 标签列表（json 数组）
     */
    @TableField(value = "tags")
    private String tags;

    /**
     * 图片
     */
    @TableField(value = "picture")
    private String picture;

    /**
     * 文件配置（json字符串）
     */
    @TableField(value = "fileConfig")
    private String fileConfig;

    /**
     * 模型配置（json字符串）
     */
    @TableField(value = "modelConfig")
    private String modelConfig;

    /**
     * 代码生成器产物路径
     */
    @TableField(value = "distPath")
    private String distPath;

    /**
     * 状态
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 创建用户 id
     */
    @TableField(value = "userId")
    private Long userId;

    /**
     * 创建时间
     */
    @TableField(value = "createTime")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "updateTime")
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableField(value = "isDelete")
    private Integer isDelete;

    /**
     * 审核状态：0-待审核 1-已通过 2-未通过
     */
    @TableField(value = "audit_status")
    private Integer auditStatus;

    /**
     * 最后审核时间
     */
    @TableField(value = "last_audit_time")
    private Date lastAuditTime;
}