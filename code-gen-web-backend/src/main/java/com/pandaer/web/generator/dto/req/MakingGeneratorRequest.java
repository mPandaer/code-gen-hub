package com.pandaer.web.generator.dto.req;

import com.pandaer.maker.meta.Meta;
import lombok.Data;

@Data
public class MakingGeneratorRequest {
    Meta meta;
    String zipTemplateFilesUrl;
}
