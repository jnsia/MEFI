package com.mefi.backend.api.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserModifyPasswordReqDto {

    // 현재 비밀번호
    @NotBlank
    private String currentPassword;

    // 변경 비밀번호
    @NotBlank
    private String modifyPassword;
}