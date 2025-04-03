package com.pandaer.web.generator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pandaer.web.generator.dto.resp.GeneratorDownloadVO;
import com.pandaer.web.generator.entity.DownloadLog;
import com.pandaer.web.generator.mapper.DownloadLogMapper;
import com.pandaer.web.generator.service.DownloadLogService;
import com.pandaer.web.statistic.dto.resp.DailyDownloadVO;
import com.pandaer.web.statistic.dto.resp.DailyRegisterUserVO;
import com.pandaer.web.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
* @author pandaer
* @description 针对表【download_log】的数据库操作Service实现
* @createDate 2025-03-05 18:01:19
*/
@Service
@RequiredArgsConstructor
public class DownloadLogServiceImpl extends ServiceImpl<DownloadLogMapper, DownloadLog>
    implements DownloadLogService{

    private final DownloadLogMapper downloadLogMapper;

    private final UserMapper userMapper;

    @Override
    public Map<String, Object> getDownloadTrend(LocalDate start, LocalDate end) {
        // 处理时间范围（默认最近7天）
        LocalDate finalStart = Optional.ofNullable(start)
                .orElse(LocalDate.now().minusDays(6));
        LocalDate finalEnd = Optional.ofNullable(end)
                .orElse(LocalDate.now());

        // 转换为带时间的参数
        LocalDateTime startTime = finalStart.atStartOfDay();
        LocalDateTime endTime = finalEnd.atTime(23, 59, 59);

        // 查询数据
        List<DailyDownloadVO> list = downloadLogMapper.selectDailyStats(startTime, endTime);

        // 处理日期不连续问题（补全空日期）
        return processTrendData(list, finalStart, finalEnd);
    }

    @Override
    public Map<String, Object> getGeneratorRanking(Integer count) {
        // 获取到数据
        List<GeneratorDownloadVO> downloadList = downloadLogMapper.selectGeneratorStats(count);

        Map<String, Object> map = new HashMap<>();
        // 处理数据
        List<Long> generatorIds = downloadList.stream().map(GeneratorDownloadVO::getGeneratorId).collect(Collectors.toList());
        List<String> generatorName = downloadList.stream().map(it -> Optional.ofNullable(it.getGeneratorName()).orElse("未知")).collect(Collectors.toList());
        List<Long> downloadCount = downloadList.stream().map(GeneratorDownloadVO::getDownloadCount).collect(Collectors.toList());
        // 返回结果
        map.put("generatorIds", generatorIds);
        map.put("generatorName", generatorName);
        map.put("downloadCount", downloadCount);

        return map;
    }

    @Override
    public Map<String, Object> getNewUserTrend(LocalDate finalStart, LocalDate finalEnd) {
        // 转换为带时间的参数
        LocalDateTime startTime = finalStart.atStartOfDay();
        LocalDateTime endTime = finalEnd.atTime(23, 59, 59);


        // 直接获取数据
        List<DailyRegisterUserVO> list = downloadLogMapper.selectDailyRegisterUserStats(startTime, endTime);


        // 处理数据

        List<LocalDate> dateList = list.stream().map(DailyRegisterUserVO::getDate).collect(Collectors.toList());
        List<Integer> newUserCount = list.stream().map(DailyRegisterUserVO::getNewUserCount).collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("dates", dateList);
        result.put("newUsers", newUserCount);
        return Collections.unmodifiableMap(result);
    }


    private Map<String, Object> processTrendData(List<DailyDownloadVO> list,
                                                 LocalDate start, LocalDate end) {
        Map<LocalDate, DailyDownloadVO> dataMap = list.stream()
                .collect(Collectors.toMap(DailyDownloadVO::getDate, Function.identity()));

        List<String> dateList = new ArrayList<>();
        List<Integer> downloadList = new ArrayList<>();
        List<Integer> userList = new ArrayList<>();

        // 遍历所有日期（包括空缺）
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            DailyDownloadVO vo = dataMap.getOrDefault(date, new DailyDownloadVO()
                    .setDate(date)
                    .setDownloadCount(0)
                    .setUniqueUserCount(0));

            dateList.add(date.toString());
            downloadList.add(vo.getDownloadCount());
            userList.add(vo.getUniqueUserCount());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("dates", dateList);
        result.put("downloads", downloadList);
        result.put("users", userList);
        return Collections.unmodifiableMap(result);
    }
}




