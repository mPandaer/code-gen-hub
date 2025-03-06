package com.pandaer.web.model.entity;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import com.pandaer.web.model.enums.UserRoleEnum;
import com.pandaer.web.model.vo.LoginUserVO;
import com.pandaer.web.model.vo.UserVO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

/**
 * 用户
 *
 
 */
@TableName(value = "user")
@Data
public class User implements Serializable {

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 用户密码
     */
    private String userPassword;


    /**
     * 用户邮箱
     */
    private String userEmail;



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
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


    public UserVO mapToUserVO() {
        return BeanUtil.toBean(this, UserVO.class);
    }

    public LoginUserVO mapToLoginUserVO() {
        return BeanUtil.toBean(this, LoginUserVO.class);
    }

    public boolean isAdmin() {
        return UserRoleEnum.ADMIN.getValue().equals(userRole);
    }
}