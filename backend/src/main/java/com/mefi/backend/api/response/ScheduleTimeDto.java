package com.mefi.backend.api.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ScheduleTimeDto {

    // 시작시간
    private LocalDateTime startedTime;

    // 종료시간
    private LocalDateTime endTime;

    public ScheduleTimeDto(LocalDateTime startedTime, LocalDateTime endTime) {
        this.startedTime = startedTime;
        this.endTime = endTime;
    }
}
