package ${basePackage}.acm.generator;

import ${basePackage}.acm.model.DataModel;
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

    public static void generate(File templateFile,File generatedFile,DataModel dataModel) {

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
