package com.pandaer.web.model.dto.order;

import cn.hutool.core.util.ObjectUtil;
import com.pandaer.web.validate.Validatable;
import com.pandaer.web.validate.ValidatedResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchOrderParams {
    private String orderId;

    private Long userId;

    private Long generatorId;

    private Integer pageNum;

    private Integer pageSize;
}
