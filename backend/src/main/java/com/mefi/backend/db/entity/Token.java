package com.mefi.backend.db.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name ="token")
@Getter
@NoArgsConstructor
public class Token {
    @Id // PK
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto Increment
    private Long id;

    @Column(name = "refresh_token", length = 500, nullable = false)
    private String refreshToken;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Builder
    public Token(Long userId, String refreshToken) {
        this.userId = userId;
        this.refreshToken = refreshToken;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}