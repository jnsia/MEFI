package com.mefi.backend.db.repository;

import com.mefi.backend.api.response.ConferenceResDto;
import com.mefi.backend.db.entity.Conference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

import java.util.Optional;

public interface ConferenceRepository extends JpaRepository<Conference, Long> {

    @Query("select c from Conference c inner join c.team t on t.id=:teamId where c.callStart >= :start and c.callEnd <= :end")
    List<ConferenceResDto> findAllByCallTime(@Param("teamId") Long teamId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // 회의 식별 ID로 회의 정보 조회
    Optional<Conference> findById(Long conferencId);

}