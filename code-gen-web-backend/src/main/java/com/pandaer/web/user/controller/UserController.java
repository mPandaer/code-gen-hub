package com.pandaer.web.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pandaer.web.common.annotation.AuthCheck;
import com.pandaer.web.common.constant.UserConstant;
import com.pandaer.web.common.dto.req.DeleteRequest;
import com.pandaer.web.common.dto.resp.BaseResponse;
import com.pandaer.web.common.enums.ErrorCode;
import com.pandaer.web.common.exception.BusinessException;
import com.pandaer.web.common.exception.ThrowUtils;
import com.pandaer.web.common.utils.ResultUtils;
import com.pandaer.web.common.validate.ValidatedResult;
import com.pandaer.web.user.converter.UserConverter;
import com.pandaer.web.user.dto.req.*;
import com.pandaer.web.user.dto.resp.LoginUserVO;
import com.pandaer.web.user.dto.resp.UserVO;
import com.pandaer.web.user.entity.ResetPasswordRequest;
import com.pandaer.web.user.entity.User;
import com.pandaer.web.user.entity.UserLevelPrivilege;
import com.pandaer.web.user.service.UserLevelPrivilegeService;
import com.pandaer.web.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

import static com.pandaer.web.user.service.impl.UserServiceImpl.SALT;

/**
 * 用户接口
 *
 
 */
@RestController
@RequestMapping("/user")
@Slf4j
@RequiredArgsConstructor
public class UserController {


    private final UserService userService;

    private final UserConverter userConverter;

    private final UserLevelPrivilegeService userLevelPrivilegeService;


    // region 登录相关

    /**
     * 用户注册接口。
     *
     * 该方法用于处理用户注册请求，接收用户提交的注册信息并进行校验，
     * 校验通过后调用服务层完成用户注册逻辑，并返回新用户的唯一标识。
     *
     * @param userRegisterRequest 用户注册请求对象，包含用户账户、密码及确认密码等信息。
     *                            不能为空，且需通过内部校验逻辑。
     * @return BaseResponse<Long> 返回一个包含新用户ID的成功响应对象。
     *                             如果注册失败，则会抛出业务异常。
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        // 检查请求对象是否为空，为空则抛出参数错误异常
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 调用请求对象的校验方法，验证用户输入的合法性
        ValidatedResult validate = userRegisterRequest.validate();
        if (!validate.isSuccess()) {
            // 如果校验失败，抛出参数错误异常，并附带校验失败的具体信息
            throw new BusinessException(ErrorCode.PARAMS_ERROR, validate.getMessage());
        }

        // 调用用户服务层完成注册逻辑，传入用户账户、密码及确认密码
        long newUserId = userService.userRegister(userRegisterRequest.getUserAccount(),
                userRegisterRequest.getUserPassword(), userRegisterRequest.getCheckPassword());

        // 返回包含新用户ID的成功响应
        return ResultUtils.success(newUserId);
    }


    /**
     * 用户登录接口。
     *
     * 该方法用于处理用户登录请求，验证用户输入的账号和密码，并返回登录结果。
     *
     * @param userLoginRequest 包含用户登录信息的请求对象，包括用户账号和密码。
     *                         不能为空，且需要通过内部校验逻辑。
     * @param request          HTTP请求对象，用于获取与当前请求相关的上下文信息。
     * @return BaseResponse<LoginUserVO> 返回一个包含登录用户信息的响应对象。
     *         如果登录成功，返回的响应对象中会包含用户的相关信息；
     *         如果登录失败，则会抛出业务异常。
     */
    @PostMapping("/login")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        // 检查请求参数是否为空，为空则抛出参数错误异常
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 校验请求参数的合法性，如果校验失败则抛出参数错误异常，并附带校验失败的具体信息
        ValidatedResult validatedResult = userLoginRequest.validate();
        if (!validatedResult.isSuccess()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, validatedResult.getMessage());
        }

        // 提取用户账号和密码
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();

        // 调用用户服务进行登录操作，并返回登录结果
        LoginUserVO loginUserVO = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(loginUserVO);
    }



    /**
     * 用户注销接口。
     *
     * 该方法用于处理用户注销请求，接收一个 HttpServletRequest 对象作为参数，
     * 调用 userService 的 userLogout 方法执行注销逻辑，并返回操作结果。
     *
     * @param request HttpServletRequest 对象，包含用户请求的相关信息。
     *                如果为 null，则抛出参数错误的业务异常。
     * @return BaseResponse<Boolean> 返回一个封装了布尔值的响应对象，
     *         表示注销操作是否成功。成功时返回 true，否则返回 false。
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        // 检查请求对象是否为空，为空则抛出参数错误的业务异常
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 调用用户服务层的注销方法，执行具体的注销逻辑
        boolean result = userService.userLogout(request);

        // 将注销结果封装为成功的响应对象并返回
        return ResultUtils.success(result);
    }


    /**
     * 获取当前登录用户信息。
     *
     * 该方法通过解析请求对象中的用户信息，获取当前登录用户的详细数据，并将其转换为视图对象（VO）返回。
     *
     * @param request HttpServletRequest对象，包含当前请求的上下文信息，
     *                用于从请求中提取登录用户的相关信息。
     * @return BaseResponse<LoginUserVO> 返回一个封装了登录用户视图对象（LoginUserVO）的响应对象。
     *         如果用户已登录，返回成功的响应；否则，返回相应的错误信息。
     */
    @GetMapping("/get/login")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
        // 从请求中获取当前登录用户的信息
        User user = userService.getLoginUser(request);
        LoginUserVO loginUserVO = userConverter.entityMapToLoginVO(user);
        // 将用户实体对象映射为登录用户视图对象，并封装为成功响应返回
        return ResultUtils.success(loginUserVO);
    }


    @PutMapping("/password")
    public BaseResponse<?> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest, HttpServletRequest request) {
        // todo
        // 校验参数
        if (changePasswordRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        ValidatedResult validatedResult = changePasswordRequest.validate();
        if (!validatedResult.isSuccess()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, validatedResult.getMessage());
        }


        // 执行修改
        userService.changePassword(changePasswordRequest,request);

        // 返回结果
        return ResultUtils.success(null);
    }






    @GetMapping("/password/reset")
    public BaseResponse<?> findPassword(String email) {
        // 校验邮箱
        if (StringUtils.isBlank(email)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 定义邮箱格式的正则表达式
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        if (!email.matches(emailRegex)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱格式不正确");
        }


        // 发送重置密码的邮箱消息
        userService.sendResetPasswordEmail(email);


        // 返回结果
        return ResultUtils.success(null);

    }





    @PostMapping("/password/reset")
    public BaseResponse<?> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        // 校验请求参数
        if (resetPasswordRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ValidatedResult validatedResult = resetPasswordRequest.validate();

        if (!validatedResult.isSuccess()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, validatedResult.getMessage());
        }

        // 重置密码
        userService.resetPassword(resetPasswordRequest);

        // 返回结果
        return ResultUtils.success(null);
    }


    @PostMapping("profile")
    public BaseResponse<UserVO> editUserProfile(@RequestBody EditUserProfileRequest editUserProfileRequest) {

        // 参数校验
        if (editUserProfileRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        ValidatedResult validatedResult = editUserProfileRequest.validate();
        if (!validatedResult.isSuccess()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, validatedResult.getMessage());
        }

        // 修改用户资料
        UserVO userVO = userService.editUserProfile(editUserProfileRequest);

        // 返回结果
        return ResultUtils.success(userVO);
    }







    // endregion

    // region 增删改查

    /**
     * 创建用户
     *
     * @param userAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest, HttpServletRequest request) {
        if (userAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userAddRequest, user);
        // 默认密码 12345678
        String defaultPassword = "12345678";
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + defaultPassword).getBytes());
        user.setUserPassword(encryptPassword);
        boolean result = userService.save(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(user.getId());
    }

    /**
     * 删除用户
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = userService.removeById(deleteRequest.getId());
        return ResultUtils.success(b);
    }

    /**
     * 更新用户
     *
     * @param userUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest,
            HttpServletRequest request) {
        if (userUpdateRequest == null || userUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest, user);
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取用户（仅管理员）
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<User> getUserById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(user);
    }

    /**
     * 根据 id 获取包装类
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<UserVO> getUserVOById(long id, HttpServletRequest request) {
        BaseResponse<User> response = getUserById(id, request);
        User user = response.getData();
        UserVO userVO = userConverter.entityMapToVO(user);
        return ResultUtils.success(userVO);
    }

    /**
     * 分页获取用户列表（仅管理员）
     *
     * @param userQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<User>> listUserByPage(@RequestBody UserQueryRequest userQueryRequest,
            HttpServletRequest request) {
        long current = userQueryRequest.getCurrent();
        long size = userQueryRequest.getPageSize();
        Page<User> userPage = userService.page(new Page<>(current, size),
                userService.getQueryWrapper(userQueryRequest));
        return ResultUtils.success(userPage);
    }

    /**
     * 分页获取用户封装列表
     *
     * @param userQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest,
            HttpServletRequest request) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = userQueryRequest.getCurrent();
        long size = userQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<User> userPage = userService.page(new Page<>(current, size),
                userService.getQueryWrapper(userQueryRequest));
        Page<UserVO> userVOPage = new Page<>(current, size, userPage.getTotal());
        List<UserVO> userVO = userPage.getRecords().stream().map(userConverter::entityMapToVO).collect(Collectors.toList());
        userVOPage.setRecords(userVO);
        return ResultUtils.success(userVOPage);
    }

    // endregion

    /**
     * 更新个人信息
     *
     * @param userUpdateMyRequest
     * @param request
     * @return
     */
    @PostMapping("/update/my")
    public BaseResponse<Boolean> updateMyUser(@RequestBody UserUpdateMyRequest userUpdateMyRequest,
            HttpServletRequest request) {
        if (userUpdateMyRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        User user = new User();
        BeanUtils.copyProperties(userUpdateMyRequest, user);
        user.setId(loginUser.getId());
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }
}
