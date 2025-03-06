package com.pandaer.web.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Data
@Accessors(chain = true)
public class DailyRegisterUserVO {
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    private Integer newUserCount;
}
