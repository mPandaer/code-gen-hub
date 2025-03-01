package com.pandaer.web.model.dto.generator;

import java.io.Serializable;
import java.util.List;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.json.JSONUtil;
import com.pandaer.maker.meta.Meta;
import com.pandaer.web.model.entity.Generator;
import lombok.Data;

/**
 * 编辑请求
 *
 
 */
@Data
public class GeneratorEditRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 基础包
     */
    private String basePackage;

    /**
     * 版本
     */
    private String version;

    /**
     * 作者
     */
    private String author;

    /**
     * 标签列表（json 数组）
     */
    private List<String> tags;

    /**
     * 图片
     */
    private String picture;

    /**
     * 文件配置（json字符串）
     */
    private Meta.FileConfig fileConfig;

    /**
     * 模型配置（json字符串）
     */
    private Meta.ModelConfig modelConfig;

    /**
     * 代码生成器产物路径
     */
    private String distPath;


    public Generator toGenerator() {
        Generator generator = BeanUtil.toBean(this, Generator.class);

        if (CollUtil.isNotEmpty(tags)) {
            // 转换tag
            generator.setTags(JSONUtil.toJsonStr(tags));
        }

        if (ObjUtil.isNotEmpty(fileConfig)) {
            // 转换fileConfig
            generator.setFileConfig(JSONUtil.toJsonStr(fileConfig));
        }

        if (ObjUtil.isNotEmpty(modelConfig)) {
            // 转换modelConfig
            generator.setModelConfig(JSONUtil.toJsonStr(modelConfig));
        }

        return generator;

    }



}