package com.pandaer.web.generator.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pandaer.web.generator.dto.resp.GeneratorDownloadVO;
import com.pandaer.web.generator.entity.DownloadLog;
import com.pandaer.web.statistic.dto.resp.DailyDownloadVO;
import com.pandaer.web.statistic.dto.resp.DailyRegisterUserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
* @author pandaer
* @description 针对表【download_log】的数据库操作Mapper
* @createDate 2025-03-05 18:01:19
* @Entity com.pandaer.web.modules.generator.entity.DownloadLog
*/
@Mapper
public interface DownloadLogMapper extends BaseMapper<DownloadLog> {

    List<DailyDownloadVO> selectDailyStats(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );


    List<GeneratorDownloadVO> selectGeneratorStats(
            @Param("count") Integer count
    );


    List<DailyRegisterUserVO> selectDailyRegisterUserStats(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );


}




