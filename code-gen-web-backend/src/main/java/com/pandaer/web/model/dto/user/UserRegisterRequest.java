package com.pandaer.web.model.dto.user;

import com.pandaer.web.common.ErrorCode;
import com.pandaer.web.exception.BusinessException;
import com.pandaer.web.validate.Validatable;
import com.pandaer.web.validate.ValidatedResult;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * 用户注册请求体
 *
 
 */
@Data
public class UserRegisterRequest implements Serializable, Validatable {

    private static final long serialVersionUID = 3191241716373120793L;

    private String userAccount;

    private String userPassword;

    private String checkPassword;

    @Override
    public ValidatedResult validate() {
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            return ValidatedResult.fail("参数为空");
        }
        if (userAccount.length() < 4) {
            return ValidatedResult.fail("账户长度不够4位");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            return ValidatedResult.fail("密码长度不够8位");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        return ValidatedResult.success();
    }
}
