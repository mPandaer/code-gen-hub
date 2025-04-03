package com.pandaer.web.order.dto.req;

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
