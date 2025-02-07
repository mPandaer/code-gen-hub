package com.pandaer.maker.generator;

import cn.hutool.core.io.FileUtil;

import java.io.File;

/**
 * 命令行脚本文件生成器
 */
public class ScriptGenerator {

    public void generate(String scriptName,String jarPath, File madeGeneratorDir) {
        String winCommandTemplate = "@echo off\njava -jar %s %%*";
        String winCommand = String.format(winCommandTemplate,jarPath);
        String unixCommandTemplate = "#! /bin/bash\njava -jar %s \"$@\"";
        String unixCommand = String.format(unixCommandTemplate,jarPath);
        FileUtil.writeString(unixCommand,madeGeneratorDir.getAbsolutePath() + File.separator + "generator", "UTF-8");
        FileUtil.writeString(winCommand,madeGeneratorDir.getAbsolutePath() + File.separator + "generator" + ".bat", "UTF-8");
    }
}
