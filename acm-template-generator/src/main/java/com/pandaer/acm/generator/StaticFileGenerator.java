package com.pandaer.acm.generator;

import cn.hutool.core.io.FileUtil;

import java.io.File;

/**
 * 静态文件生成器
 */
public class StaticFileGenerator {


    /**
     * 静态文件生成逻辑 非常简单直接复制
     * @param originFile 原始文件
     * @param generatedParentDir 目标文件目录
     */
    public static void generate(File originFile, File generatedParentDir) {
        FileUtil.copy(originFile,generatedParentDir,true);
    }
}
