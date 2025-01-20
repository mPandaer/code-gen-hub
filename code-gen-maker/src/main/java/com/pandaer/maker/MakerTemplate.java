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

        String madeGeneratorDir = generateFile(meta);
        System.out.println("01 代码文件生成完毕！");

        // 构建Jar包
        buildJar(madeGeneratorDir);
        System.out.println("02 Jar包构建完毕！");

        // 生成命令行脚本文件
        String jarPath = generateScriptFiles(meta, madeGeneratorDir);
        System.out.println("03 创建命令行脚本文件完毕！");

        // 生成产物包
        generateDistPackage(madeGeneratorDir, meta, jarPath);
        System.out.println("04 生成产物包完毕！");
    }

    protected abstract void generateDistPackage(String madeGeneratorDir, Meta meta, String jarPath);

    protected abstract String generateScriptFiles(Meta meta, String madeGeneratorDir);

    protected abstract void buildJar(String madeGeneratorDir) throws IOException, InterruptedException;

    protected abstract String generateFile(Meta meta);
}
