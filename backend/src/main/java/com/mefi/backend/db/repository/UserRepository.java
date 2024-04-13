package com.mefi.backend.db.repository;

import com.mefi.backend.db.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long>, UserRepositoryCustom {

    // 이메일로 회원 조회
    Optional<User> findByEmail(String email);

    // 식별 ID로 회원 조회
    Optional<User> findById(Long id);
}