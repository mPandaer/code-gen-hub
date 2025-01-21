package com.pandaer.maker.template;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import com.pandaer.maker.template.enums.FileFilterRange;
import com.pandaer.maker.template.enums.FileFilterRule;
import com.pandaer.maker.template.model.TemplateFileConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件过滤器
 */
public class FileFilter {

    public List<File> filter(String originProjectPath, TemplateFileConfig templateFileConfig) {
        // 构建需要过滤的文件路径（单文件，或者目录）
        String needFilterFileRelativePath = templateFileConfig.getPath();
        String needFilterFilePath = originProjectPath + "/" + needFilterFileRelativePath;
        List<File> files = FileUtil.loopFiles(needFilterFilePath);
        List<File> filteredFiles = new ArrayList<>();
        for (File file : files) {
            List<TemplateFileConfig.FileFilterConfig> fileFilterConfigs = templateFileConfig.getFileFilterConfigs();
            boolean result = doSingleFileFilter(file, fileFilterConfigs);
            if (result) {
                filteredFiles.add(file);
            }
        }

        return filteredFiles;
    }

    // 过滤单个文件
    private boolean doSingleFileFilter(File file, List<TemplateFileConfig.FileFilterConfig> fileFilterConfigs) {
        if (CollUtil.isEmpty(fileFilterConfigs)) {
            return true;
        }

        for (TemplateFileConfig.FileFilterConfig fileFilterConfig : fileFilterConfigs) {
            FileFilterRange fileFilterRange = fileFilterConfig.getFileFilterRange();
            String content = fileFilterRange.getContent(file);
            FileFilterRule fileFilterRule = fileFilterConfig.getFileFilterRule();
            String value = fileFilterConfig.getValue();
            boolean result = fileFilterRule.apply(content, value);
            if (!result) {
                return false;
            }
        }
        return true;

    }


}
