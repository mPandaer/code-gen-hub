package com.pandaer.web.generator.dto.req;

import lombok.Data;

import java.util.Map;

@Data
public class UseGeneratorRequest {

    private Long id;

    private Map<String,Object> dataModel;

}
