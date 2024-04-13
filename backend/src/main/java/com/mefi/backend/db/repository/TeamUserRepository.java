package com.mefi.backend.db.repository;

import com.mefi.backend.db.entity.Team;
import com.mefi.backend.db.entity.UserTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

// 스프링 데이터 리포지토리에 사용자 정의 인터페이스 상속
public interface TeamUserRepository extends JpaRepository<UserTeam, Long>, TeamUserRepositoryCustom {

    @Query("SELECT ut FROM UserTeam ut WHERE ut.user.id = :userId AND ut.team.id = :teamId")
    Optional<UserTeam> findByUserIdAndTeamId(@Param("userId") Long userId,@Param("teamId") Long teamId);

    @Modifying
    @Query("DELETE FROM UserTeam ut WHERE ut.team.id = :teamId")
    void deleteByTeamId(Long teamId);

    // 팀멤버 삭제
    @Modifying
    @Query("DELETE FROM UserTeam ut WHERE ut.user.id = :userId AND ut.team.id = :teamId")
    void deleteByUserIdAndTeamId(Long userId, Long teamId);

    // 멤버 PK 조회
    @Query("SELECT ut.user.id FROM UserTeam ut WHERE  ut.team.id = :teamId")
    List<Long> findByUserId(@Param("teamId") Long teamId);

    List<UserTeam> findAllByTeamId(Long teamId);
}
