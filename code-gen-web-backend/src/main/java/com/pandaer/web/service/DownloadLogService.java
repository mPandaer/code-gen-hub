package com.pandaer.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pandaer.web.model.entity.DownloadLog;

import java.time.LocalDate;
import java.util.Map;

/**
* @author pandaer
* @description 针对表【download_log】的数据库操作Service
* @createDate 2025-03-05 18:01:19
*/
public interface DownloadLogService extends IService<DownloadLog> {

    Map<String, Object> getDownloadTrend(LocalDate start, LocalDate end);

    Map<String, Object> getGeneratorRanking(Integer count);

    Map<String, Object> getNewUserTrend(LocalDate finalStart, LocalDate finalEnd);
}
