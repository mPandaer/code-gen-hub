package com.pandaer.web.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 
 * @TableName generator_comment
 */
@TableName(value ="generator_comment")
@Data
public class GeneratorComment {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 评论内容
     */
    @TableField(value = "content")
    private String content;

    /**
     * 评论用户id
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 评论目标id（可以是文章id等）
     */
    @TableField(value = "generator_id")
    private Long generatorId;

    /**
     * 父评论id，用于回复功能
     */
    @TableField(value = "parent_id")
    private Long parentId;

    /**
     * 点赞数
     */
    @TableField(value = "like_count")
    private Integer likeCount;

    /**
     * 状态（0：待审核 1：已发布 2：已删除）
     */
    @TableField(value = "status")
    @TableLogic(delval = "2",value = "1")
    private Integer status;

    /**
     * 
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 
     */
    @TableField(value = "update_time")
    private Date updateTime;
}