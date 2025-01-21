package com.pandaer.maker.template;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.pandaer.maker.enums.FileTypeEnum;
import com.pandaer.maker.enums.GenerateTypeEnum;
import com.pandaer.maker.meta.Meta;
import com.pandaer.maker.template.enums.FileFilterRange;
import com.pandaer.maker.template.enums.FileFilterRule;
import com.pandaer.maker.template.model.*;
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


    private FileFilter fileFilter;

    // 测试
    public static void main(String[] args) {

        Meta meta = new Meta();
        // 一些用于测试的预定义信息
        String name = "acm-template-generator";
        String description = "ACM代码生成器模板";
        meta.setName(name);
        meta.setDescription(description);

        // 当前工作目录
        String currentDir = System.getProperty("user.dir").replace("\\", "/");
        // 原始工程路径
        String originProjectPath = currentDir + "/" + "origin-project-demo/springboot-init";

        // 定义模板文件依赖的模型参数
        Meta.ModelConfig.ModelInfo modelInfo1 = new Meta.ModelConfig.ModelInfo();
        modelInfo1.setFieldName("url");
        modelInfo1.setType("String");
        modelInfo1.setDescription("数据库URL");
        TemplateModelInfo templateModelInfo1 = new TemplateModelInfo();
        templateModelInfo1.setModelInfo(modelInfo1);
        templateModelInfo1.setSearchStr("jdbc:mysql://localhost:3306/my_db");

        Meta.ModelConfig.ModelInfo modelInfo2 = new Meta.ModelConfig.ModelInfo();
        modelInfo2.setFieldName("username");
        modelInfo2.setType("String");
        modelInfo2.setDescription("用户名");
        TemplateModelInfo templateModelInfo2 = new TemplateModelInfo();
        templateModelInfo2.setModelInfo(modelInfo2);
        templateModelInfo2.setSearchStr("root");

        Meta.ModelConfig.ModelInfo modelInfo3 = new Meta.ModelConfig.ModelInfo();
        modelInfo3.setFieldName("password");
        modelInfo3.setType("String");
        modelInfo3.setDescription("密码");
        TemplateModelInfo templateModelInfo3 = new TemplateModelInfo();
        templateModelInfo3.setModelInfo(modelInfo3);
        templateModelInfo3.setSearchStr("123456");


        // 添加一个数据模型配置
        TemplateMakerModelsConfig templateMakerModelsConfig = new TemplateMakerModelsConfig();
        List<TemplateModelInfo> templateModelInfos = new ArrayList<>();
        templateModelInfos.add(templateModelInfo1);
        templateModelInfos.add(templateModelInfo2);
        templateModelInfos.add(templateModelInfo3);
        templateMakerModelsConfig.setModels(templateModelInfos);
        // 分组信息
        // TODO 存在BUG 简单的字符串替换，会替换我们不想替换的内容
        ModelGroupConfig modelGroupConfig = new ModelGroupConfig();
        modelGroupConfig.setGroupKey("mysql");
        modelGroupConfig.setGroupName("数据库配置");
        templateMakerModelsConfig.setModelGroupConfig(modelGroupConfig);



//        List<String> needOriginFileRelativePaths = Arrays.asList("src/main/java/com/yupi/springbootinit/common", "src/main/java/com/yupi/springbootinit/controller");
        // 构建一个TemplateFilesInfo


        // 文件过滤配置 1
//        TemplateFileConfig templateFileConfig = new TemplateFileConfig();
//        templateFileConfig.setPath("src/main/java/com/yupi/springbootinit/common");
//        TemplateFileConfig.FileFilterConfig fileFilterConfig = new TemplateFileConfig.FileFilterConfig();
//        fileFilterConfig.setFileFilterRange(FileFilterRange.FILE_NAME);
//        fileFilterConfig.setFileFilterRule(FileFilterRule.CONTAINS);
//        fileFilterConfig.setValue("Base");
//        templateFileConfig.setFileFilterConfigs(Collections.singletonList(fileFilterConfig));

        // 文件过滤配置 2
        TemplateFileConfig templateFileConfig2 = new TemplateFileConfig();
        templateFileConfig2.setPath("src/main/resources/application.yml");


        TemplateMakerFilesInfo templateMakerFilesInfo = new TemplateMakerFilesInfo();
        templateMakerFilesInfo.setFiles(Arrays.asList(templateFileConfig2));
        FileGroupConfig fileGroupConfig = new FileGroupConfig();
        fileGroupConfig.setGroupKey("test2");
        fileGroupConfig.setGroupName("测试分组");
        fileGroupConfig.setCondition("test-condition");
        templateMakerFilesInfo.setFileGroupConfig(fileGroupConfig);


        // 引入工作空间
//        String workspaceId = "1881697187304202240";
        String workspaceId = null;

        // 制作代码生成器依赖的模板文件
        TemplateFileMaker templateFileMaker = new TemplateFileMaker();
        FileFilter simpleFileFilter = new FileFilter();
        templateFileMaker.setFileFilter(simpleFileFilter);
        templateFileMaker.make(workspaceId, meta, originProjectPath, templateMakerFilesInfo, templateMakerModelsConfig);
    }









    /**
     * 制作模板文件
     */
    public void make(String workspaceId, Meta meta, String originProjectPath, TemplateMakerFilesInfo templateMakerFilesInfo, TemplateMakerModelsConfig templateMakerModelsConfig) {

        // 获取当前工作目录
        String currentDir = System.getProperty("user.dir").replace("\\", "/");

        // 导入到workspaceId对应的工作空间中，如果工作空间已经存在，则不会导入项目
        workspaceId = importOriginProject2Workspace(originProjectPath, workspaceId);

        // 工作空间中的原始工程目录
        String originProjectInWorkspacePath = currentDir + "/" + ".temp/" + workspaceId + "/" + FileUtil.getLastPathEle(Paths.get(originProjectPath));

        // 模板文件依赖的模型参数列表

        // 制作多个模板文件
        List<Meta.FileConfig.FileInfo> fileInfoList = makeTemplateFiles(originProjectInWorkspacePath, templateMakerFilesInfo,templateMakerModelsConfig);

        // 生成元信息配置
        List<Meta.ModelConfig.ModelInfo> modelInfos = generateModelInfo(templateMakerModelsConfig, templateMakerModelsConfig.getModels());

        // 生成元信息配置文件
        generateMetaFile(originProjectInWorkspacePath, meta, fileInfoList, modelInfos);

    }

    private static List<Meta.ModelConfig.ModelInfo> generateModelInfo(TemplateMakerModelsConfig templateMakerModelsConfig, List<TemplateModelInfo> templateModelInfos) {
        List<Meta.ModelConfig.ModelInfo> modelInfos = new ArrayList<>();
        ModelGroupConfig modelGroupConfig = templateMakerModelsConfig.getModelGroupConfig();
        // 如果是模型组
        if (modelGroupConfig != null && StrUtil.isNotBlank(modelGroupConfig.getGroupKey())) {
            List<TemplateModelInfo> models = templateMakerModelsConfig.getModels();
            List<Meta.ModelConfig.ModelInfo> innerModelInfos = models.stream().map(TemplateModelInfo::getModelInfo).collect(Collectors.toList());
            Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
            modelInfo.setGroupKey(modelGroupConfig.getGroupKey());
            modelInfo.setGroupName(modelGroupConfig.getGroupName());
            modelInfo.setCondition(modelGroupConfig.getCondition());
            modelInfo.setModels(innerModelInfos);
            modelInfos.add(modelInfo);

        }else {
            modelInfos.addAll(templateModelInfos.stream().map(TemplateModelInfo::getModelInfo).collect(Collectors.toList()));
        } return modelInfos;
    }


    // 同时制作多个模板文件
    private List<Meta.FileConfig.FileInfo> makeTemplateFiles(String originProjectInWorkspacePath, TemplateMakerFilesInfo templateMakerFilesInfo, TemplateMakerModelsConfig templateMakerModelsConfig) {


        List<File> needOriginFiles = new ArrayList<>();

        // 处理原始项目路径
        List<TemplateFileConfig> templateFileConfigs = templateMakerFilesInfo.getFiles();
        for (TemplateFileConfig templateFileConfig : templateFileConfigs) {

            List<File> filteredFiles = fileFilter.filter(originProjectInWorkspacePath, templateFileConfig);

            // 添加过滤好的文件列表
            needOriginFiles.addAll(filteredFiles);
        }


        // 制作模板文件并构建文件配置信息
        List<Meta.FileConfig.FileInfo> fileInfoList = new ArrayList<>();
        for (File needOriginFile : needOriginFiles) {
            File waitedMakingTemplateFile = new File(needOriginFile.getParentFile(), needOriginFile.getName() + ".ftl");
            Meta.FileConfig.FileInfo fileInfo = makeSingleTemplateFile(originProjectInWorkspacePath,templateMakerModelsConfig , needOriginFile, waitedMakingTemplateFile);
            fileInfoList.add(fileInfo);
        }

        FileGroupConfig fileGroupConfig = templateMakerFilesInfo.getFileGroupConfig();
        if ( fileGroupConfig != null && StrUtil.isNotBlank(fileGroupConfig.getGroupKey())) {
            Meta.FileConfig.FileInfo fileInfo = new Meta.FileConfig.FileInfo();
            fileInfo.setType(FileTypeEnum.GROUP.value);
            fileInfo.setCondition(fileGroupConfig.getCondition());
            fileInfo.setGroupKey(fileGroupConfig.getGroupKey());
            fileInfo.setGroupName(fileGroupConfig.getGroupName());
            fileInfo.setFiles(fileInfoList);
            // TODO 可能有问题，不变性？？？
            return Collections.singletonList(fileInfo);

        }

        return fileInfoList;
    }


    private Meta.FileConfig.FileInfo makeSingleTemplateFile(String originProjectInWorkspacePath, TemplateMakerModelsConfig templateMakerModelsConfig, File needOriginFile, File waitedMakingTemplateFile) {
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
        for (TemplateModelInfo templateModelInfo : templateMakerModelsConfig.getModels()) {
            Meta.ModelConfig.ModelInfo modelInfo = templateModelInfo.getModelInfo();
            String searchStr = templateModelInfo.getSearchStr();

            ModelGroupConfig modelGroupConfig = templateMakerModelsConfig.getModelGroupConfig();
            String replacement;
            if (modelGroupConfig != null && StrUtil.isNotBlank(modelGroupConfig.getGroupKey())) {
                replacement = String.format("${%s.%s}", modelGroupConfig.getGroupKey(),modelInfo.getFieldName());
            }else {
                replacement = String.format("${%s}", modelInfo.getFieldName());
            }
            
            templateFileContent = StrUtil.replace(templateFileContent, searchStr, replacement);
        }


        // 构造FileInfo
        Meta.FileConfig.FileInfo fileInfo = new Meta.FileConfig.FileInfo();

        fileInfo.setInputPath(needOriginFilePath.replace("\\", "/").replace(originProjectInWorkspacePath + "/", ""));
        fileInfo.setOutputPath(madeTemplateFilePath.replace("\\", "/").replace(originProjectInWorkspacePath + "/", ""));
        fileInfo.setType(FileTypeEnum.FILE.value);


        // TODO 这个判断可能需要优化
        if (originFileContent.equals(templateFileContent)) {
            fileInfo.setGenerateType(GenerateTypeEnum.STATIC.value);
        } else {
            // 生成模板文件
            FileUtil.writeString(templateFileContent, madeTemplateFilePath, "UTF-8");
            // TODO 需要判断
            fileInfo.setGenerateType(GenerateTypeEnum.DYNAMIC.value);
        }

        return fileInfo;

    }


    // TODO 这里的参数meta可以优化 主要是携带了 name 和 desc
    private void generateMetaFile(String originProjectInWorkspacePath, Meta meta, List<Meta.FileConfig.FileInfo> fileInfos, List<Meta.ModelConfig.ModelInfo> modelInfos) {
        // 3.生成元信息配置文件
        String metaFilePath = originProjectInWorkspacePath + "/" + "meta.json";

        if (FileUtil.exist(metaFilePath)) {
            meta = JSONUtil.toBean(FileUtil.readUtf8String(metaFilePath), Meta.class);

            // 文件配置信息 -- 文件列表信息更新
            Meta.FileConfig fileConfig = meta.getFileConfig();
            List<Meta.FileConfig.FileInfo> files = fileConfig.getFiles();


            // 添加文件信息
            files.addAll(fileInfos);
            // 去重
            fileConfig.setFiles(distinctFiles(files));


            // 模型配置信息 -- 模型列表信息更新
            Meta.ModelConfig modelConfig = meta.getModelConfig();
            List<Meta.ModelConfig.ModelInfo> models = modelConfig.getModels();
            // 添加模型信息
            models.addAll(modelInfos);
            // 去重
            modelConfig.setModels(distinctModels(models));


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
            // 复制原始工程到工作空间
            FileUtil.copy(originProjectPath, workspaceDirPath, true);
        }
        return workspaceId;
    }


    /**
     * 模型列表去重
     *
     * @param models 元信息数据模型
     */
    private List<Meta.ModelConfig.ModelInfo> distinctModels(List<Meta.ModelConfig.ModelInfo> models) {
        // 最终的结果数据模型信息列表
        List<Meta.ModelConfig.ModelInfo> resultModelInfoList = new ArrayList<>();

        // 合并分组
        Map<String, List<Meta.ModelConfig.ModelInfo>> groupKey2ModelInfoMapping = models.stream().filter(modelInfo -> StrUtil.isNotBlank(modelInfo.getGroupKey()))
                .collect(Collectors.groupingBy(Meta.ModelConfig.ModelInfo::getGroupKey));

        for (Map.Entry<String, List<Meta.ModelConfig.ModelInfo>> entry : groupKey2ModelInfoMapping.entrySet()) {
            List<Meta.ModelConfig.ModelInfo> modelInfoList = entry.getValue();
            Collection<Meta.ModelConfig.ModelInfo> distinctModelInfosList = modelInfoList.stream().flatMap(modelInfo -> modelInfo.getModels().stream())
                    .collect(Collectors.toMap(Meta.ModelConfig.ModelInfo::getFieldName, Function.identity(), (k1, k2) -> k2)).values();
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
    private List<Meta.FileConfig.FileInfo> distinctFiles(List<Meta.FileConfig.FileInfo> files) {
        // 最终的结果文件信息列表
        List<Meta.FileConfig.FileInfo> resultFileInfoList = new ArrayList<>();

        // 合并分组
        Map<String, List<Meta.FileConfig.FileInfo>> groupKey2FileInfoMapping = files.stream().filter(fileInfo -> StrUtil.isNotBlank(fileInfo.getGroupKey()))
                .collect(Collectors.groupingBy(Meta.FileConfig.FileInfo::getGroupKey));

        for (Map.Entry<String, List<Meta.FileConfig.FileInfo>> entry : groupKey2FileInfoMapping.entrySet()) {
            List<Meta.FileConfig.FileInfo> fileInfoList = entry.getValue();
            Collection<Meta.FileConfig.FileInfo> distinctFileInfosList = fileInfoList.stream().flatMap(fileInfo -> fileInfo.getFiles().stream())
                    .collect(Collectors.toMap(Meta.FileConfig.FileInfo::getInputPath, Function.identity(), (k1, k2) -> k2)).values();
            Meta.FileConfig.FileInfo lastestFileInfo = CollUtil.getLast(fileInfoList);
            lastestFileInfo.setFiles(new ArrayList<>(distinctFileInfosList));
            resultFileInfoList.add(lastestFileInfo);
        }


        // 去重非分组文件
        Map<String, Meta.FileConfig.FileInfo> inputPath2FileInfoMapping = files.stream().filter(fileInfo -> StrUtil.isBlank(fileInfo.getGroupKey())).collect(Collectors.toMap(Meta.FileConfig.FileInfo::getInputPath, Function.identity(), (k1, k2) -> k2));
        resultFileInfoList.addAll(new ArrayList<>(inputPath2FileInfoMapping.values()));
        return resultFileInfoList;

    }


}
