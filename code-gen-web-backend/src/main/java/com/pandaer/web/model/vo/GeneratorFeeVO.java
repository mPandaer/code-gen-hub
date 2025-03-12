package com.pandaer.web.model.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
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
