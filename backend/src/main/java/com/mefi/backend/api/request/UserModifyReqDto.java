package com.mefi.backend.api.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserModifyReqDto {

    // 변경 항목
    @NotBlank
    private String category;

    // 변경 내용
    @NotBlank
    private String content;
}
