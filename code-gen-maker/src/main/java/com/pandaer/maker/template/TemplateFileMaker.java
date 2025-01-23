package com.pandaer.maker.template;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.pandaer.maker.enums.FileTypeEnum;
import com.pandaer.maker.enums.GenerateTypeEnum;
import com.pandaer.maker.meta.Meta;
import com.pandaer.maker.template.model.*;
import com.pandaer.maker.template.utils.TemplateMakerUtil;
import lombok.Data;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 模板文件制作器
 *
 * @author pandaer
 */
@Data
public class TemplateFileMaker {


    /**
     * 文件过滤器
     */
    private FileFilter fileFilter;


    public void make(MakingTemplatesConfig config) {
        String workspaceId = config.getWorkspaceId();
        Meta meta = config.getMeta();
        String originProjectPath = config.getOriginProjectPath();
        MakingTemplateFilesInfo templateMakerFilesInfo = config.getMakingTemplateFilesInfo();
        MakingTemplateModelsConfig templateMakerModelsConfig = config.getMakingTemplateModelsConfig();
        MakingTemplateOutputConfig makingTemplateOutputConfig = config.getMakingTemplateOutputConfig();
        make(workspaceId,meta,originProjectPath,templateMakerFilesInfo,templateMakerModelsConfig,makingTemplateOutputConfig);
    }


    /**
     * 制作代码生成器依赖的动态模板文件
     */
    public void make(String workspaceId, Meta meta, String originProjectPath,
                     MakingTemplateFilesInfo makingTemplateFilesInfo,
                     MakingTemplateModelsConfig makingTemplateModelsConfig,
                     MakingTemplateOutputConfig makingTemplateOutputConfig) {

        // 获取当前工作目录
        String currentDir = System.getProperty("user.dir").replace("\\", "/");

        // 导入到workspaceId对应的工作空间中，如果工作空间已经存在，则不会导入项目
        workspaceId = importOriginProject2Workspace(originProjectPath, workspaceId);

        // 工作空间中的原始工程目录
        String workSpaceDirPath = currentDir + "/" + ".temp/" + workspaceId;
        boolean isExistWorkSpace = FileUtil.exist(workSpaceDirPath);
        boolean isEmptyOriginProjectPath = StrUtil.isBlank(originProjectPath);
        String originProjectInWorkspacePath;
        if (isExistWorkSpace && isEmptyOriginProjectPath) {
            List<File> files = FileUtil.loopFiles(new File(workSpaceDirPath), 1, null);
            File originProjectInWorkspace = files.stream().filter(File::isDirectory).findFirst().orElseThrow(RuntimeException::new);
            originProjectInWorkspacePath = originProjectInWorkspace.getAbsolutePath().replace("\\","/");
        }else {
            originProjectInWorkspacePath = workSpaceDirPath + "/" + FileUtil.getLastPathEle(Paths.get(originProjectPath));
        }


        // 模板文件依赖的模型参数列表

        // 制作多个模板文件
        List<Meta.FileConfig.FileInfo> fileInfoList = makeTemplateFiles(originProjectInWorkspacePath, makingTemplateFilesInfo, makingTemplateModelsConfig);

        // 生成元信息配置
        List<Meta.ModelConfig.ModelInfo> modelInfos = generateModelInfos(makingTemplateModelsConfig);

        // 生成元信息配置文件
        generateMetaFile(originProjectInWorkspacePath, meta, fileInfoList, modelInfos,makingTemplateOutputConfig);

    }

    private List<Meta.ModelConfig.ModelInfo> generateModelInfos(MakingTemplateModelsConfig makingTemplateModelsConfig) {
        List<Meta.ModelConfig.ModelInfo> modelInfos = new ArrayList<>();

        if (makingTemplateModelsConfig == null || makingTemplateModelsConfig.getModels() == null) {
            return modelInfos;
        }
        ModelGroupConfig modelGroupConfig = makingTemplateModelsConfig.getModelGroupConfig();
        // 如果是模型组
        if (modelGroupConfig != null && StrUtil.isNotBlank(modelGroupConfig.getGroupKey())) {
            List<TemplateModelInfo> models = makingTemplateModelsConfig.getModels();
            List<Meta.ModelConfig.ModelInfo> innerModelInfos = models.stream().map(TemplateModelInfo::getModelInfo).collect(Collectors.toList());
//            Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
//            modelInfo.setGroupKey(modelGroupConfig.getGroupKey());
//            modelInfo.setGroupName(modelGroupConfig.getGroupName());
//            modelInfo.setCondition(modelGroupConfig.getCondition());
            Meta.ModelConfig.ModelInfo modelInfo = BeanUtil.toBean(modelGroupConfig, Meta.ModelConfig.ModelInfo.class);
            modelInfo.setModels(innerModelInfos);

            modelInfos.add(modelInfo);

        } else {
            modelInfos.addAll(makingTemplateModelsConfig.getModels().stream().map(TemplateModelInfo::getModelInfo).collect(Collectors.toList()));
        }
        return modelInfos;
    }


    // 同时制作多个模板文件
    private List<Meta.FileConfig.FileInfo> makeTemplateFiles(String originProjectInWorkspacePath,
             MakingTemplateFilesInfo makingTemplateFilesInfo,
            MakingTemplateModelsConfig makingTemplateModelsConfig) {


        // 最终的文件信息列表
        List<Meta.FileConfig.FileInfo> fileInfoList = new ArrayList<>();

        // 原始文件列表
        List<File> needOriginFiles = new ArrayList<>();

        // 处理原始项目路径
        if (makingTemplateFilesInfo == null || makingTemplateFilesInfo.getFiles() == null) {
            return fileInfoList;
        }

        List<TemplateFileConfig> templateFileConfigs = makingTemplateFilesInfo.getFiles();
        Map<String, TemplateFileConfig> pathTemplateFileConfigMapping = templateFileConfigs.stream()
                .collect(Collectors.toMap(TemplateFileConfig::getPath, Function.identity()));

        for (TemplateFileConfig templateFileConfig : templateFileConfigs) {

            List<File> filteredFiles = fileFilter.filter(originProjectInWorkspacePath, templateFileConfig);

            // 添加过滤好的文件列表
            needOriginFiles.addAll(filteredFiles);
        }

        // 过滤掉制作好的动态模板文件
        needOriginFiles = TemplateMakerUtil.filterMadeTemplateFiles(needOriginFiles);


        // 制作模板文件并构建文件配置信息
        // 最终的元信息文件列表配置
        for (File needOriginFile : needOriginFiles) {
            File waitedMakingTemplateFile = new File(needOriginFile.getParentFile(), needOriginFile.getName() + ".ftl");
            String originFileRelativePath = needOriginFile.getAbsolutePath().replace("\\", "/").replace(originProjectInWorkspacePath + "/", "");
            TemplateFileConfig templateFileConfig = pathTemplateFileConfigMapping.get(originFileRelativePath);
            Meta.FileConfig.FileInfo fileInfo = makeSingleTemplateFile(originProjectInWorkspacePath, makingTemplateModelsConfig,
                    templateFileConfig,
                    needOriginFile, waitedMakingTemplateFile);
            fileInfoList.add(fileInfo);
        }

        FileGroupConfig fileGroupConfig = makingTemplateFilesInfo.getFileGroupConfig();
        if (fileGroupConfig != null && StrUtil.isNotBlank(fileGroupConfig.getGroupKey())) {
            Meta.FileConfig.FileInfo fileInfo = new Meta.FileConfig.FileInfo();
            fileInfo.setType(FileTypeEnum.GROUP.value);
            fileInfo.setCondition(fileGroupConfig.getCondition());
            fileInfo.setGroupKey(fileGroupConfig.getGroupKey());
            fileInfo.setGroupName(fileGroupConfig.getGroupName());
            fileInfo.setFiles(fileInfoList);
            // TODO 可能有问题，列表不变性？？？
            return Collections.singletonList(fileInfo);

        }
        return fileInfoList;
    }


    private Meta.FileConfig.FileInfo makeSingleTemplateFile(
            String originProjectInWorkspacePath,
            MakingTemplateModelsConfig makingTemplateModelsConfig,
            TemplateFileConfig templateFileConfig,
            File needOriginFile, File waitedMakingTemplateFile) {
        // 2.制作模板文件

        // 获取原始文件路径
        String needOriginFilePath = needOriginFile.getAbsolutePath();
        // 定义模板文件路径
        String madeTemplateFilePath = waitedMakingTemplateFile.getAbsolutePath();

        // 制作模板
        String originFileContent = FileUtil.readUtf8String(needOriginFilePath);
        String templateFileContent = originFileContent;
        // 如果模板文件存在则读取模板文件的内容
        if (FileUtil.exist(madeTemplateFilePath)) {
            templateFileContent = FileUtil.readUtf8String(madeTemplateFilePath);
        }


        // 根据数据模型制作动态模板的内容
        for (TemplateModelInfo templateModelInfo : makingTemplateModelsConfig.getModels()) {
            Meta.ModelConfig.ModelInfo modelInfo = templateModelInfo.getModelInfo();
            String searchStr = templateModelInfo.getSearchStr();

            ModelGroupConfig modelGroupConfig = makingTemplateModelsConfig.getModelGroupConfig();
            String replacement;
            if (modelGroupConfig != null && StrUtil.isNotBlank(modelGroupConfig.getGroupKey())) {
                replacement = String.format("${%s.%s}", modelGroupConfig.getGroupKey(), modelInfo.getFieldName());
            } else {
                replacement = String.format("${%s}", modelInfo.getFieldName());
            }

            templateFileContent = StrUtil.replace(templateFileContent, searchStr, replacement);
        }


        // 构造FileInfo
        Meta.FileConfig.FileInfo fileInfo = new Meta.FileConfig.FileInfo();

        String needOriginFileRelativePath = needOriginFilePath.replace("\\", "/").replace(originProjectInWorkspacePath + "/", "");
        String madeTemplateFileRelativePath = madeTemplateFilePath.replace("\\", "/").replace(originProjectInWorkspacePath + "/", "");


        fileInfo.setInputPath(madeTemplateFileRelativePath);
        fileInfo.setOutputPath(needOriginFileRelativePath);
        fileInfo.setType(FileTypeEnum.FILE.value);
        // 设置单个文件的条件
        if (templateFileConfig != null && templateFileConfig.getCondition() != null) {
            fileInfo.setCondition(templateFileConfig.getCondition());
        }



        if (originFileContent.equals(templateFileContent)) {
            fileInfo.setInputPath(needOriginFileRelativePath);
            fileInfo.setGenerateType(GenerateTypeEnum.STATIC.value);
        } else {
            // 生成模板文件
            FileUtil.writeString(templateFileContent, madeTemplateFilePath, "UTF-8");
            fileInfo.setGenerateType(GenerateTypeEnum.DYNAMIC.value);
        }

        return fileInfo;

    }


    // TODO 这里的参数meta可以优化 主要是携带了 name 和 desc
    private void generateMetaFile(String originProjectInWorkspacePath, Meta meta,
                                  List<Meta.FileConfig.FileInfo> fileInfos,
                                  List<Meta.ModelConfig.ModelInfo> modelInfos,
                                  MakingTemplateOutputConfig outputConfig) {
        // 3.生成元信息配置文件
        String metaFilePath = new File(originProjectInWorkspacePath).getParent().replace("\\","/") + "/" + "meta.json";

        if (FileUtil.exist(metaFilePath)) {
            meta = JSONUtil.toBean(FileUtil.readUtf8String(metaFilePath), Meta.class);

            // 文件配置信息 -- 文件列表信息更新
            Meta.FileConfig fileConfig = meta.getFileConfig();
            List<Meta.FileConfig.FileInfo> files = fileConfig.getFiles();


            // 添加文件信息
            files.addAll(fileInfos);
            // 去重
            List<Meta.FileConfig.FileInfo> distinctFiles = TemplateMakerUtil.distinctFiles(files);

            // 根据规则再次去重（分组与非分组同名文件去重）
            if (outputConfig.isRemoveSameGroupFileInfo()) {
                List<Meta.FileConfig.FileInfo> oldFiles = meta.getFileConfig().getFiles();
                Set<String> groupInnerFilePathSet = oldFiles.stream().filter(file -> StrUtil.isNotBlank(file.getGroupKey()))
                        .flatMap(group -> group.getFiles().stream())
                        .map(Meta.FileConfig.FileInfo::getInputPath)
                        .collect(Collectors.toSet());

                distinctFiles = distinctFiles.stream().filter(file -> !groupInnerFilePathSet.contains(file.getInputPath())).collect(Collectors.toList());
            }

            fileConfig.setFiles(distinctFiles);


            // 模型配置信息 -- 模型列表信息更新
            Meta.ModelConfig modelConfig = meta.getModelConfig();
            List<Meta.ModelConfig.ModelInfo> models = modelConfig.getModels();
            // 添加模型信息
            models.addAll(modelInfos);
            // 去重
            modelConfig.setModels(TemplateMakerUtil.distinctModels(models));


        } else {
            // 文件配置信息
            Meta.FileConfig fileConfig = new Meta.FileConfig();
            fileConfig.setOriginProjectPath(originProjectInWorkspacePath);

            // 文件列表信息

            // 设置文件列表信息
            fileConfig.setFiles(fileInfos);
            meta.setFileConfig(fileConfig);


            // 模型配置信息
            Meta.ModelConfig modelConfig = new Meta.ModelConfig();
            // 配置模型信息
            modelConfig.setModels(modelInfos);
            meta.setModelConfig(modelConfig);
        }


        // 持久化元信息配置文件
        String metaJsonStr = JSONUtil.toJsonPrettyStr(meta);
        FileUtil.writeUtf8String(metaJsonStr, metaFilePath);
    }


    /**
     * 导入原始工程到工作空间 (工作空间存在则保留)
     *
     * @param originProjectPath 最初的原始工程路径
     * @param workspaceId       工作空间id
     * @return
     */
    private String importOriginProject2Workspace(String originProjectPath, String workspaceId) {
        if (StrUtil.isBlank(workspaceId)) {
            workspaceId = IdUtil.getSnowflakeNextIdStr();
        }

        String currentDir = System.getProperty("user.dir").replace("\\", "/");
        String workspaceDirPath = currentDir + "/" + ".temp/" + workspaceId;
        if (!FileUtil.exist(workspaceDirPath)) {
            FileUtil.mkdir(workspaceDirPath);
            if (StrUtil.isBlank(originProjectPath)) {
                throw new RuntimeException("第一次制作时，必须指定原始工程路径");
            }
            // 复制原始工程到工作空间
            FileUtil.copy(originProjectPath, workspaceDirPath, true);
        }
        return workspaceId;
    }


}
