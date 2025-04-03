package com.pandaer.web.generator.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pandaer.web.common.enums.ErrorCode;
import com.pandaer.web.common.exception.BusinessException;
import com.pandaer.web.generator.dto.req.comment.AddGeneratorCommentRequest;
import com.pandaer.web.generator.dto.req.comment.PageListCommentsRequest;
import com.pandaer.web.generator.dto.req.comment.ReplyGeneratorCommentRequest;
import com.pandaer.web.generator.dto.resp.GeneratorCommentVO;
import com.pandaer.web.generator.entity.GeneratorComment;
import com.pandaer.web.generator.mapper.GeneratorCommentMapper;
import com.pandaer.web.generator.service.GeneratorCommentService;
import com.pandaer.web.user.converter.UserConverter;
import com.pandaer.web.user.dto.resp.UserVO;
import com.pandaer.web.user.entity.User;
import com.pandaer.web.user.entity.UserLevelPrivilege;
import com.pandaer.web.user.enums.UserActivityTypeEnum;
import com.pandaer.web.user.event.UserActivityEvent;
import com.pandaer.web.user.service.UserLevelPrivilegeService;
import com.pandaer.web.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
* @author pandaer
* @description 针对表【generator_comment】的数据库操作Service实现
* @createDate 2025-03-05 15:26:19
*/
@Service
public class GeneratorCommentServiceImpl extends ServiceImpl<GeneratorCommentMapper, GeneratorComment>
    implements GeneratorCommentService{


    @Autowired
    private UserService userService;

    @Autowired
    private UserConverter userConverter;
    @Autowired
    private UserLevelPrivilegeService userLevelPrivilegeService;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void addComment(AddGeneratorCommentRequest generatorAddCommentRequest) {
        // 转换为实体对象直接保存
        GeneratorComment comment = generatorAddCommentRequest.mapToGeneratorComment();
        boolean saveResult = save(comment);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "评论失败");
        }

        // 发布添加评论的用户事件
        applicationEventPublisher.publishEvent(new UserActivityEvent(comment.getUserId(), UserActivityTypeEnum.PUBLISH_COMMENT,this));
    }

    @Override
    public void replyComment(ReplyGeneratorCommentRequest replyGeneratorCommentRequest) {
        GeneratorComment comment = replyGeneratorCommentRequest.mapToGeneratorComment();
        boolean saveResult = save(comment);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "回复失败");
        }
    }

    @Override
    public void deleteComment(Long commentId, HttpServletRequest request) {

        // 判断这条评论是不是当前登录用户的
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        GeneratorComment comment = lambdaQuery().eq(GeneratorComment::getUserId, loginUser.getId()).eq(GeneratorComment::getId, commentId).one();
        if (comment == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"评论不存在或不属于你的评论");
        }

        boolean deleteResult = removeById(commentId);
        if (!deleteResult) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "删除失败");
        }

    }

    @Override
    public Page<GeneratorCommentVO> pageListComments(PageListCommentsRequest pageListCommentsRequest) {

        Page<GeneratorComment> page = Page.of(pageListCommentsRequest.getPageNum(), pageListCommentsRequest.getPageSize());

        // 直接分页查询
        Page<GeneratorComment> commentPage = lambdaQuery().eq(GeneratorComment::getGeneratorId, pageListCommentsRequest.getGeneratorId()).page(page);


        if (commentPage.getRecords().isEmpty()) {
            Page<GeneratorCommentVO> voPage = new Page<>();
            BeanUtil.copyProperties(commentPage, voPage,"records");
            return voPage;
        }



        // 用户Id合集
        Set<Long> userIdSet = commentPage.getRecords().stream().map(GeneratorComment::getUserId).collect(Collectors.toSet());

        List<User> users = userService.listByIds(userIdSet);

        Map<Long, User> idUserMapping = users.stream().collect(Collectors.toMap(User::getId, Function.identity()));


        List<GeneratorCommentVO> voList = commentPage.getRecords().stream().map(it -> {
            GeneratorCommentVO vo = BeanUtil.toBean(it, GeneratorCommentVO.class);
            User user = idUserMapping.get(it.getUserId());
            if (user != null) {
                UserVO userVO = userConverter.entityMapToVO(user);
                vo.setUserVO(userVO);
            }

            return vo;
        }).collect(Collectors.toList());





        Page<GeneratorCommentVO> voPage = new Page<>();

        BeanUtil.copyProperties(commentPage, voPage,"records");
        voPage.setRecords(voList);

        return voPage;


    }
}




