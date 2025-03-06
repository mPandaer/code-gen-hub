package com.pandaer.web.model.vo;


import lombok.Data;

import java.util.Date;


@Data
public class GeneratorCommentVO {

    private Long id;

    private String content;


    private Long generatorId;


    private Long parentId;


    private Integer likeCount;


    private Integer status;


    private Date createTime;


    private Date updateTime;

    private UserVO userVO;
}
