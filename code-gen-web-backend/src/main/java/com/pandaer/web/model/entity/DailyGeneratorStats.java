package com.pandaer.web.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * @TableName daily_generator_stats
 */
@TableName(value ="daily_generator_stats")
@Data
public class DailyGeneratorStats {
    /**
     * 统计日期
     */
    @TableId(value = "stat_date")
    private Date statDate;

    /**
     * 新增生成器数
     */
    @TableField(value = "new_generator_count")
    private Integer newGeneratorCount;

    /**
     * 总下载量
     */
    @TableField(value = "total_download")
    private Integer totalDownload;

    /**
     * 活跃开发者数
     */
    @TableField(value = "active_developer_count")
    private Integer activeDeveloperCount;

    /**
     * 平均评分
     */
    @TableField(value = "avg_rating")
    private BigDecimal avgRating;

    /**
     * 付费转化率
     */
    @TableField(value = "pay_conversion_rate")
    private BigDecimal payConversionRate;
}