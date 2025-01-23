package com.pandaer.maker.template;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;
import com.pandaer.maker.meta.Meta;
import com.pandaer.maker.template.model.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TemplateFileMakerTest {

    // 测试
    @Test
    public  void testTemplateMake() {
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
        modelInfo1.setFieldName("className");
        modelInfo1.setType("String");
        modelInfo1.setDescription("一个简单的类名");
        TemplateModelInfo templateModelInfo1 = new TemplateModelInfo();
        templateModelInfo1.setModelInfo(modelInfo1);
        templateModelInfo1.setSearchStr("BaseResponse");

        // 添加一个数据模型配置
        MakingTemplateModelsConfig makingTemplateModelsConfig = new MakingTemplateModelsConfig();
        List<TemplateModelInfo> templateModelInfos = new ArrayList<>();
        templateModelInfos.add(templateModelInfo1);
        makingTemplateModelsConfig.setModels(templateModelInfos);



        // 文件过滤配置
        TemplateFileConfig templateFileConfig2 = new TemplateFileConfig();
        templateFileConfig2.setPath("src/main/java/com/yupi/springbootinit/common");


        MakingTemplateFilesInfo makingTemplateFilesInfo = new MakingTemplateFilesInfo();
        makingTemplateFilesInfo.setFiles(Arrays.asList(templateFileConfig2));


        // 制作代码生成器依赖的模板文件
        TemplateFileMaker templateFileMaker = new TemplateFileMaker();
        FileFilter simpleFileFilter = new FileFilter();
        templateFileMaker.setFileFilter(simpleFileFilter);
        templateFileMaker.make("1", meta, originProjectPath, makingTemplateFilesInfo, makingTemplateModelsConfig,null);
    }


    /**
     * 通过一步一步的JSON文件，从而创建出一个SpringBoot模版项目的代码生成器
     */
    @Test
    public void testMakeSpringBootInitTemplateFilesWithJson() {
        TemplateFileMaker templateFileMaker = new TemplateFileMaker();
        FileFilter simpleFileFilter = new FileFilter();
        templateFileMaker.setFileFilter(simpleFileFilter);

        // 制作模板

        // 1. 制作代码生成器基本信息
        String templateMakerConfigJsonStr =
                ResourceUtil.readUtf8Str("examples/springbootinit/templateMaker1.json");
        MakingTemplatesConfig config = JSONUtil.toBean(templateMakerConfigJsonStr, MakingTemplatesConfig.class);
        templateFileMaker.make(config);

        // 2. 替换代码包名
        templateMakerConfigJsonStr =
                ResourceUtil.readUtf8Str("examples/springbootinit/templateMaker2.json");
        config = JSONUtil.toBean(templateMakerConfigJsonStr, MakingTemplatesConfig.class);
        templateFileMaker.make(config);

        // 3. 控制是否开启帖子相关的功能
        templateMakerConfigJsonStr =
                ResourceUtil.readUtf8Str("examples/springbootinit/templateMaker3.json");
        config = JSONUtil.toBean(templateMakerConfigJsonStr, MakingTemplatesConfig.class);
        templateFileMaker.make(config);

        // 4. 控制是否开启跨域
        templateMakerConfigJsonStr =
                ResourceUtil.readUtf8Str("examples/springbootinit/templateMaker4.json");
        config = JSONUtil.toBean(templateMakerConfigJsonStr, MakingTemplatesConfig.class);
        templateFileMaker.make(config);


        // 5. 控制是否开启接口文档
        templateMakerConfigJsonStr =
                ResourceUtil.readUtf8Str("examples/springbootinit/templateMaker5.json");
        config = JSONUtil.toBean(templateMakerConfigJsonStr, MakingTemplatesConfig.class);
        templateFileMaker.make(config);

        // 6. 控制是否开启MySQL的配置
        templateMakerConfigJsonStr =
                ResourceUtil.readUtf8Str("examples/springbootinit/templateMaker6.json");
        config = JSONUtil.toBean(templateMakerConfigJsonStr, MakingTemplatesConfig.class);
        templateFileMaker.make(config);

        // 7. 控制是否开启Redis的配置
        templateMakerConfigJsonStr =
                ResourceUtil.readUtf8Str("examples/springbootinit/templateMaker7.json");
        config = JSONUtil.toBean(templateMakerConfigJsonStr, MakingTemplatesConfig.class);
        templateFileMaker.make(config);

        // 8. 控制是否开启ES的配置
        templateMakerConfigJsonStr =
                ResourceUtil.readUtf8Str("examples/springbootinit/templateMaker8.json");
        config = JSONUtil.toBean(templateMakerConfigJsonStr, MakingTemplatesConfig.class);
        templateFileMaker.make(config);

        // 9. 补充接口文档具体的信息参数配置
        templateMakerConfigJsonStr =
                ResourceUtil.readUtf8Str("examples/springbootinit/templateMaker9.json");
        config = JSONUtil.toBean(templateMakerConfigJsonStr, MakingTemplatesConfig.class);
        templateFileMaker.make(config);


    }

}