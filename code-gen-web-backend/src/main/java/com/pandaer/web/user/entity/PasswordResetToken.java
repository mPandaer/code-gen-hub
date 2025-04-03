package com.pandaer.web.user.entity;


import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.crypto.digest.HMac;
import cn.hutool.crypto.digest.HmacAlgorithm;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * 密码重置令牌类，用于用户忘记密码功能
 * 该类映射到数据库中的password_reset_token表
 */
@TableName(value ="password_reset_token")
@Data
public class PasswordResetToken implements Serializable {

    /**
     * 主键ID，自动增长
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联的用户信息
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 重置密码的令牌字符串
     */
    @TableField("token")
    private String token;

    /**
     * 令牌创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 令牌过期时间
     */
    @TableField("expire_time")
    private LocalDateTime expireTime;

    /**
     * 令牌是否已使用
     */
    @TableField("used")
    private Integer used;

    public static PasswordResetToken create(User resetUser) {
        String key = "reset.user.password";
        HMac hmac = DigestUtil.hmac(HmacAlgorithm.HmacSHA256, key.getBytes());
        String raw = resetUser.getId() + "_" + resetUser.getUserName() + "_" + resetUser.getUserEmail() + "_" + System.currentTimeMillis();
        String token = hmac.digestHex(raw);

        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setUserId(resetUser.getId());
        passwordResetToken.setToken(token);
        passwordResetToken.setCreateTime(LocalDateTime.now());
        passwordResetToken.setExpireTime(LocalDateTime.now().plusMinutes(15));

        return passwordResetToken;

    }
}
