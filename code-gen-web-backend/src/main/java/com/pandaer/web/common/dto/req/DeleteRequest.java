package com.pandaer.web.common.dto.req;

import lombok.Data;

import java.io.Serializable;

/**
 * 删除请求
 *
 
 */
@Data
public class DeleteRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}