package com.pandaer.web.generator.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pandaer.web.generator.entity.GeneratorAudit;
import org.apache.ibatis.annotations.Mapper;

/**
* @author pandaer
* @description 针对表【generator_audit(生成器审核记录表)】的数据库操作Mapper
* @createDate 2025-03-29 10:05:55
* @Entity com.pandaer.web.modules.generator.entity.GeneratorAudit
*/
@Mapper
public interface GeneratorAuditMapper extends BaseMapper<GeneratorAudit> {

}




