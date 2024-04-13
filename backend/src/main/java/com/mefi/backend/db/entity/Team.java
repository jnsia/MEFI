package com.mefi.backend.db.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "team")
@Getter
@NoArgsConstructor
public class Team {
    // 식별ID
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 생성을 DB에 위임하는 전략
    private Long id;
    
    // 팀명
    @NotBlank
    private String name;

    // 팀소개
    private String description;

    // 생성시간
    private LocalDateTime createdTime;


    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    private List<UserTeam> userTeams = new ArrayList<>();

    @OneToMany(mappedBy = "team")
    private List<Conference> conferences = new ArrayList<>();

    @Builder
    public Team(String name, String description, LocalDateTime time){
        this.name = name;
        this.description = description;
        this.createdTime = time; 
    }

    public void addUserTeam(UserTeam teamLeader) {
        this.userTeams.add(teamLeader);
    }

    public void changeTeamName(String name) {
        this.name = name;
    }

    public void changeTeamDescription(String description) {
        this.description = description;
    }
}
