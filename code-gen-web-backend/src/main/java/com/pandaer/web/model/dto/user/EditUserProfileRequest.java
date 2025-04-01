package com.pandaer.web.model.dto.user;

import cn.hutool.core.util.StrUtil;
import com.pandaer.web.validate.Validatable;
import com.pandaer.web.validate.ValidatedResult;
import lombok.Data;

@Data
public class EditUserProfileRequest implements Validatable {


    private Long userId;

    private String userName;

    /**
     * 是一个头像链接
     */
    private String userAvatar;

    private String userProfile;

    @Override
    public ValidatedResult validate() {
        if (userId == null) {
            return ValidatedResult.fail("参数为空");
        }
        return ValidatedResult.success();
    }
}
