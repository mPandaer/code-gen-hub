package com.pandaer.maker.generator;

import cn.hutool.core.io.IoUtil;
import sun.nio.ch.IOUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Jar包生成器
 */
public class JarGenerator {
    /**
     * 构建Jar包
     * @param generatedProject 基于原始项目生成的项目
     */
    public void generator(File generatedProject) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.directory(generatedProject);
        // TODO 后期优化 适配不同的操作系统
        processBuilder.command("mvn.cmd clean package".split(" "));
        Process process = processBuilder.start();
        InputStream inputStream = process.getInputStream();
        String info = IoUtil.read(inputStream, "GBK");
        System.out.println(info);
        process.waitFor();

    }
}
