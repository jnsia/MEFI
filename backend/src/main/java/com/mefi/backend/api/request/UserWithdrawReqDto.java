package com.mefi.backend.api.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserWithdrawReqDto {

    @NotBlank
    // 본인 인증을 위한 현재 비밀번호
    private String currentPassword;

}