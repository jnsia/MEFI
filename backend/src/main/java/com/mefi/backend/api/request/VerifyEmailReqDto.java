package com.mefi.backend.api.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyEmailReqDto {

    @NotBlank
    @Email
    // 이메일 전송 아이디
    private String email;
}
