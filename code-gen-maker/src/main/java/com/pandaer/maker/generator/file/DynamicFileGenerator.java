package com.pandaer.maker.generator.file;

import com.pandaer.maker.meta.Meta;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.TimeZone;

/**
 * 动态文件生成器
 */
public class DynamicFileGenerator {

    public static void generate(File templateFile,File generatedFile,Meta dataModel) {

        Configuration freemarkerConfig = null;
        try {
            freemarkerConfig = initFreemarkerConfig(templateFile);
            Template template = freemarkerConfig.getTemplate(templateFile.getName());
            // 渲染模板
            template.process(dataModel,new OutputStreamWriter(Files.newOutputStream(generatedFile.toPath()), StandardCharsets.UTF_8));
        } catch (IOException | TemplateException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     *
     * @param templateFileClassPath /templates/java/xxx/xxx/aa.java.ftl
     * @param generatedFile
     * @param dataModel
     */
    public static void generateByClassPath(String templateFileClassPath,File generatedFile,Meta dataModel) {

        int index = templateFileClassPath.lastIndexOf("/");
        String basePackage = templateFileClassPath.substring(0,index);
        String templateFileName = templateFileClassPath.substring(index + 1);

        Configuration cfg = null;
        try {
            cfg = new Configuration(Configuration.VERSION_2_3_34);
            cfg.setTemplateLoader(new ClassTemplateLoader(DynamicFileGenerator.class,basePackage));
            cfg.setDefaultEncoding("UTF-8");
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            cfg.setLogTemplateExceptions(false);
            cfg.setWrapUncheckedExceptions(true);
            cfg.setFallbackOnNullLoopVariable(false);
            cfg.setSQLDateAndTimeTimeZone(TimeZone.getDefault());
            Template template = cfg.getTemplate(templateFileName);
            // 渲染模板
            template.process(dataModel,new OutputStreamWriter(Files.newOutputStream(generatedFile.toPath()), StandardCharsets.UTF_8));
        } catch (IOException | TemplateException e) {
            throw new RuntimeException(e);
        }

    }

    // TODO 改造成单例模式
    private static Configuration initFreemarkerConfig(File templateFile) throws IOException {
        // 获取生成文件路径
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_34);
        cfg.setTemplateLoader(new FileTemplateLoader(templateFile.getParentFile()));
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
        cfg.setWrapUncheckedExceptions(true);
        cfg.setFallbackOnNullLoopVariable(false);
        cfg.setSQLDateAndTimeTimeZone(TimeZone.getDefault());
        return cfg;
    }
}
