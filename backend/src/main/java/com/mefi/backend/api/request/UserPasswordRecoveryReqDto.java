package com.mefi.backend.api.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserPasswordRecoveryReqDto {

    // 이메일
    @NotBlank
    @Email
    private String email;

    // 변경할 비밀번호
    @NotBlank
    private String modifyPassword;
}
