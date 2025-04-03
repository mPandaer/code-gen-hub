package com.pandaer.web.generator.dto.resp;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class GeneratorFeeVO {

    private BigDecimal price;

    private Integer isFree;

    private String validity;

    private Date createTime;
}
