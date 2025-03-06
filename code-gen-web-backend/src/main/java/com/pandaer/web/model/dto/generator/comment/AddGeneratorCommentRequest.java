package com.pandaer.web.model.dto.generator.comment;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.pandaer.web.model.entity.GeneratorComment;
import com.pandaer.web.validate.Validatable;
import com.pandaer.web.validate.ValidatedResult;
import lombok.Data;

/**
 * 请求对象，用于添加生成器评论
 * 该类封装了添加评论所需的信息，包括用户ID、生成器ID、评论内容和父评论ID
 */
@Data
public class AddGeneratorCommentRequest implements Validatable {

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
     * 父评论ID，用于支持评论的层级结构如果该评论是顶级评论，则此字段为null
     */
    private Long parentId;

    @Override
    public ValidatedResult validate() {
        if (userId == null || generatorId == null || StrUtil.isBlank(content)) {
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

