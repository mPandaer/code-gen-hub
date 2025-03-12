package com.pandaer.web.model.dto.pay;

import cn.hutool.core.util.StrUtil;
import com.pandaer.web.validate.Validatable;
import com.pandaer.web.validate.ValidatedResult;
import lombok.Data;

@Data
public class PayRequest implements Validatable {
    private String orderId;
    private String subject;

    @Override
    public ValidatedResult validate() {
        if (StrUtil.hasBlank(orderId, subject)) {
            return ValidatedResult.fail("参数为空");
        }

        return ValidatedResult.success();
    }
}
