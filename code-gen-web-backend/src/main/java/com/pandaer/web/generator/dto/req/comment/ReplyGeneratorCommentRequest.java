package com.pandaer.web.generator.dto.req.comment;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.pandaer.web.common.validate.Validatable;
import com.pandaer.web.common.validate.ValidatedResult;
import com.pandaer.web.generator.entity.GeneratorComment;
import lombok.Data;

/**
 * 请求对象，用于回复生成器评论
 * 该类封装了回复评论所需的信息，包括用户ID、生成器ID、评论内容和父评论ID
 */
@Data
public class ReplyGeneratorCommentRequest implements Validatable {

    /**
     * 用户ID，标识评论的作者
     */
    private Long userId;

    /**
     * 生成器ID，标识评论关联的生成器
     */
    private Long generatorId;

    /**
     * 评论内容，用户对生成器的评价或反馈
     */
    private String content;

    /**
     * 父评论ID
     */
    private Long parentId;

    @Override
    public ValidatedResult validate() {
        if (userId == null || generatorId == null || StrUtil.isBlank(content) || parentId == null) {
            return ValidatedResult.fail("参数为空");
        }

        if (content.length() > 1000) {
            return ValidatedResult.fail("评论内容过长");
        }
        return ValidatedResult.success();
    }

    public GeneratorComment mapToGeneratorComment() {
        return BeanUtil.toBean(this, GeneratorComment.class);
    }
}

