package com.pandaer.maker;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import com.pandaer.maker.generator.JarGenerator;
import com.pandaer.maker.generator.ScriptGenerator;
import com.pandaer.maker.generator.dist.DistPackageConfig;
import com.pandaer.maker.generator.dist.DistPackageGenerator;
import com.pandaer.maker.generator.file.FileGenerator;
import com.pandaer.maker.meta.Meta;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * 生成器制作主类
 */
public class DefaultGeneratorMaker extends MakerTemplate {


    @Override
    protected void generateDistPackage(String madeGeneratorDir, Meta meta, String jarPath) {
        DistPackageGenerator distPackageGenerator = new DistPackageGenerator();
        DistPackageConfig distPackageConfig = new DistPackageConfig();
        distPackageConfig.setDistPackagePath(madeGeneratorDir + "-dist");
        String projectTemplatesDirPath = madeGeneratorDir + File.separator + ".source/" + FileUtil.getLastPathEle(Paths.get(meta.getFileConfig().getOriginProjectPath()));
        distPackageConfig.setProjectTemplatesDirPath(projectTemplatesDirPath);
        distPackageConfig.setMadeGeneratorJarPath(madeGeneratorDir + File.separator + jarPath);
        distPackageConfig.setScriptFilePath(madeGeneratorDir + File.separator + meta.getName());
        distPackageGenerator.generator(distPackageConfig);
    }

    @Override
    protected String generateScriptFiles(Meta meta, String madeGeneratorDir) {
        ScriptGenerator scriptGenerator = new ScriptGenerator();
        String jarPath = "target/" + String.format("%s-%s-jar-with-dependencies.jar", meta.getName(), meta.getVersion());
        scriptGenerator.generate(meta.getName(), jarPath, FileUtil.file(madeGeneratorDir));
        return jarPath;
    }

    @Override
    protected void buildJar(String madeGeneratorDir) throws IOException, InterruptedException {
        JarGenerator jarGenerator = new JarGenerator();
        jarGenerator.generator(FileUtil.file(madeGeneratorDir));
    }

    @Override
    protected String generateFile(Meta meta) {
        FileGenerator fileGenerator = new FileGenerator(meta);
        // 代码文件的生成
        ClassPathResource classPathResource = new ClassPathResource("templates");
        String templatesDir = classPathResource.getAbsolutePath();

        String madeGeneratorDir = meta.getFileConfig().getGeneratorPath();
        // 确保目录存在
        FileUtil.mkdir(madeGeneratorDir);

        // 代码文件生成
        fileGenerator.generator(FileUtil.file(templatesDir), FileUtil.file(madeGeneratorDir));
        return madeGeneratorDir;
    }
}
