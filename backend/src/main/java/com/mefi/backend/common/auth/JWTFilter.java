package com.mefi.backend.common.auth;

import com.mefi.backend.common.exception.ErrorCode;
import com.mefi.backend.common.exception.Exceptions;
import com.mefi.backend.common.util.JWTUtil;
import com.mefi.backend.db.entity.User;
import com.mefi.backend.db.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // request에서 Authorization 헤더 찾기
        String authorization = request.getHeader("Authorization");

        // Authorization 헤더 검증 (토큰이 담겨있는지 확인)
        // -> 토큰이 없거나, Bearer 없는 경우
        if (authorization == null || !authorization.startsWith("Bearer ")) {

            // 다음 필터로 요청을 전달하거나, 요청에 대한 실제 서블릿 또는 리소스로 전달
            filterChain.doFilter(request, response);

            // 조건이 해당되면 메소드 종료 (필수)
            return;
        }

        // Bearer 부분 제거 후 순수 토큰만 획득
        String token = authorization.split(" ")[1];

        // 토큰 소멸 시간 검증
        if (jwtUtil.isExpired(token)) {

            // 시간이 만료된 경우 다음 필터로 전달
            filterChain.doFilter(request, response);

            // 조건이 해당되면 메소드 종료 (필수)
            return;
        }

        // 토큰 유효성 검사
        if(!jwtUtil.validateToken(token)){
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰에서 email, position 획득
        String email = jwtUtil.getEmail(token);
        String position = jwtUtil.getPosition(token);

        // user 생성하여 값 초기화
        if(!userRepository.findByEmail(email).isPresent())
            throw new Exceptions(ErrorCode.USER_NOT_EXIST);

        User user = userRepository.findByEmail(email).get();

        // UserDetails 회원 정보 객체 담기
        CustomUserDetails customUserDetails = new CustomUserDetails(user);

        // 스프링 시큐리티 인증 토큰 생성 (회원 정보 객체 토큰)
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

        // 세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // 다음 필터로 요청, 응답 전달
        filterChain.doFilter(request, response);
    }
}