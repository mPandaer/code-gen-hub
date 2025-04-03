package com.pandaer.web.user.dto.req;

import com.pandaer.web.common.validate.Validatable;
import com.pandaer.web.common.validate.ValidatedResult;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

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
