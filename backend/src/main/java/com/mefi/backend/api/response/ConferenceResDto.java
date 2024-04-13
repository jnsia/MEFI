package com.mefi.backend.api.response;

import com.mefi.backend.db.entity.Conference;
import com.mefi.backend.db.entity.ConferenceStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class ConferenceResDto {
    private Long id; // 회의 식별 ID

    private LocalDateTime callStart; // 회의 시작 시간

    private LocalDateTime callEnd; // 회의 종료 시간

    private String title; // 회의 제목

    private ConferenceStatus status; // 회의 상태

    // 엔티티를 DTO로 변환
    public ConferenceResDto(Conference conference){
        this.id = conference.getId();
        this.callStart = conference.getCallStart();
        this.callEnd = conference.getCallEnd();
        this.title = conference.getTitle();
        this.status = conference.getStatus();
    }
}
