package com.pandaer.web.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 
 * @TableName download_log
 */
@TableName(value ="download_log")
@Data
public class DownloadLog {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 生成器ID
     */
    @TableField(value = "generator_id")
    private Long generatorId;

    /**
     * 下载用户ID
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 客户端IP（用于地域分析）
     */
    @TableField(value = "client_ip")
    private String clientIp;

    /**
     * 设备类型(PC/Android/iOS)
     */
    @TableField(value = "device_type")
    private String deviceType;

    /**
     * 
     */
    @TableField(value = "create_time")
    private Date createTime;
}