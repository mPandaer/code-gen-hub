package com.pandaer.maker.template.model;

import lombok.Data;

import java.util.List;

@Data
public class TemplateMakerModelsConfig {
    private List<TemplateModelInfo> models;

    private ModelGroupConfig modelGroupConfig;
}
