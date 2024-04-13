package com.mefi.backend.api.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class ConferenceDetailResDto {

    // 회의 제목
    private String title;

    // 회의 내용
    private String description;

    // 회의 시작 시간
    private LocalDateTime callStart;

    // 회의 종료 시간
    private LocalDateTime callEnd;

    // 회의 접속 링크
    private String thumbnailUrl;

    // 회의 파일
    private List<String> meetingFiles;
}
