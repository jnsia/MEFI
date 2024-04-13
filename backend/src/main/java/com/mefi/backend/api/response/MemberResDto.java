package com.mefi.backend.api.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

// 팀원 목록 조회시 팀원 정보를 담은 DTO
@Getter
@Setter
public class MemberResDto {

    // 유저 식별 ID
    Long id;

    // 이메일(아이디)
    String email;

    // 이름
    String name;

    // 직급
    String position;

    // 부서
    String department;

    // 생성자
    // @QueryProjection : DTO에 사용되며, Querydsl에서 결과를 특정 클래스의 인스턴스로 직접 받기 위해 사용
    @QueryProjection
    public MemberResDto(Long id, String email, String name, String position, String department) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.position = position;
        this.department = department;
    }
}
