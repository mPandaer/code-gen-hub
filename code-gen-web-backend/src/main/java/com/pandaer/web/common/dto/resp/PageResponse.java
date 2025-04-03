package com.pandaer.web.common.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    private List<T> data;

    private Integer total;


    private Integer pageNum;

    private Integer pageSize;
}
