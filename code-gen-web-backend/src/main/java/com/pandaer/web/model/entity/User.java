package com.pandaer.web.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 账号
     */
    @TableField(value = "userAccount")
    private String userAccount;

    /**
     * 密码
     */
    @TableField(value = "userPassword")
    private String userPassword;

    /**
     * 用户邮箱
     */
    @TableField(value = "userEmail")
    private String userEmail;

    /**
     * 用户昵称
     */
    @TableField(value = "userName")
    private String userName;

    /**
     * 用户头像
     */
    @TableField(value = "userAvatar")
    private String userAvatar;

    /**
     * 用户简介
     */
    @TableField(value = "userProfile")
    private String userProfile;

    /**
     * 用户角色：user/admin/ban
     */
    @TableField(value = "userRole")
    private String userRole;

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
     * 经验值
     */
    @TableField(value = "experience")
    private Integer experience;

    /**
     * 用户等级
     */
    @TableField(value = "user_level")
    private Integer userLevel;

    /**
     * 金币余额
     */
    @TableField(value = "gold_coins")
    private BigDecimal goldCoins;

    /**
     * 累计收益
     */
    @TableField(value = "total_income")
    private BigDecimal totalIncome;

    /**
     * 当月剩余提现额度
     */
    @TableField(value = "monthly_quota")
    private BigDecimal monthlyQuota;
}