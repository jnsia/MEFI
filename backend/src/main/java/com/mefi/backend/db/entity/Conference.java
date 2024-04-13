package com.mefi.backend.db.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name ="conference")
@Getter
@NoArgsConstructor
public class Conference {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 생성을 DB에 위임하는 전략
    private Long id; // 식별ID

    private Long leaderId; // 팀장ID

    private LocalDateTime callStart;

    private LocalDateTime callEnd;

    private String thumbnailUrl;

    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    private ConferenceStatus status;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Team team;

    @OneToMany(mappedBy = "conference", orphanRemoval = true)
    private List<MeetingFile> meetingFiles = new ArrayList<>();

    @Builder
    public Conference(Long leaderId, String title, String description,
                      LocalDateTime callStart, LocalDateTime callEnd, Team team, String thumbnailUrl) {

        this.leaderId = leaderId;
        this.title = title;
        this.description = description;
        this.callStart = callStart;
        this.callEnd = callEnd;
        this.team = team;
        this.status = ConferenceStatus.TODO;
        this.thumbnailUrl = thumbnailUrl;
    }

    // 회의 취소 상태 변경 메서드
    public void cancelConferenceStatus() {
        this.status = ConferenceStatus.CANCELED;
    }

    // 회의 종료 상태 변경 메서드
    public void doneConferenceStatus() {
        this.status = ConferenceStatus.DONE;
    }

    // 회의 세션 ID 변경 메서드
    public void saveConferenceSessionId(String sessionId){
        this.thumbnailUrl = sessionId;
    }

    // 회의 정보 전체 수정
    public void updateAll(LocalDateTime callStart, LocalDateTime callEnd, String title, String description) {
        this.callStart = callStart;
        this.callEnd = callEnd;
        this.title = title;
        this.description = description;
    }
}