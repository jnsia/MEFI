package com.mefi.backend.api.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class JoinReqDto {

    @NotBlank
    @Email
    // 아이디(회원 이메일)
    private String email;

    @NotBlank
    // 비밀번호
    private String password;

    @NotBlank
    // 이름
    private String name;

    // 부서
    private String dept;

    // 직책
    private String position;
}