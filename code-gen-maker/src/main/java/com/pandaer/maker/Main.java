package com.pandaer.maker;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import com.pandaer.maker.generator.JarGenerator;
import com.pandaer.maker.generator.ScriptGenerator;
import com.pandaer.maker.generator.file.FileGenerator;
import com.pandaer.maker.meta.Meta;
import com.pandaer.maker.meta.MetaManager;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        // 获取代码生成器的元信息
        Meta meta = MetaManager.getMetaObject();
        FileGenerator fileGenerator = new FileGenerator(meta);

        // 代码文件的生成
        ClassPathResource classPathResource = new ClassPathResource("templates");
        String absolutePath = classPathResource.getAbsolutePath();
        System.out.println("ClassPathResource 绝对路径：" + absolutePath);
        String templatesDir = absolutePath;

        String madeGeneratorDir = meta.getFileConfig().getGeneratorPath();
        // 确保目录存在
        FileUtil.mkdir(madeGeneratorDir);

        // 代码文件生成
        fileGenerator.generator(FileUtil.file(templatesDir), FileUtil.file(madeGeneratorDir));
        System.out.println("代码文件生成完毕");

        // 构建Jar包
        JarGenerator jarGenerator = new JarGenerator();
        jarGenerator.generator(FileUtil.file(madeGeneratorDir));
        System.out.println("Jar包构建完毕");

        // 生成命令行脚本文件
        ScriptGenerator scriptGenerator = new ScriptGenerator();
        String jarPath = "target/" + String.format("%s-%s-jar-with-dependencies.jar",meta.getName(),meta.getVersion());
        scriptGenerator.generate(meta.getName(),jarPath,FileUtil.file(madeGeneratorDir));

    }
}
