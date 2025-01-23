package com.pandaer.maker.template.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.pandaer.maker.meta.Meta;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TemplateMakerUtil {

    /**
     * 模型列表去重
     *
     * @param models 元信息数据模型
     */
    public static List<Meta.ModelConfig.ModelInfo> distinctModels(List<Meta.ModelConfig.ModelInfo> models) {
        // 最终的结果数据模型信息列表
        List<Meta.ModelConfig.ModelInfo> resultModelInfoList = new ArrayList<>();

        // 合并分组
        Map<String, List<Meta.ModelConfig.ModelInfo>> groupKey2ModelInfoMapping = models.stream().filter(modelInfo -> StrUtil.isNotBlank(modelInfo.getGroupKey())).collect(Collectors.groupingBy(Meta.ModelConfig.ModelInfo::getGroupKey));

        for (Map.Entry<String, List<Meta.ModelConfig.ModelInfo>> entry : groupKey2ModelInfoMapping.entrySet()) {
            List<Meta.ModelConfig.ModelInfo> modelInfoList = entry.getValue();
            Collection<Meta.ModelConfig.ModelInfo> distinctModelInfosList = modelInfoList.stream().flatMap(modelInfo -> modelInfo.getModels().stream()).collect(Collectors.toMap(Meta.ModelConfig.ModelInfo::getFieldName, Function.identity(), (k1, k2) -> k2)).values();
            Meta.ModelConfig.ModelInfo lastestModelInfo = CollUtil.getLast(modelInfoList);
            lastestModelInfo.setModels(new ArrayList<>(distinctModelInfosList));
            resultModelInfoList.add(lastestModelInfo);
        }


        // 去重非分组数据模型
        Map<String, Meta.ModelConfig.ModelInfo> inputPath2ModelInfoMapping = models.stream().filter(modelInfo -> StrUtil.isBlank(modelInfo.getGroupKey())).collect(Collectors.toMap(Meta.ModelConfig.ModelInfo::getFieldName, Function.identity(), (k1, k2) -> k2));
        resultModelInfoList.addAll(new ArrayList<>(inputPath2ModelInfoMapping.values()));
        return resultModelInfoList;
    }

    /**
     * 文件列表去重
     *
     * @param files 元信息文件信息
     */
    public static List<Meta.FileConfig.FileInfo> distinctFiles(List<Meta.FileConfig.FileInfo> files) {
        // 最终的结果文件信息列表
        List<Meta.FileConfig.FileInfo> resultFileInfoList = new ArrayList<>();

        // 合并分组
        Map<String, List<Meta.FileConfig.FileInfo>> groupKey2FileInfoMapping = files.stream().filter(fileInfo -> StrUtil.isNotBlank(fileInfo.getGroupKey())).collect(Collectors.groupingBy(Meta.FileConfig.FileInfo::getGroupKey));

        for (Map.Entry<String, List<Meta.FileConfig.FileInfo>> entry : groupKey2FileInfoMapping.entrySet()) {
            List<Meta.FileConfig.FileInfo> fileInfoList = entry.getValue();
            Collection<Meta.FileConfig.FileInfo> distinctFileInfosList = fileInfoList.stream().flatMap(fileInfo -> fileInfo.getFiles().stream()).collect(Collectors.toMap(Meta.FileConfig.FileInfo::getInputPath, Function.identity(), (k1, k2) -> k2)).values();
            Meta.FileConfig.FileInfo lastestFileInfo = CollUtil.getLast(fileInfoList);
            lastestFileInfo.setFiles(new ArrayList<>(distinctFileInfosList));
            resultFileInfoList.add(lastestFileInfo);
        }


        // 去重非分组文件
        Map<String, Meta.FileConfig.FileInfo> inputPath2FileInfoMapping = files.stream().filter(fileInfo -> StrUtil.isBlank(fileInfo.getGroupKey())).collect(Collectors.toMap(Meta.FileConfig.FileInfo::getInputPath, Function.identity(), (k1, k2) -> k2));
        resultFileInfoList.addAll(new ArrayList<>(inputPath2FileInfoMapping.values()));
        return resultFileInfoList;

    }

    /**
     * 过滤已经制作好的动态模板文件
     * @param needOriginFiles
     * @return
     */
    // TODO 假设整个原始工作文件没有后缀为.ftl的文件，否则这里逻辑有问题
    public static List<File> filterMadeTemplateFiles(List<File> needOriginFiles) {
        return needOriginFiles.stream().filter(fileInfo -> !fileInfo.getName().endsWith(".ftl")).collect(Collectors.toList());
    }
}
