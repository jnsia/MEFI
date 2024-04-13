package com.mefi.backend.db.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "notification")
public class Noti {

    // 알림식별ID
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 생성을 DB에 위임하는 전략
    private Long id;

    // 메세지
    private String message;

    // 읽음여부
    private Boolean status;

    // 생성시간
    private LocalDateTime createdTime;

    // 수신자
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id")
    private User user;

    // 발송자 이름
    private String sender;


    @Builder
    public Noti(String message, Boolean status, LocalDateTime createdTime, User user, String sender){
        this.message = message;
        this.status = status;
        this.createdTime = createdTime;
        this.user = user;
        this.sender = sender;
    }

    public void read(){
        this.status = true;
    }

}
