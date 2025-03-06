package com.pandaer.web.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pandaer.web.common.ErrorCode;
import com.pandaer.web.exception.BusinessException;
import com.pandaer.web.model.dto.generator.comment.AddGeneratorCommentRequest;
import com.pandaer.web.model.dto.generator.comment.PageListCommentsRequest;
import com.pandaer.web.model.dto.generator.comment.ReplyGeneratorCommentRequest;
import com.pandaer.web.model.entity.GeneratorComment;
import com.pandaer.web.model.entity.User;
import com.pandaer.web.model.vo.GeneratorCommentVO;
import com.pandaer.web.service.GeneratorCommentService;
import com.pandaer.web.mapper.GeneratorCommentMapper;
import com.pandaer.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Override
    public void addComment(AddGeneratorCommentRequest generatorAddCommentRequest) {
        // 转换为实体对象直接保存
        GeneratorComment comment = generatorAddCommentRequest.mapToGeneratorComment();
        boolean saveResult = save(comment);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "评论失败");
        }
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
            vo.setUserVO(idUserMapping.get(it.getUserId()).mapToUserVO());
            return vo;
        }).collect(Collectors.toList());





        Page<GeneratorCommentVO> voPage = new Page<>();

        BeanUtil.copyProperties(commentPage, voPage,"records");
        voPage.setRecords(voList);

        return voPage;


    }
}




