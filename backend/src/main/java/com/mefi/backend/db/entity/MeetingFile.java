package com.mefi.backend.db.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "file")
public class MeetingFile {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 생성을 DB에 위임하는 전략
    private Long id;

    @Column(name = "file_name")
    private String fileName;

    @Enumerated(EnumType.STRING)
    private MeetingFileType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conference_id")
    private Conference conference;

    @Builder
    public MeetingFile(String fileName, MeetingFileType type){
        this.fileName = fileName;
        this.type = type;
    }

    // 연관관계 편의 메소드
    public void setConference(Conference conference){
        // 기본 연관관계 정리
        if(this.conference != null){
            this.conference.getMeetingFiles().remove(this);
        }
        // 양방향 연관관계를 맺는다
        this.conference = conference;
        conference.getMeetingFiles().add(this);
    }

}
