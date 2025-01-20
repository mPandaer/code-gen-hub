package com.pandaer.maker.generator.file;

import cn.hutool.core.io.FileUtil;

import java.io.File;

/**
 * 模板文件生成器
 */
public class TemplateFileGenerator {

    /**
     * TODO 简单实现，直接复制手动制作好的模板文件到指定目录
     * @param templatesDir 模板文件所在的目录
     * @param madeGeneratorDir 制作好的生成器所在的目录
     */
    public void generator(File templatesDir,File madeGeneratorDir) {
        File sourcePath = new File(madeGeneratorDir,".source");
        // 确保目录存在
        FileUtil.mkdir(sourcePath);
        // 复制制作好的模板文件
        FileUtil.copy(templatesDir,sourcePath,true);
    }
}
