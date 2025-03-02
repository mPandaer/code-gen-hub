package com.pandaer.web.service.impl;

import static com.pandaer.web.constant.UserConstant.USER_LOGIN_STATE;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pandaer.web.common.ErrorCode;
import com.pandaer.web.constant.CommonConstant;
import com.pandaer.web.exception.BusinessException;
import com.pandaer.web.mapper.UserMapper;
import com.pandaer.web.model.dto.user.UserQueryRequest;
import com.pandaer.web.model.entity.User;
import com.pandaer.web.model.enums.UserRoleEnum;
import com.pandaer.web.model.vo.LoginUserVO;
import com.pandaer.web.model.vo.UserVO;
import com.pandaer.web.service.UserService;
import com.pandaer.web.utils.SqlUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

/**
 * 用户服务实现
 *

 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * 盐值，混淆密码
     */
    public static final String SALT = "yupi";

    /**
     * 用户注册函数，用于处理用户注册请求。
     * 该函数在并发条件下确保账户的唯一性，并对用户密码进行加密处理，最后将用户信息持久化到数据库。
     *
     * @param userAccount 用户账户名，用于唯一标识用户
     * @param userPassword 用户密码，用于登录验证
     * @param checkPassword 确认密码，用于验证用户输入的密码是否一致
     * @return 返回注册用户的唯一标识ID
     * @throws BusinessException 如果账户重复或数据库操作失败，抛出业务异常
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 使用synchronized关键字确保在并发条件下，账户的唯一性
        synchronized (userAccount.intern()) {
            // 查询数据库中是否已存在相同的账户名
            Long count = lambdaQuery().eq(User::getUserAccount, userAccount).count();
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
            }

            // 对用户密码进行加密处理，使用MD5算法并添加盐值
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

            // 创建用户对象并设置账户名和加密后的密码
            User user = new User();
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);

            // 将用户信息持久化到数据库
            boolean saveResult = save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }

            // 返回注册用户的唯一标识ID
            return user.getId();
        }
    }




    /**
     * 用户登录认证处理
     *
     * @param userAccount 用户账号
     * @param userPassword 用户明文密码
     * @param request HTTP请求对象，用于维护登录态
     * @return 脱敏后的登录用户视图对象
     * @throws BusinessException 当用户认证失败时抛出，携带错误码 PARAMS_ERROR
     */
    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 执行带盐值的MD5加密处理（盐值拼接在密码前）
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        // 构建组合查询条件：账号匹配 + 加密密码匹配
        LambdaQueryChainWrapper<User> query =
                lambdaQuery().eq(User::getUserAccount, userAccount).eq(User::getUserPassword, encryptPassword);

        User user = query.one();
        // 用户认证失败处理
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        // 在会话中设置用户登录状态
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        return user.mapToLoginUserVO();
    }


    /**
     * 获取当前登录用户信息。
     *
     * 该方法通过从请求的会话中获取用户登录状态，验证用户是否已登录，并从数据库中加载最新的用户信息。
     * 如果用户未登录或用户信息无效，则抛出业务异常。
     *
     * @param request HttpServletRequest对象，用于获取当前会话中的用户登录状态。
     * @return 返回当前登录用户的User对象，包含最新的用户信息。
     * @throws BusinessException 如果用户未登录或用户信息无效，则抛出业务异常，错误码为ErrorCode.NOT_LOGIN_ERROR。
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 从会话中获取用户登录状态，判断用户是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        // 从数据库中查询用户信息（如果追求性能，可以注释此段逻辑，直接使用缓存中的用户信息）
        long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        return currentUser;
    }


    /**
     * 判断当前用户是否为管理员。
     *
     * @param request HTTP请求对象，用于获取当前会话中的用户信息。
     *                通过该对象的会话属性可以访问用户的登录状态。
     * @return 返回一个布尔值，表示当前用户是否为管理员。
     *         如果用户存在且为管理员，则返回true；否则返回false。
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        // 从会话中获取用户登录状态对象
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);

        // 将获取的对象转换为User类型
        User user = (User) userObj;

        // 检查用户对象是否为空，并判断用户是否为管理员
        return user != null && user.isAdmin();
    }



    /**
     * 用户注销函数。
     *
     * 该函数用于处理用户注销操作，移除用户的登录状态。如果用户未登录，则抛出业务异常。
     *
     * @param request HttpServletRequest对象，包含当前会话信息。
     *                通过该参数获取用户的会话并检查登录状态。
     * @return boolean 返回true表示注销成功。
     * @throws BusinessException 如果用户未登录，抛出业务异常，错误码为ErrorCode.OPERATION_ERROR。
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        // 检查用户是否已登录，若未登录则抛出异常
        if (request.getSession().getAttribute(USER_LOGIN_STATE) == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }

        // 移除用户的登录状态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }



    /**
     * 根据用户查询请求对象生成查询条件包装器。
     *
     * @param userQueryRequest 用户查询请求对象，包含查询条件和排序信息。
     *                         如果为 null，则抛出参数错误异常。
     * @return QueryWrapper<User> 查询条件包装器，用于构建数据库查询条件。
     * @throws BusinessException 如果 userQueryRequest 为 null，则抛出业务异常，提示请求参数为空。
     */
    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        // 检查请求参数是否为空，若为空则抛出业务异常
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        // 从请求对象中提取查询条件
        Long id = userQueryRequest.getId();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();

        // 创建查询条件包装器
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();

        // 添加 ID 等值查询条件（仅当 ID 不为空时）
        queryWrapper.eq(id != null, "id", id);

        // 添加用户角色等值查询条件（仅当用户角色不为空时）
        queryWrapper.eq(StringUtils.isNotBlank(userRole), "userRole", userRole);

        // 添加用户简介模糊查询条件（仅当用户简介不为空时）
        queryWrapper.like(StringUtils.isNotBlank(userProfile), "userProfile", userProfile);

        // 添加用户名模糊查询条件（仅当用户名不为空时）
        queryWrapper.like(StringUtils.isNotBlank(userName), "userName", userName);

        // 添加排序条件（仅当排序字段有效时）
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);

        // 返回构建完成的查询条件包装器
        return queryWrapper;
    }
}
