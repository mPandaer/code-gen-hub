package com.pandaer.maker.generator.file;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.io.resource.ResourceUtil;
import javafx.geometry.Orientation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.StandardCopyOption;

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

    public static void generateByClassPath(String originFile, File generatedFile) {

        ClassLoader classLoader = StaticFileGenerator.class.getClassLoader();
        URL rootUrl = classLoader.getResource("");  // 注意这里是空字符串，不是"/"
        System.out.println("模块A的classpath根路径: " + rootUrl.getPath());

        // 获取具体资源文件
        URL resourceUrl = classLoader.getResource(originFile);
        System.out.println("资源文件路径: " + resourceUrl.getPath());

//        URL resource = ResourceUtil.getResource("/", StaticFileGenerator.class);
//        System.out.println(resource.getPath());
//
//        resource = ResourceUtil.getResource("/" + originFile, StaticFileGenerator.class);
//        System.out.println(resource.getPath());

        InputStream stream = ResourceUtil.getStream(originFile);
        try {
            IoUtil.copy(stream,new FileOutputStream(generatedFile));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
