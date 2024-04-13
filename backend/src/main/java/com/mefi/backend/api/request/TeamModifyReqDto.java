package com.mefi.backend.api.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamModifyReqDto {
    // 변경된 팀명
    String name;

    // 변경된 팀 설명
    String description;
}
