package com.pandaer.maker;

import com.pandaer.maker.meta.Meta;
import com.pandaer.maker.meta.MetaManager;

import java.io.IOException;

public abstract class MakerTemplate {

    /**
     * 核心制作逻辑
     * @throws IOException
     * @throws InterruptedException
     */
    public void make() throws IOException, InterruptedException {
        // 获取代码生成器的元信息
        Meta meta = MetaManager.getMetaObject();
        System.out.println("元信息：" + meta);
        make(meta);
    }

    public void make(Meta meta) throws IOException, InterruptedException {
        String madeGeneratorDir = generateFile(meta);
        // 构建Jar包
        buildJar(madeGeneratorDir);
        // 生成命令行脚本文件
        String jarPath = generateScriptFiles(meta, madeGeneratorDir);
        // 生成产物包
        generateDistPackage(madeGeneratorDir, meta, jarPath);
    }

    protected abstract String generateDistPackage(String madeGeneratorDir, Meta meta, String jarPath);

    protected abstract String generateScriptFiles(Meta meta, String madeGeneratorDir);

    protected abstract void buildJar(String madeGeneratorDir) throws IOException, InterruptedException;

    protected abstract String generateFile(Meta meta);

}
