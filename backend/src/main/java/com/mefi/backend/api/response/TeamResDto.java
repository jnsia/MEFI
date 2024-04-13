package com.mefi.backend.api.response;

import com.mefi.backend.db.entity.UserRole;
import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

// 사용자 팀 목록 조회 응답 DTO

@Getter
@Setter
@NoArgsConstructor
@ToString
public class TeamResDto {

    // 팀 식별 ID
    Long teamId;

    // 팀명
    String teamName;

    // 팀 설명
    String teamDescription;

    // 유저 권한
    UserRole role;

    // 생성자
    // @QueryProjection : DTO에 사용되며, Querydsl에서 결과를 특정 클래스의 인스턴스로 직접 받기 위해 사용
    @QueryProjection
    public TeamResDto(Long teamId, String teamName, String teamDescription, UserRole role){
        this.teamId = teamId;
        this.teamName = teamName;
        this.teamDescription = teamDescription;
        this.role = role;
    }
}
