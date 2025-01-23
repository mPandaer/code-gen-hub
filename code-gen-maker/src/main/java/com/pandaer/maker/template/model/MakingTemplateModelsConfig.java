package com.pandaer.maker.template.model;

import lombok.Data;

import java.util.List;

@Data
public class MakingTemplateModelsConfig {
    private List<TemplateModelInfo> models;

    private ModelGroupConfig modelGroupConfig;
}
