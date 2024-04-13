package com.mefi.backend.api.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class TeamDetailDto {
    // 팀 식별 ID
    Long teamId;

    // 팀명
    String teamName;

    // 팀 설명
    String teamDescription;

    // 팀 생성 일시
    private LocalDateTime createdTime;


    public TeamDetailDto(Long teamId, String teamName, String teamDescription, LocalDateTime createdTime) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.teamDescription = teamDescription;
        this.createdTime = createdTime;
    }
}
