package com.pandaer.web.generator.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pandaer.maker.meta.Meta;
import com.pandaer.maker.meta.MetaValidator;
import com.pandaer.web.common.annotation.AuthCheck;
import com.pandaer.web.common.dto.req.DeleteRequest;
import com.pandaer.web.common.dto.resp.BaseResponse;
import com.pandaer.web.common.enums.ErrorCode;
import com.pandaer.web.common.exception.BusinessException;
import com.pandaer.web.common.exception.ThrowUtils;
import com.pandaer.web.common.manager.CosManager;
import com.pandaer.web.common.utils.ResultUtils;
import com.pandaer.web.generator.dto.req.*;
import com.pandaer.web.generator.dto.resp.GeneratorFeeVO;
import com.pandaer.web.generator.dto.resp.GeneratorVO;
import com.pandaer.web.generator.entity.Generator;
import com.pandaer.web.generator.entity.GeneratorFee;
import com.pandaer.web.generator.entity.UserGenerator;
import com.pandaer.web.generator.enums.GeneratorStatus;
import com.pandaer.web.generator.service.GeneratorFeeService;
import com.pandaer.web.generator.service.GeneratorService;
import com.pandaer.web.generator.service.UserGeneratorService;
import com.pandaer.web.user.entity.User;
import com.pandaer.web.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.pandaer.web.common.constant.UserConstant.ADMIN_ROLE;

/**
 * 帖子接口
 *
 
 */
@RestController
@RequestMapping("/generator")
@Slf4j
public class GeneratorController {

    @Resource
    private GeneratorService generatorService;


    @Resource
    private UserGeneratorService userGeneratorService;

    @Resource
    private UserService userService;

    @Resource
    private GeneratorFeeService generatorFeeService;

    @Autowired
    private CosManager cosManager;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    // region 增删改查

    /**
     * 创建
     *
     * @param generatorAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addGenerator(@RequestBody GeneratorAddRequest generatorAddRequest, HttpServletRequest request) {
        if (generatorAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        String name = generatorAddRequest.getName();
        // 额外的增强逻辑
        // 校验代码生成器名称是否符合Java命名规范
        if (!ReUtil.isMatch("^[a-zA-Z_$][a-zA-Z0-9_$]*$", name)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "名称必须符合Java标识符规范（字母/下划线/$开头，后续可包含数字）");
        }

        // 补充校验：不能是Java关键字（可选增强）
        if (StrUtil.hasBlank(name) || ReUtil.isMatch("^(abstract|assert|boolean|break|byte|...)$", name)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "名称不能是Java保留关键字");
        }

        Long generatorId = generatorService.addGenerator(generatorAddRequest, request);
        return ResultUtils.success(generatorId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteGenerator(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Generator oldGenerator = generatorService.getById(id);
        ThrowUtils.throwIf(oldGenerator == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldGenerator.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = generatorService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param generatorUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = ADMIN_ROLE)
    public BaseResponse<Boolean> updateGenerator(@RequestBody GeneratorUpdateRequest generatorUpdateRequest) {
        if (generatorUpdateRequest == null || generatorUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Generator generator = new Generator();
        BeanUtils.copyProperties(generatorUpdateRequest, generator);
        List<String> tags = generatorUpdateRequest.getTags();
        if (tags != null) {
            generator.setTags(JSONUtil.toJsonStr(tags));
        }
        // 参数校验
        generatorService.validGenerator(generator, false);
        long id = generatorUpdateRequest.getId();
        // 判断是否存在
        Generator oldGenerator = generatorService.getById(id);
        ThrowUtils.throwIf(oldGenerator == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = generatorService.updateById(generator);
        return ResultUtils.success(result);
    }

    /**
     * 在线使用代码生成器 TODO 需要抽离逻辑
     * @param useGeneratorRequest 在线使用代码生成器的参数
     * @return
     */
    @PostMapping("/use")
    public void useGeneratorByIdOnline(@RequestBody UseGeneratorRequest useGeneratorRequest,
                                       HttpServletRequest request,
                                       HttpServletResponse response) throws IOException, InterruptedException {

        // 用户登录
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        // 获取用户参数
        Long generatorId = useGeneratorRequest.getId();
        Map<String, Object> dataModel = useGeneratorRequest.getDataModel();

        // 获取对应ID的代码生成器信息
        Generator generator = generatorService.getById(generatorId);
        if (generator == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"对应的代码生成器不存在");
        }

        String distPath = generator.getDistPath();
        if (StrUtil.isBlank(distPath)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"对应的代码生成器产物包不存在");
        }

        // 使用代码生成器
        File generatedCodeFilesZip = generatorService.useGenerator(generator, distPath, dataModel);

        // 返回给前端
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        String fileName = generatedCodeFilesZip.getName();
        response.setHeader("Content-Disposition", "attachment;filename=" +fileName );
        Files.copy(generatedCodeFilesZip.toPath(), response.getOutputStream());

    }



    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<GeneratorVO> getGeneratorVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Generator generator = generatorService.getById(id);
        if (generator == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(generatorService.getGeneratorVO(generator, request));
    }

    /**
     * 分页获取列表（仅管理员）
     *
     * @param generatorQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = ADMIN_ROLE)
    public BaseResponse<Page<Generator>> listGeneratorByPage(@RequestBody GeneratorQueryRequest generatorQueryRequest) {
        long current = generatorQueryRequest.getCurrent();
        long size = generatorQueryRequest.getPageSize();

        // TODO 这样实现不够优雅
        QueryWrapper<Generator> queryWrapper = generatorService.getQueryWrapper(generatorQueryRequest);
//        queryWrapper.eq("status", GeneratorStatus.AUDIT_PASS.getValue());
        Page<Generator> generatorPage = generatorService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(generatorPage);
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param generatorQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<GeneratorVO>> listGeneratorVOByPage(@RequestBody GeneratorQueryRequest generatorQueryRequest, HttpServletRequest request) {
        long current = generatorQueryRequest.getCurrent();
        long size = generatorQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);


        // TODO 这样实现不够优雅
        QueryWrapper<Generator> queryWrapper = generatorService.getQueryWrapper(generatorQueryRequest);
        queryWrapper.eq("status", GeneratorStatus.AUDIT_PASS.getValue());
        Page<Generator> generatorPage = generatorService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(generatorService.getGeneratorVOPage(generatorPage, request));
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param generatorQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<GeneratorVO>> listMyGeneratorVOByPage(@RequestBody GeneratorQueryRequest generatorQueryRequest, HttpServletRequest request) {
        if (generatorQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        generatorQueryRequest.setUserId(loginUser.getId());
        long current = generatorQueryRequest.getCurrent();
        long size = generatorQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Generator> generatorPage = generatorService.page(new Page<>(current, size), generatorService.getQueryWrapper(generatorQueryRequest));
        return ResultUtils.success(generatorService.getGeneratorVOPage(generatorPage, request));
    }

    // endregion


    /**
     * 在线制作代码生成器
     * @param makingGeneratorRequest 制作代码生成器需要使用的参数
     * @param response 响应体
     */
    @PostMapping("/make")
    public void makeGeneratorOnline(@RequestBody MakingGeneratorRequest makingGeneratorRequest, HttpServletResponse response) {

        // 校验Meta元信息配置
        Meta meta = makingGeneratorRequest.getMeta();
        MetaValidator.validate(meta);

        String zipTemplateFilesUrl = makingGeneratorRequest.getZipTemplateFilesUrl();
        // TODO 校验 url是否存在

        // 一个校验的增强，校验代码生成器的名字只能是英文
        String name = makingGeneratorRequest.getMeta().getName();
        // 校验代码生成器名称是否符合Java命名规范
        if (!ReUtil.isMatch("^[a-zA-Z_$][a-zA-Z0-9_$]*$", name)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "名称必须符合Java标识符规范（字母/下划线/$开头，后续可包含数字）");
        }

        // 补充校验：不能是Java关键字（可选增强）
        if (StrUtil.hasBlank(name) || ReUtil.isMatch("^(abstract|assert|boolean|break|byte|...)$", name)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "名称不能是Java保留关键字");
        }

        generatorService.makeGenerator(makingGeneratorRequest,response);
    }


    @GetMapping("/users/purchase")
    public BaseResponse<UserGenerator> getGeneratorForUserPurchase(Long userId,Long generatorId) {
        if (ObjectUtil.hasNull(userId,generatorId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserGenerator purchasedGenerator = userGeneratorService.getGeneratorForPurchase(userId, generatorId);
        return ResultUtils.success(purchasedGenerator);
    }






    /**
     * 编辑（用户）
     *
     * @param generatorEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editGenerator(@RequestBody GeneratorEditRequest generatorEditRequest, HttpServletRequest request) {
        if (generatorEditRequest == null || generatorEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Generator generator = generatorEditRequest.toGenerator();
        // 参数校验
        generatorService.validGenerator(generator, false);

        User loginUser = userService.getLoginUser(request);
        long id = generatorEditRequest.getId();
        // 判断是否存在
        Generator oldGenerator = generatorService.getById(id);
        ThrowUtils.throwIf(oldGenerator == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldGenerator.getUserId().equals(loginUser.getId()) && !Objects.equals(loginUser.getUserRole(), ADMIN_ROLE)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = generatorService.updateById(generator);
        GeneratorFeeVO generatorFeeVO = generatorEditRequest.getGeneratorFee();
//        GeneratorFee generatorFee = BeanUtil.toBean(generatorFeeVO, GeneratorFee.class);
        if (generatorFeeVO!=null && generatorFeeVO.getPrice()!=null) {
            generatorFeeService.lambdaUpdate().eq(GeneratorFee::getGeneratorId,generator.getId()).set(GeneratorFee::getPrice,generatorFeeVO.getPrice()).update();
        }


        return ResultUtils.success(result);
    }


    @GetMapping("/{id}/free")
    public BaseResponse<Boolean> isFreeById(@PathVariable("id") Long generatorId) {
        if (generatorId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Generator generator = generatorService.getById(generatorId);
        if (generator == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        Boolean res = generatorService.isFreeById(generatorId);
        return ResultUtils.success(res);


    }

}
