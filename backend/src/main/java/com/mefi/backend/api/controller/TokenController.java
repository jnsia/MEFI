package com.mefi.backend.api.controller;

import com.mefi.backend.api.response.CreateAccessTokenResDto;
import com.mefi.backend.api.service.TokenService;
import com.mefi.backend.api.service.UserService;
import com.mefi.backend.common.auth.CustomUserDetails;
import com.mefi.backend.common.model.BaseResponseBody;
import com.mefi.backend.db.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
@Slf4j
@RestController
@RequestMapping("/api/token")
@RequiredArgsConstructor
@Tag(name = "2.TOKEN", description = "TOKEN API")
public class TokenController {
    private final TokenService tokenService;
    private final UserService userService;

    @PostMapping
    @Operation(summary = "Access Token 재발급", description = "/token\n\n 만료된 Access Token을 재발급한다.")
    @ApiResponse(responseCode = "200", description = "성공 \n\n Access Token 반환")
    public ResponseEntity<? extends BaseResponseBody> createAccessToken(
            Authentication authentication, @RequestHeader HttpHeaders headers) {

        // Authorization 헤더 값 (refreshToken) 가져오기
        String refreshToken = headers.getFirst("Authorization");

        // 로그인된 유저 정보 조회
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // 식별 ID로 유저 조회
        User user = userService.findById(userDetails.getUserId());

        // 유저 리프레시 토큰으로 엑세스 토큰 재발급
        CreateAccessTokenResDto accessToken = tokenService.createAccessToken(user, refreshToken);
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponseBody.of(0,accessToken));
    }
}