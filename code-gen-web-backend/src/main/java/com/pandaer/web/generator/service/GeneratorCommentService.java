package com.pandaer.web.generator.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pandaer.web.generator.dto.req.comment.AddGeneratorCommentRequest;
import com.pandaer.web.generator.dto.req.comment.PageListCommentsRequest;
import com.pandaer.web.generator.dto.req.comment.ReplyGeneratorCommentRequest;
import com.pandaer.web.generator.dto.resp.GeneratorCommentVO;
import com.pandaer.web.generator.entity.GeneratorComment;

import javax.servlet.http.HttpServletRequest;

/**
* @author pandaer
* @description 针对表【generator_comment】的数据库操作Service
* @createDate 2025-03-05 15:26:19
*/
public interface GeneratorCommentService extends IService<GeneratorComment> {

    void addComment(AddGeneratorCommentRequest generatorAddCommentRequest);

    void replyComment(ReplyGeneratorCommentRequest replyGeneratorCommentRequest);

    void deleteComment(Long commentId, HttpServletRequest request);

    Page<GeneratorCommentVO> pageListComments(PageListCommentsRequest pageListCommentsRequest);
}
