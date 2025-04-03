package com.pandaer.web.user.dto.req;

import cn.hutool.core.util.StrUtil;
import com.pandaer.web.common.validate.Validatable;
import com.pandaer.web.common.validate.ValidatedResult;
import lombok.Data;

@Data
public class ChangePasswordRequest implements Validatable {

    private String oldPassword;

    private String newPassword;


    @Override
    public ValidatedResult validate() {
        if (StrUtil.hasBlank(oldPassword, newPassword)) {
            return ValidatedResult.fail("参数为空");
        }
        if (oldPassword.length() < 8 || newPassword.length() < 8) {
            return ValidatedResult.fail("密码长度错误");
        }

        return ValidatedResult.success();

    }
}
