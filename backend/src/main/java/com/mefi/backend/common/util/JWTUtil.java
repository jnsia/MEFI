package com.mefi.backend.common.util;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@Slf4j
public class JWTUtil {

    // 키를 저장할 객체
    private SecretKey secretKey;

    // properties 변수에서 값을 가져오기
    public JWTUtil(@Value("${spring.jwt.secret}") String secret) {

        // 객체 타입으로 암호화하여 생성
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {

            log.info("\n토큰 서명 확인 중");
            Jwts.parser().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
            log.info("\n토큰 서명 확인 완료");
            return true;

        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("\n잘못된 JWT 서명입니다.");

        } catch (ExpiredJwtException e) {
            log.info("\n만료된 JWT 토큰입니다.");

        } catch (UnsupportedJwtException e) {
            log.info("\n지원되지 않는 JWT 토큰 입니다.");

        } catch (IllegalArgumentException e) {
            log.info("\nJWT 토큰이 잘못되었습니다.");
        }

        return false;
    }

    // 검증 - 아이디 추출 메서드
    public String getEmail(String token) {

        // Jwts.parser() : JWT 파싱
        // verifyWith(secretKey) : 해당 서버에서 생성되었는지 검증
        // parseSignedClaims(token).getPayload().get("claims key", String.class)
        // -> 토큰을 파싱해 페이로드의 클레임 키에 해당하는 값을 문자열로 가져오기
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("email", String.class);
    }

    // 검증 - 직책 추출 메서드
    public String getPosition(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("position", String.class);
    }

    // 검증 - 만료 기간 추출 메서드
    public Boolean isExpired(String token) {

        // getExpiration().before(new Date())
        // -> 만료 기간이 현재 시간 이전인지 확인
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    // 토큰 생성 메서드
    public String createJwt(String email, String position, Long expiredMs) {

        return Jwts.builder()
                // 클레임 1
                .claim("email", email)
                // 클레임 2
                .claim("position", position)
                // 발행 시점
                .issuedAt(new Date(System.currentTimeMillis()))
                // 만료 시점
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                // 서명
                .signWith(secretKey)
                // 압축
                .compact();
    }
}