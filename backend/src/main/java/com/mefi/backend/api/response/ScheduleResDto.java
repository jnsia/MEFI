package com.mefi.backend.api.response;

import com.mefi.backend.db.entity.PrivateSchedule;

public class ScheduleResDto {
    // 요약
    private String summary;

    // 상세 설명
    private String description;


    // 생성자, DTO-엔티티 변환
    public ScheduleResDto(PrivateSchedule privateSchedule){
        this.summary = privateSchedule.getSummary();
        this.description = privateSchedule.getDescription();
    }
}
