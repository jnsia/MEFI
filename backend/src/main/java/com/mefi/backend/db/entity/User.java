package com.mefi.backend.db.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 유저 모델 정의
 */
@Entity
@Getter
@NoArgsConstructor
public class User {
    // 회원식별ID
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 생성을 DB에 위임하는 전략
    private Long id;

    // 아이디(회원 이메일)
    private String email;

    // 비밀번호
    @JsonIgnore
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    // 이름
    private String name;

    // 부서
    private String dept;

    // 직책
    private String position;

    // 생성시간
    private LocalDateTime createdTime;

    // 프로필사진경로
    private String imgUrl;

    // 리프레시 토큰
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "token_id")
    private Token token;

    // 개인일정
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<PrivateSchedule> schedules = new ArrayList<>();

    // 알림
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Noti> notis = new ArrayList<>();

    // 팀과 연결 테이블
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserTeam> userTeams = new ArrayList<>();

    @Builder
    public User(String email, String password, String name, String dept, String position, String imgUrl) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.dept = dept;
        this.position = position;
        this.imgUrl = imgUrl;
        this.createdTime = LocalDateTime.now();
    }

    public void updateToken(Token token) {
        this.token = token;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateDept(String dept) {
        this.dept = dept;
    }

    public void updatePosition(String position) {
        this.position = position;
    }

    public void updateImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void updateAll(String name, String dept, String position, String imgUrl) {
        this.name = name;
        this.dept = dept;
        this.position = position;
        this.imgUrl = imgUrl;
    }
}