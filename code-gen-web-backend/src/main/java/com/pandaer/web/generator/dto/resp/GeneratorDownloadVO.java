package com.pandaer.web.generator.dto.resp;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class GeneratorDownloadVO {
    private Long generatorId;

    private String generatorName;

    private Long downloadCount;
}
