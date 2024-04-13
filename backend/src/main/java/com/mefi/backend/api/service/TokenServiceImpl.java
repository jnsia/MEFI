package com.mefi.backend.api.service;

import com.mefi.backend.api.response.CreateAccessTokenResDto;
import com.mefi.backend.common.util.JWTUtil;
import com.mefi.backend.db.entity.Token;
import com.mefi.backend.db.entity.User;
import com.mefi.backend.db.repository.TokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final JWTUtil jwtUtil;
    private final TokenRepository tokenRepository;

    // 엑세스 토큰 재발급
    @Override
    @Transactional
    public CreateAccessTokenResDto createAccessToken(User user, String refreshToken) {

        // 엑세스 토큰 재발급 및 저장
        String accessToken = jwtUtil.createJwt(user.getEmail(), user.getPosition(), 60*60*1000L);
        return new CreateAccessTokenResDto(accessToken);
    }

    // 유저 식별 ID로 토큰 조회
    @Override
    public Token findByUserId(Long id) {
        return tokenRepository.findByUserId(id)
                .orElseThrow(() -> new IllegalArgumentException());
    }

    // 리프레시 토큰 제거
    @Override
    public void deleteRefreshToken(Token token) {
        tokenRepository.delete(token);
    }
}
