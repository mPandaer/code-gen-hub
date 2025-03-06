package com.pandaer.web.model.dto.generator.comment;

import com.pandaer.web.validate.Validatable;
import com.pandaer.web.validate.ValidatedResult;
import lombok.Data;

@Data
public class PageListCommentsRequest implements Validatable {

    private Long generatorId;

    private int pageNum;

    private int pageSize = 10;

    @Override
    public ValidatedResult validate() {
        if (pageNum < 1) {
            return ValidatedResult.fail("页码错误");
        }
        if (generatorId == null) {
            return ValidatedResult.fail("参数为空");
        }
        return ValidatedResult.success();
    }
}
