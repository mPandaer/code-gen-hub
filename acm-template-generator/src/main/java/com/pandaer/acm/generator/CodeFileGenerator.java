package com.pandaer.acm.generator;

import cn.hutool.core.io.FileUtil;
import com.pandaer.acm.model.DataModel;
import lombok.AllArgsConstructor;

import java.io.File;
import java.util.List;

/**
 * 代码生成器
 */

@AllArgsConstructor
public class CodeFileGenerator {

    private DataModel dataModel;

    /**
     * 遍历原始项目，获取文件信息，根据文件信息路由到具体的代码生成器
     * @param originProject 原始项目
     * @param generateProject 生成的项目
     */
    public void generator(File originProject,File generateProject) {
        List<File> files = FileUtil.loopFiles(originProject);
        for (File originFile : files) {
            // 基于原始路径，生成目标路径
            String parentDir = originFile.getParent();
            if (parentDir == null) continue;
//            System.out.println("原始项目绝对路径：" + originProject.getAbsolutePath());
            String parentDirRelativePath = parentDir.replace(originProject.getAbsolutePath(),"");

            // TODO 修复一下相对路径的Bug
            if (parentDirRelativePath.startsWith(File.separator)) {
                parentDirRelativePath = parentDirRelativePath.substring(1);
            }

//            System.out.println("相对路径: " + parentDirRelativePath);
            String generatedParentDirPath = generateProject.getAbsolutePath() + File.separator + parentDirRelativePath;
//            System.out.println("生成父目录路径：" + generatedParentDirPath);
            // 保证父目录存在
            FileUtil.mkdir(generatedParentDirPath);

            // 判断文件类型，是静态文件还是动态文件
            // TODO 后期优化，这里直接获取动态模板文件列表，根据文件名进行判断
            List<File> templateFiles = getTemplateFiles();
            boolean isExist = templateFiles.stream().anyMatch(templateFile ->
                    templateFile.getName().equals(originFile.getName() + ".ftl"));
            if (isExist) {
                // 交给动态代码生成器
                DynamicFileGenerator.generate(originFile,FileUtil.file(generatedParentDirPath),dataModel);
            }else {
                // 交给静态代码生成器
                StaticFileGenerator.generate(originFile,FileUtil.file(generatedParentDirPath));
            }
        }

        System.out.println("代码生成完毕！");
    }


    /**
     * TODO 临时的实现
     * 获取动态模板文件列表
     * @return
     */
    private List<File> getTemplateFiles() {
        String currentDir = System.getProperty("user.dir");
        String templateDirPath = currentDir + File.separator + ".source";
        return FileUtil.loopFiles(FileUtil.file(templateDirPath));
    }
}
