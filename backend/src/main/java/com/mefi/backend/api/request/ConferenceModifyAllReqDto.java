package com.mefi.backend.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ConferenceModifyAllReqDto {

    // 회의 시작 시간
    @NotNull
    private LocalDateTime callStart;

    // 회의 종료 시간
    @NotNull
    private LocalDateTime callEnd;
    
    // 회의 제목
    @NotBlank
    private String title;
    
    // 회의 설명
    @NotBlank
    private String description;
}
