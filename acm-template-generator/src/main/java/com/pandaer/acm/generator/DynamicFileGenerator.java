package com.pandaer.acm.generator;

import com.pandaer.acm.model.DataModel;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.TimeZone;

/**
 * 动态文件生成器
 */
public class DynamicFileGenerator {

    public static void generate(File originFile,File generatedParentDir,DataModel dataModel) {

        Configuration freemarkerConfig = null;
        try {
            freemarkerConfig = getFreemarkerConfig();
            Template template = freemarkerConfig.getTemplate(originFile.getName() + ".ftl");
            // 创建一个新的文件
            File newFile = new File(generatedParentDir,originFile.getName());
            template.process(dataModel,new OutputStreamWriter(Files.newOutputStream(newFile.toPath()), StandardCharsets.UTF_8));
        } catch (IOException | TemplateException e) {
            throw new RuntimeException(e);
        }


    }

    private static Configuration getFreemarkerConfig() throws IOException {
        // 获取当前目录
        String currentDir = System.getProperty("user.dir");
        // 获取模板文件路径
        String templateFileDir = currentDir + File.separator + ".source";
        // 获取生成文件路径
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_34);
        cfg.setDirectoryForTemplateLoading(new File(templateFileDir));
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
        cfg.setWrapUncheckedExceptions(true);
        cfg.setFallbackOnNullLoopVariable(false);
        cfg.setSQLDateAndTimeTimeZone(TimeZone.getDefault());
        return cfg;
    }
}
