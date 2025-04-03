package com.pandaer.web.user.dto.req;

import com.pandaer.web.common.validate.Validatable;
import com.pandaer.web.common.validate.ValidatedResult;
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
