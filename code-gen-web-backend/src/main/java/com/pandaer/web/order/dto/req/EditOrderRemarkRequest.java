package com.pandaer.web.order.dto.req;

import cn.hutool.core.util.StrUtil;
import com.pandaer.web.common.validate.Validatable;
import com.pandaer.web.common.validate.ValidatedResult;
import lombok.Data;

@Data
public class EditOrderRemarkRequest implements Validatable {

    private String orderId;


    private String remark;

    @Override
    public ValidatedResult validate() {
        if (StrUtil.hasBlank(orderId,remark)) {
            return ValidatedResult.fail("参数为空");
        }
        return ValidatedResult.success();
    }
}
