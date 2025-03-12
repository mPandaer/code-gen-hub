package com.pandaer.web.model.entity;


import cn.hutool.core.util.StrUtil;
import com.pandaer.web.validate.Validatable;
import com.pandaer.web.validate.ValidatedResult;
import lombok.Data;

@Data
public class ResetPasswordRequest implements Validatable {

    private String token;

    private String email;

    private String newPassword;


    @Override
    public ValidatedResult validate() {
        if (StrUtil.hasBlank(token, newPassword)) {
            return ValidatedResult.fail("参数为空");
        }

        if (newPassword.length() < 8) {
            return ValidatedResult.fail("密码长度错误");
        }

        return ValidatedResult.success();
    }
}
