package com.pandaer.maker.generator;

import cn.hutool.core.util.ZipUtil;

import java.io.File;

/**
 * 产物包压缩文件生成器
 */
public class ZipGenerator {

    /**
     * 压缩产物包
     * @param distPath
     */
    public void generate(String distPath) {

        File distFile = new File(distPath);
        String zipPath = distFile.getParent() + File.separator + distFile.getName() + ".zip";
        ZipUtil.zip(distFile.getAbsolutePath(),zipPath);
    }
}
