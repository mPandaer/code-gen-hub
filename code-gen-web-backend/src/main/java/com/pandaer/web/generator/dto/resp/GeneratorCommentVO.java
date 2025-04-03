package com.pandaer.web.generator.dto.resp;


import com.pandaer.web.user.dto.resp.UserVO;
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
