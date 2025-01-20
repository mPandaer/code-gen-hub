package com.pandaer.maker.generator.dist;

import cn.hutool.core.io.FileUtil;

import java.io.File;

/**
 * 产物包生成器
 */
public class DistPackageGenerator {


    /**
     * 基于配置快速生成产物包
     * @param config
     */
    public void generator(DistPackageConfig config) {
        String distPackagePath = config.getDistPackagePath();
        // 确保产物包路径存在
        FileUtil.mkdir(distPackagePath);

        // 复制依赖的模板文件
        String distProjectTemplateDirParentPath = distPackagePath + File.separator + ".source";
        FileUtil.mkdir(distProjectTemplateDirParentPath);
        FileUtil.copy(config.getProjectTemplatesDirPath(),distProjectTemplateDirParentPath,true);

        // 复制生成器Jar包文件
        String distJarFileParentDirPath = distPackagePath + File.separator + "target";
        // 确保目录存在
        FileUtil.mkdir(distJarFileParentDirPath);
        FileUtil.copy(config.getMadeGeneratorJarPath(),distJarFileParentDirPath,true);

        // 复制命令行脚本文件
        FileUtil.copy(config.getScriptFilePath(),distPackagePath,true);
        FileUtil.copy(config.getScriptFilePath() + ".bat",distPackagePath,true);
    }
}
