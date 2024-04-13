package com.mefi.backend.db.repository;

import com.mefi.backend.db.entity.Token;
import com.mefi.backend.db.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token,Long> {

    // 유저 식별 ID로 토큰 조회
    Optional<Token> findByUserId(Long UserId);
}