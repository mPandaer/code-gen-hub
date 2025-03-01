package com.pandaer.web.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pandaer.maker.ZipGeneratorMaker;
import com.pandaer.web.common.ErrorCode;
import com.pandaer.web.constant.CommonConstant;
import com.pandaer.web.constant.FileConstant;
import com.pandaer.web.exception.BusinessException;
import com.pandaer.web.exception.ThrowUtils;
import com.pandaer.web.manager.CosManager;
import com.pandaer.web.mapper.GeneratorMapper;
import com.pandaer.maker.meta.Meta;
import com.pandaer.web.model.dto.generator.GeneratorQueryRequest;
import com.pandaer.web.model.dto.generator.MakingGeneratorRequest;
import com.pandaer.web.model.entity.Generator;
import com.pandaer.web.model.entity.User;
import com.pandaer.web.model.vo.GeneratorVO;
import com.pandaer.web.model.vo.UserVO;
import com.pandaer.web.service.GeneratorService;
import com.pandaer.web.service.UserService;
import com.pandaer.web.utils.SqlUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;
import cn.hutool.core.collection.CollUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

/**
 * 帖子服务实现
 */
@Service
@Slf4j
public class GeneratorServiceImpl extends ServiceImpl<GeneratorMapper, Generator> implements GeneratorService {

    @Resource
    private UserService userService;


    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Resource
    private CosManager cosManager;


    @Override
    public File useGenerator(Generator generator, String distPath, Map<String, Object> dataModel) throws IOException, InterruptedException {
        // 下载代码生成器压缩包
        // TODO distPath是一个绝对路径，带https://
        // 工作空间隔离
        String currentDir = System.getProperty("user.dir");

        // TODO 可能%d有问题
        String currentGeneratorWorkspace = String.format("%s/.temp/use/%d", currentDir, generator.getId());
        FileUtil.mkdir(currentGeneratorWorkspace);

        String targetPath = String.format("%s/dist.zip", currentGeneratorWorkspace);

        File zipDistFile = cosManager.download(distPath.replace(FileConstant.COS_HOST, ""), targetPath);

        String unzipDir = String.format("%s/dist", currentGeneratorWorkspace);
        // 解压并执行脚本文件
        ZipUtil.unzip(zipDistFile.getAbsolutePath(), unzipDir);

        // 将用户的数据模型参数保存在当前工作目录下的一个json文件
        String jsonFilePath = String.format("%s/dataModel.json", currentGeneratorWorkspace);
        FileUtil.writeUtf8String(JSONUtil.toJsonStr(dataModel), jsonFilePath);

        // 执行脚本文件 TODO 后期适配linux系统
        File scriptFile = FileUtil.loopFiles(new File(unzipDir), 3, null).stream().filter(file -> "generator.bat".equals(file.getName())).findFirst().orElseThrow(RuntimeException::new);

        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.directory(new File(currentGeneratorWorkspace, "dist"));
        String execCommand = String.format("%s jsonGenerate -f=\"%s\"", scriptFile.getAbsolutePath(), jsonFilePath);
        Process process = processBuilder.command(execCommand.split(" ")).start();
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "代码生成失败");
        }

        // 压缩生成好的代码文件并返回
        String generatedCodeFilesDir = String.format("%s/dist/generated", currentGeneratorWorkspace);
        File generatedCodeFilesZip = ZipUtil.zip(generatedCodeFilesDir);

        // 清空工作空间目录
        FileUtil.del(currentGeneratorWorkspace);
        return generatedCodeFilesZip;
    }

    /**
     * 制作代码生成器
     *
     * @param makingGeneratorRequest
     * @param response
     */
    @Override
    public void makeGenerator(MakingGeneratorRequest makingGeneratorRequest, HttpServletResponse response) {
        // 获取元信息配置 以及 压缩包下载地址
        Meta meta = makingGeneratorRequest.getMeta();
        String zipTemplateFilesUrl = makingGeneratorRequest.getZipTemplateFilesUrl();


        // 下载模板文件压缩包并解压
        // 创建制作代码生成器的工作空间目录 TODO 这段逻辑是可以被封装成一个函数的
        String currentDir = System.getProperty("user.dir").replace("\\", "/");
        String makingGeneratorId = IdUtil.getSnowflakeNextIdStr();
        // 当前制作器工作空间
        String currentMakerWorkspace = String.format("%s/.temp/make/%s", currentDir, makingGeneratorId);
        FileUtil.mkdir(currentMakerWorkspace);

        // todo templates.zip是否可以抽取为常量
        String localZipTemplateFiles = currentMakerWorkspace + "/" + "templates.zip";

        // 下载压缩文件到具体的工作空间中并解压
        File zipTemplateFiles = cosManager.download(zipTemplateFilesUrl.replace(FileConstant.COS_HOST, ""), localZipTemplateFiles);
        ZipUtil.unzip(zipTemplateFiles);

        // 利用Maker模块，根据Meta配置信息以及模板文件制作出一个代码生成器的压缩包
        Meta.FileConfig fileConfig = meta.getFileConfig();

        // 制作好的生成器路径
        String madeGeneratorPath = currentMakerWorkspace + "/made";
        fileConfig.setGeneratorPath(madeGeneratorPath);

        // 设置模板文件路径
        fileConfig.setOriginProjectPath(currentMakerWorkspace + "/templates");

        ZipGeneratorMaker zipGeneratorMaker = new ZipGeneratorMaker();
        try {
            zipGeneratorMaker.make(meta);
        } catch (Exception e) {
            log.error("失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "制作代码生成器失败");
        }

        //  将代码生成器文件返回给前端

        // 获取到生成器压缩包的路径

        // 压缩代码生成器
        File zippedGeneratorFile = ZipUtil.zip(madeGeneratorPath + "-dist");

        // 返回给前端
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader("Content-Disposition", "attachment;filename=" + zippedGeneratorFile.getName());
        try {
            Files.copy(zippedGeneratorFile.toPath(), response.getOutputStream());
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "传输生成器压缩包异常");
        }


        // 异步 清理工作空间
//        CompletableFuture.runAsync(() -> {
//            FileUtil.del(currentMakerWorkspace);
//        });


    }


    @Override
    public void validGenerator(Generator generator, boolean add) {
        if (generator == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        String name = generator.getName();
        String description = generator.getDescription();

        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(name, description), ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(name) && name.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标题过长");
        }
        if (StringUtils.isNotBlank(description) && description.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }
    }

    /**
     * 获取查询包装类
     *
     * @param generatorQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Generator> getQueryWrapper(GeneratorQueryRequest generatorQueryRequest) {
        QueryWrapper<Generator> queryWrapper = new QueryWrapper<>();
        if (generatorQueryRequest == null) {
            return queryWrapper;
        }

        String searchText = generatorQueryRequest.getSearchText();
        Long id = generatorQueryRequest.getId();
        String name = generatorQueryRequest.getName();
        String description = generatorQueryRequest.getDescription();
        String basePackage = generatorQueryRequest.getBasePackage();
        String author = generatorQueryRequest.getAuthor();
        List<String> tags = generatorQueryRequest.getTags();
        // TODO 未作为条件查询的参数
        List<String> orTags = generatorQueryRequest.getOrTags();
        Integer status = generatorQueryRequest.getStatus();
        Long userId = generatorQueryRequest.getUserId();
        String sortField = generatorQueryRequest.getSortField();
        String sortOrder = generatorQueryRequest.getSortOrder();

        // 拼接查询条件
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.and(qw -> qw.like("name", searchText).or().like("description", searchText));
        }
        queryWrapper.like(StringUtils.isNotBlank(name), "name", name);
        queryWrapper.like(StringUtils.isNotBlank(basePackage), "basePackage", basePackage);
        queryWrapper.like(StringUtils.isNotBlank(author), "author", author);
        queryWrapper.like(ObjectUtils.isNotEmpty(status), "status", status);
        queryWrapper.like(StringUtils.isNotBlank(description), "description", description);
        if (CollUtil.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        return queryWrapper;
    }


    @Override
    public GeneratorVO getGeneratorVO(Generator generator, HttpServletRequest request) {
        GeneratorVO generatorVO = GeneratorVO.objToVo(generator);
        long generatorId = generator.getId();
        // 1. 关联查询用户信息
        Long userId = generator.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        generatorVO.setUser(userVO);
        // 2. 已登录，获取用户点赞、收藏状态
        User loginUser = userService.getLoginUserPermitNull(request);
        return generatorVO;
    }

    @Override
    public Page<GeneratorVO> getGeneratorVOPage(Page<Generator> generatorPage, HttpServletRequest request) {
        List<Generator> generatorList = generatorPage.getRecords();
        Page<GeneratorVO> generatorVOPage = new Page<>(generatorPage.getCurrent(), generatorPage.getSize(), generatorPage.getTotal());
        if (CollUtil.isEmpty(generatorList)) {
            return generatorVOPage;
        }
        // 1. 关联查询用户信息
        Set<Long> userIdSet = generatorList.stream().map(Generator::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream().collect(Collectors.groupingBy(User::getId));
        // 2. 已登录，获取用户点赞、收藏状态
        Map<Long, Boolean> generatorIdHasThumbMap = new HashMap<>();
        Map<Long, Boolean> generatorIdHasFavourMap = new HashMap<>();
        User loginUser = userService.getLoginUserPermitNull(request);
        if (loginUser != null) {
            Set<Long> generatorIdSet = generatorList.stream().map(Generator::getId).collect(Collectors.toSet());
            loginUser = userService.getLoginUser(request);
        }
        // 填充信息
        List<GeneratorVO> generatorVOList = generatorList.stream().map(generator -> {
            GeneratorVO generatorVO = GeneratorVO.objToVo(generator);
            Long userId = generator.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            generatorVO.setUser(userService.getUserVO(user));
            return generatorVO;
        }).collect(Collectors.toList());
        generatorVOPage.setRecords(generatorVOList);
        return generatorVOPage;
    }

}




