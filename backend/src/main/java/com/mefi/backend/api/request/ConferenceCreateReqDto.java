package com.mefi.backend.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class ConferenceCreateReqDto {

    // 회의 제목
    @NotBlank
    private String title;

    // 회의 주제, 내용, 설명
    @NotBlank
    private String description;

    // 회의 접속 링크
    private String thumbnailUrl;

    // 회의 시작 시간
    @NotNull
    private LocalDateTime callStart;

    // 회의 종료 시간
    @NotNull
    private LocalDateTime callEnd;
    
    // 회의 진행 팀
    @NotNull
    private Long teamId;
}