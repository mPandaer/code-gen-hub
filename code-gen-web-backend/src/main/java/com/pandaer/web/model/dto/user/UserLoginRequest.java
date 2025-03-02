package com.pandaer.web.model.dto.user;

import java.io.Serializable;

import com.pandaer.web.common.ErrorCode;
import com.pandaer.web.exception.BusinessException;
import com.pandaer.web.validate.Validatable;
import com.pandaer.web.validate.ValidatedResult;
import lombok.Data;
import lombok.Value;
import org.apache.commons.lang3.StringUtils;

/**
 * 用户登录请求
 *
 
 */
@Data
public class UserLoginRequest implements Serializable, Validatable {

    private static final long serialVersionUID = 3191241716373120793L;

    private String userAccount;

    private String userPassword;

    @Override
    public ValidatedResult validate() {
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return ValidatedResult.fail("参数为空");
        }
        if (userAccount.length() < 4) {
            return ValidatedResult.fail("用户账号错误");
        }
        if (userPassword.length() < 8) {
            return ValidatedResult.fail("用户密码错误");
        }
        return ValidatedResult.success();
    }
}
