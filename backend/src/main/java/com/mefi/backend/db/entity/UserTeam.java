package com.mefi.backend.db.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 유저-팀 연관테이블
 */
@Entity
@Table(name = "user_team")
@NoArgsConstructor
@Getter
@ToString
public class UserTeam {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Team team;

    @Builder
    public UserTeam(User user, Team team, UserRole role){
        this.user = user;
        this.team = team;
        this.role = role;
    }

    public void changeRole(UserRole role){
        this.role = role;
    }
}
