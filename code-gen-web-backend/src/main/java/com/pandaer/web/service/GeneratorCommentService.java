package com.pandaer.web.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pandaer.web.model.dto.generator.comment.AddGeneratorCommentRequest;
import com.pandaer.web.model.dto.generator.comment.PageListCommentsRequest;
import com.pandaer.web.model.dto.generator.comment.ReplyGeneratorCommentRequest;
import com.pandaer.web.model.entity.GeneratorComment;
import com.pandaer.web.model.vo.GeneratorCommentVO;

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
