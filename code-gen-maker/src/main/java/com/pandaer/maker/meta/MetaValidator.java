package com.pandaer.maker.meta;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.pandaer.maker.enums.FileTypeEnum;
import com.pandaer.maker.enums.GenerateTypeEnum;

import java.util.List;

public class MetaValidator {

    public static void validate(Meta meta) {
        // 校验基础信息
        validateBasicInfo(meta);

        // 校验文件配置
        Meta.FileConfig fileConfig = meta.getFileConfig();
        validateFileConfig(fileConfig);


        // 校验模型配置
        Meta.ModelConfig modelConfig = meta.getModelConfig();
        validateModelConfig(modelConfig);

    }

    private static void validateModelConfig(Meta.ModelConfig modelConfig) {
        List<Meta.ModelConfig.ModelInfo> models = modelConfig.getModels();
        for (Meta.ModelConfig.ModelInfo model : models) {
            if (StrUtil.isBlank(model.getFieldName())) {
                throw new ValidatedMetaException("字段名不能为空");
            }
            if (StrUtil.isBlank(model.getType())) {
                throw new ValidatedMetaException("字段类型不能为空");
            }
        }
    }

    private static void validateFileConfig(Meta.FileConfig fileConfig) {
        if (fileConfig == null) {
            throw new ValidatedMetaException("没有任何文件配置");
        }


        if (StrUtil.isBlank(fileConfig.getOriginProjectPath())) {
            throw new ValidatedMetaException("原始项目路径 (originProjectPath) 不能为为空");
        }


        if (StrUtil.isBlank(fileConfig.getGeneratedProjectPath())) {
            fileConfig.setGeneratedProjectPath("generated");
        }

        if (StrUtil.isBlank(fileConfig.getGeneratorPath())) {
            String currentDir = System.getProperty("user.dir").replace("\\","/");
            fileConfig.setGeneratorPath(currentDir + "/madeGenerator");
        }

        if (StrUtil.isBlank(fileConfig.getType())) {
            fileConfig.setType(FileTypeEnum.DIR.value);
        }

        List<Meta.FileConfig.FileInfo> files = fileConfig.getFiles();
        for (Meta.FileConfig.FileInfo file : files) {
            validateFileInfo(file);

        }
    }

    private static void validateFileInfo(Meta.FileConfig.FileInfo file) {
        if (StrUtil.isBlank(file.getInputPath())) {
            throw new ValidatedMetaException("项目文件输入路径不能为空");
        }

        if (StrUtil.isBlank(file.getOutputPath())) {
            if (StrUtil.endWith(file.getOutputPath(),".ftl")) {
                file.setOutputPath(file.getInputPath().replace(".ftl",""));
            }else {
                file.setOutputPath(file.getInputPath());
            }
        }

        if (StrUtil.isBlank(file.getType())) {
            if (FileUtil.getSuffix(file.getInputPath()) != null) {
                file.setType(FileTypeEnum.FILE.value);
            }else {
                file.setType(FileTypeEnum.DIR.value);
            }
        }


        if (StrUtil.isBlank(file.getGenerateType())) {
            if (file.getInputPath().endsWith(".ftl")) {
                file.setType(GenerateTypeEnum.DYNAMIC.value);
            }else {
                file.setType(GenerateTypeEnum.STATIC.value);
            }
        }
    }

    private static void validateBasicInfo(Meta meta) {
        if (StrUtil.isBlank(meta.getName())) {
            meta.setName("simple-generator");
        }

        if (StrUtil.isBlank(meta.getDescription())) {
            meta.setDescription("一个简单的代码生成器");
        }

        if (StrUtil.isBlank(meta.getBasePackage())) {
            throw new ValidatedMetaException("basePackage不能为空!");
        }

        if (StrUtil.isBlank(meta.getVersion())) {
            meta.setVersion("1.0");
        }

        if (StrUtil.isBlank(meta.getAuthor())) {
            meta.setAuthor("simple");
        }

        if (StrUtil.isBlank(meta.getCreateTime())) {
            meta.setCreateTime(DateUtil.now());
        }
    }
}
