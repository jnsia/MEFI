package com.mefi.backend.db.repository;

import com.mefi.backend.db.entity.EmailAuth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MailRepository extends JpaRepository <EmailAuth, Long> {
    
    // 이메일로 인증 엔티티 조회
    Optional<EmailAuth> findByEmail(String email);
}
