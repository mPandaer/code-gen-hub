package com.pandaer.maker;

import com.pandaer.maker.generator.ZipGenerator;
import com.pandaer.maker.meta.Meta;

/**
 * 增加产物包压缩文件
 */
public class ZipGeneratorMaker extends DefaultGeneratorMaker {

    @Override
    protected String generateDistPackage(String madeGeneratorDir, Meta meta, String jarPath) {
        String distPath = super.generateDistPackage(madeGeneratorDir, meta, jarPath);
        new ZipGenerator().generate(distPath);
        return distPath;
    }
}
