package com.pandaer.acm;


import com.pandaer.acm.generator.CodeFileGenerator;
import com.pandaer.acm.model.DataModel;

import java.io.File;

/**
 * 主程序
 */
public class Main {
    public static void main(String[] args) {
        // 生成一个DataModel
        DataModel dataModel = new DataModel();
        dataModel.setAuthor("lwh");
        dataModel.setLoop(true);
        dataModel.setOutputText("haha");

        // 代码生成器
        CodeFileGenerator codeFileGenerator = new CodeFileGenerator(dataModel);
        // 原始项目
        String currentDir = System.getProperty("user.dir");
        String originProjectDirPath = currentDir + File.separator + "origin-project-demo" + File.separator + "acm-template";
        File originProject = new File(originProjectDirPath);
        // 生成的项目
        String generateProjectDirPath = currentDir + File.separator + "generated";
        File generateProject = new File(generateProjectDirPath);
        // 生成代码
        codeFileGenerator.generator(originProject,generateProject);
    }
}
