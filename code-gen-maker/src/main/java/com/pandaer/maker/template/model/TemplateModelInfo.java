package com.pandaer.maker.template.model;

import com.pandaer.maker.meta.Meta;
import lombok.Data;

/**
 * 模板文件的模型参数
 */

@Data
public class TemplateModelInfo {
    /**
     * 模型信息（真实的模型信息）
     */
    private Meta.ModelConfig.ModelInfo modelInfo;

    /**
     * 搜索字符串
     */
    private String searchStr;
}
