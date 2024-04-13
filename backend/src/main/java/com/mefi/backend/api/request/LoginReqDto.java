package com.mefi.backend.api.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginReqDto {

    // 이메일
    @NotBlank
    @Email
    private String email;

    // 비밀번호
    @NotBlank
    private String password;
}
