package com.pandaer.web.statistic.controller;


import com.pandaer.web.common.dto.resp.BaseResponse;
import com.pandaer.web.common.utils.ResultUtils;
import com.pandaer.web.generator.service.DownloadLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/statistic")
@RequiredArgsConstructor
public class StatisticController {

    private final DownloadLogService analysisService;

    @GetMapping("download-trend")
    public BaseResponse<Map<String, Object>> getDownloadTrend(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        return ResultUtils.success(analysisService.getDownloadTrend(start, end));
    }


    @GetMapping("generator-ranking")
    public BaseResponse<Map<String, Object>> getGeneratorRanking(Integer count) {
        // 校验请求参数
        if (count == null || count < 10) {
            count = 100;
        }

        // 获取排行榜数据
        Map<String,Object> rankingData =analysisService.getGeneratorRanking(count);

        // 返回结果
        return ResultUtils.success(rankingData);
    }


    @GetMapping("user-trend")
    public BaseResponse<Map<String, Object>> getNewUserTrend(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        LocalDate finalStart = Optional.ofNullable(start).orElse(LocalDate.now().minusDays(7));
        LocalDate finalEnd = Optional.ofNullable(end).orElse(LocalDate.now());

        // 获取注册用户数据
        Map<String,Object> newUserTrend =analysisService.getNewUserTrend(finalStart,finalEnd);

        // 返回结果
        return ResultUtils.success(newUserTrend);
    }






}
