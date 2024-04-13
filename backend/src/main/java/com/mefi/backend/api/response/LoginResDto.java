package com.mefi.backend.api.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginResDto {

    // 회원 식별 ID
    private Long id;
    
    // 이메일
    private String email;

    // 이름
    private String name;

    // 부서
    private String dept;

    // 직책
    private String position;

    // 프로필 이미지;
    private byte[] profile;
}
