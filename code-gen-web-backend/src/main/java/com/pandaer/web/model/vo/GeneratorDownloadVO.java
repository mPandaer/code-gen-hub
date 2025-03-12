package com.pandaer.web.model.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class GeneratorDownloadVO {
    private Long generatorId;

    private String generatorName;

    private Long downloadCount;
}
