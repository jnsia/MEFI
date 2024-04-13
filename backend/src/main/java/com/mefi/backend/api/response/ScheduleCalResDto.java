package com.mefi.backend.api.response;

import com.mefi.backend.db.entity.ScheduleType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class ScheduleCalResDto {

    // 회의 식별 ID
    private Long id;

    // 요약
    private String summary;

    // 시작시간
    private LocalDateTime startedTime;

    // 종료시간
    private LocalDateTime endTime;

    // 개인 일정 타입
    private ScheduleType type;

    public ScheduleCalResDto(Long id, String summary, LocalDateTime startedTime, LocalDateTime endTime, ScheduleType type) {
        this.id = id;
        this.summary = summary;
        this.startedTime = startedTime;
        this.endTime = endTime;
        this.type = type;
    }
}

