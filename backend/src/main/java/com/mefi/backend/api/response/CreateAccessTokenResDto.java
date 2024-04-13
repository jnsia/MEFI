package com.mefi.backend.api.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class CreateAccessTokenResDto {

    // 엑세스 토큰
    private String accessToken;
}
