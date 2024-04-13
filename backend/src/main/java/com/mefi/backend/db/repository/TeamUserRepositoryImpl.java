package com.mefi.backend.db.repository;

import com.mefi.backend.api.response.MemberResDto;
import com.mefi.backend.api.response.QMemberResDto;
import com.mefi.backend.api.response.QTeamResDto;
import com.mefi.backend.api.response.TeamResDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

import static com.mefi.backend.db.entity.QTeam.team;
import static com.mefi.backend.db.entity.QUser.user;
import static com.mefi.backend.db.entity.QUserTeam.userTeam;

// 사용자 정의 인터페이스 구현
public class TeamUserRepositoryImpl implements TeamUserRepositoryCustom{

    // JPAQueryFactory : Querydsl을 사용하여 JPA에서 타입-세이프한 쿼리를 생성하고 실행하기 위한 클래스
    private final JPAQueryFactory queryFactory;
    // EntityManager 인스턴스를 매개변수로 받아, 이를 사용하여 JPAQueryFactory 인스턴스를 초기화
    public TeamUserRepositoryImpl(EntityManager em){
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<TeamResDto> findTeamsByUserId(Long userId) {
        // Querydsl 반환
        return queryFactory
                .select(new QTeamResDto(team.id, team.name, team.description, userTeam.role))
                .from(userTeam)
                .leftJoin(userTeam.team, team)
                .where(userTeam.user.id.eq(userId))
                .fetch();
    }

    // 사용자가 해당 팀의 구성원인지 확인하는 메서드
    @Override
    public Long isMember(Long userId, Long teamId){
        // Querydsl 반환
        return Optional.ofNullable(queryFactory
                .select(userTeam.count())
                .from(userTeam)
                .where(userTeam.user.id.eq(userId)
                        .and(userTeam.team.id.eq(teamId)))
                .fetchOne()).orElse(0L);
    }

    // 해당 팀원 정보 조회하는 메서드
    @Override
    public List<MemberResDto> getMemberList(Long teamId){
        // Querydsl 반환
        return queryFactory
                .select(new QMemberResDto(user.id, user.email, user.name, user.position, user.dept))
                .from(userTeam)
                .leftJoin(userTeam.user, user)
                .where(userTeam.team.id.eq(teamId))
                .fetch();
    }
}
