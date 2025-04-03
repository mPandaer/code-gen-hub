package com.pandaer.web.user.dto.resp;

import com.pandaer.web.user.entity.UserLevelPrivilege;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户视图（脱敏）
 *
 
 */
@Data
public class UserVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色：user/admin/ban
     */
    private String userRole;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 经验值
     */
    private Integer experience;

    /**
     * 用户等级
     */
    private Integer userLevel;

    /**
     * 金币余额
     */
    private BigDecimal goldCoins;

    /**
     * 累计收益
     */
    private BigDecimal totalIncome;

    /**
     * 当月剩余提现额度
     */
    private BigDecimal monthlyQuota;

    /**
     * 对应的用户等级权益信息
     */
    private UserLevelPrivilege privilege;

    private static final long serialVersionUID = 1L;
}