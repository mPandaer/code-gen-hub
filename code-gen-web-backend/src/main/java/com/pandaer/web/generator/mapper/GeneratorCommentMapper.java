package com.pandaer.web.generator.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pandaer.web.generator.entity.GeneratorComment;
import org.apache.ibatis.annotations.Mapper;

/**
* @author pandaer
* @description 针对表【generator_comment】的数据库操作Mapper
* @createDate 2025-03-05 15:26:19
* @Entity com.pandaer.web.modules.generator.entity.GeneratorComment
*/
@Mapper
public interface GeneratorCommentMapper extends BaseMapper<GeneratorComment> {

}




