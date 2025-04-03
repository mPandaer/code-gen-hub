package com.pandaer.web.generator.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pandaer.web.generator.dto.req.GeneratorAddRequest;
import com.pandaer.web.generator.dto.req.GeneratorQueryRequest;
import com.pandaer.web.generator.dto.req.MakingGeneratorRequest;
import com.pandaer.web.generator.dto.resp.GeneratorVO;
import com.pandaer.web.generator.entity.Generator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * 帖子服务
 *
 
 */
public interface GeneratorService extends IService<Generator> {

    // 添加代码生成器
    Long addGenerator(GeneratorAddRequest generatorAddRequest, HttpServletRequest request);





    /**
     * 校验
     *
     * @param generator
     * @param add
     */
    void validGenerator(Generator generator, boolean add);

    /**
     * 获取查询条件
     *
     * @param generatorQueryRequest
     * @return
     */
    QueryWrapper<Generator> getQueryWrapper(GeneratorQueryRequest generatorQueryRequest);

    /**
     * 获取帖子封装
     *
     * @param generator
     * @param request
     * @return
     */
    GeneratorVO getGeneratorVO(Generator generator, HttpServletRequest request);

    /**
     * 分页获取帖子封装
     *
     * @param generatorPage
     * @param request
     * @return
     */
    Page<GeneratorVO> getGeneratorVOPage(Page<Generator> generatorPage, HttpServletRequest request);

    File useGenerator(Generator generator, String distPath, Map<String, Object> dataModel) throws IOException, InterruptedException;

    void makeGenerator(MakingGeneratorRequest makingGeneratorRequest, HttpServletResponse response);


    Boolean isFreeById(Long generatorId);
}
