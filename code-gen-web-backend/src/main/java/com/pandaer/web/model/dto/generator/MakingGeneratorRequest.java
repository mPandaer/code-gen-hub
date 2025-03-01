package com.pandaer.web.model.dto.generator;

import com.pandaer.maker.meta.Meta;
import lombok.Data;

@Data
public class MakingGeneratorRequest {
    Meta meta;
    String zipTemplateFilesUrl;
}
