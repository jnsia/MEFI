package com.mefi.backend.api.service;

import com.mefi.backend.api.response.CreateAccessTokenResDto;
import com.mefi.backend.db.entity.Token;
import com.mefi.backend.db.entity.User;

public interface TokenService {

    // 엑세스 토큰 재발급
    CreateAccessTokenResDto createAccessToken(User user, String refreshToken);

    // 유저 식별 ID로 토큰 조회
    Token findByUserId(Long id);

    // 리프레시 토큰 제거
    void deleteRefreshToken(Token token);
}