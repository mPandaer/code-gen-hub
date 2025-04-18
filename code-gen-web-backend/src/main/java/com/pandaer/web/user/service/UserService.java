package com.pandaer.web.user.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pandaer.web.user.dto.req.ChangePasswordRequest;
import com.pandaer.web.user.dto.req.EditUserProfileRequest;
import com.pandaer.web.user.dto.req.UserQueryRequest;
import com.pandaer.web.user.dto.resp.LoginUserVO;
import com.pandaer.web.user.dto.resp.UserVO;
import com.pandaer.web.user.entity.ResetPasswordRequest;
import com.pandaer.web.user.entity.User;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户服务
 *
 
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);


    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);


    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);


    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);



    /**
     * 获取查询条件
     *
     * @param userQueryRequest
     * @return
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    void changePassword(ChangePasswordRequest changePasswordRequest, HttpServletRequest request);

    void sendResetPasswordEmail(String email);

    void resetPassword(ResetPasswordRequest resetPasswordRequest);

    UserVO editUserProfile(EditUserProfileRequest editUserProfileRequest);
}
