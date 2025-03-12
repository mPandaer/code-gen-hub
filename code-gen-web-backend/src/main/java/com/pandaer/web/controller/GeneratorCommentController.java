package com.pandaer.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pandaer.web.common.BaseResponse;
import com.pandaer.web.common.ErrorCode;
import com.pandaer.web.common.ResultUtils;
import com.pandaer.web.exception.BusinessException;
import com.pandaer.web.model.dto.generator.comment.AddGeneratorCommentRequest;
import com.pandaer.web.model.dto.generator.comment.PageListCommentsRequest;
import com.pandaer.web.model.dto.generator.comment.ReplyGeneratorCommentRequest;
import com.pandaer.web.model.vo.GeneratorCommentVO;
import com.pandaer.web.service.GeneratorCommentService;
import com.pandaer.web.validate.ValidatedResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/generators/comments")
public class GeneratorCommentController {

    @Autowired
    private GeneratorCommentService commentService;


    @PostMapping
    public BaseResponse<?> addComment(@RequestBody AddGeneratorCommentRequest generatorAddCommentRequest) {
        // 校验请求参数
        if (generatorAddCommentRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        ValidatedResult validatedResult = generatorAddCommentRequest.validate();
        if (!validatedResult.isSuccess()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, validatedResult.getMessage());
        }

        // 添加评论
        commentService.addComment(generatorAddCommentRequest);

        // 返回结果

        return ResultUtils.success(null);
    }

    @PostMapping("reply")
    public BaseResponse<?> replyComment(@RequestBody ReplyGeneratorCommentRequest replyGeneratorCommentRequest) {

        // 校验请求参数
        if (replyGeneratorCommentRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ValidatedResult validatedResult = replyGeneratorCommentRequest.validate();
        if (!validatedResult.isSuccess()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, validatedResult.getMessage());
        }

        // 回复评论
        commentService.replyComment(replyGeneratorCommentRequest);

        // 返回结果
        return ResultUtils.success(null);
    }



    @DeleteMapping("{id}")
    public BaseResponse<?> deleteComment(@PathVariable("id") Long commentId, HttpServletRequest request) {
        // 校验请求参数
        if (commentId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 删除自己的评论
        commentService.deleteComment(commentId,request);

        // 返回结果
        return ResultUtils.success(null);
    }

    @GetMapping("page")
    public BaseResponse<Page<GeneratorCommentVO>> pageListComments(PageListCommentsRequest pageListCommentsRequest) {

        // 校验请求参数
        if (pageListCommentsRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 获取分页数据
        Page<GeneratorCommentVO> commentsPage = commentService.pageListComments(pageListCommentsRequest);

        // 返回分页数据
        return ResultUtils.success(commentsPage);

    }





}
