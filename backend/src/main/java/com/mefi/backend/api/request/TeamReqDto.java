package com.mefi.backend.api.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TeamReqDto {

    // 팀장 ID
    private Long leaderId;

    // 팀 이름
    private String teamName;

    // 팀 설명
    private String teamDescription;

    // 팀원 ID
    private List<Long> members;
}
