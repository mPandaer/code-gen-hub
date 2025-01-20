package com.pandaer.maker.generator.dist;

import lombok.Data;

/**
 * 产物包配置说明
 */
@Data
public class DistPackageConfig {

    /**
     * 产物包路径
     */
    private String distPackagePath;

    /**
     * 生成器需要的模板文件所在目录路径
     */
    private String projectTemplatesDirPath;

    /**
     * 生成器对应的Jar包路径
     */
    private String madeGeneratorJarPath;

    /**
     * 命令行脚本文件路径
     */
    private String scriptFilePath;
}
