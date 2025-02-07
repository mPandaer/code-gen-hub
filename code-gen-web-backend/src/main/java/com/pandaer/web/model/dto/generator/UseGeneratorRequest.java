package com.pandaer.web.model.dto.generator;

import lombok.Data;

import java.util.Map;

@Data
public class UseGeneratorRequest {

    private Long id;

    private Map<String,Object> dataModel;

}
