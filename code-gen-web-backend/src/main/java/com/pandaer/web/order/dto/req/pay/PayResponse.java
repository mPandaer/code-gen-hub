package com.pandaer.web.order.dto.req.pay;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PayResponse {
    private String htmlPage;
}
