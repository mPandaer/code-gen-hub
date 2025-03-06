package com.pandaer.web.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Data
@Accessors(chain = true)
public class GeneratorDownloadVO {
    private Long generatorId;

    private String generatorName;

    private Long downloadCount;
}
