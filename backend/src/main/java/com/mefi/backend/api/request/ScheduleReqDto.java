package com.mefi.backend.api.request;

import com.mefi.backend.db.entity.ScheduleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ScheduleReqDto {

    // 시작 시간
    @NotNull(message = "시작 시간은 필수 입력 값입니다.")
    private LocalDateTime startedTime;

    // 종료 시간
    @NotNull(message = "종료 시간은 필수 입력 값입니다.")
    private LocalDateTime endTime;

    // 일정 유형
    @NotNull(message = "일정 유형은 필수 입력 값입니다.")
    private ScheduleType type;

    // 요약
    @NotBlank(message = "일정 제목을 입력해주세요.")
    private String summary;

    // 상세 설명
    @NotBlank(message = "일정에 대한 설명을 입력해주세요.")
    private String description;
}
