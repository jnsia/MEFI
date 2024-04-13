package com.mefi.backend.db.repository;

import com.mefi.backend.api.response.ScheduleTimeDto;
import com.mefi.backend.db.entity.PrivateSchedule;
import com.mefi.backend.db.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<PrivateSchedule, Long> {

    Optional<PrivateSchedule> findById(Long scheduleId);

    List<PrivateSchedule> findByUserAndStartedTimeBetweenOrderByStartedTime(User user, LocalDateTime start, LocalDateTime end);

    @Query("SELECT new com.mefi.backend.api.response.ScheduleTimeDto(s.startedTime, s.endTime) " +
            "FROM PrivateSchedule s " +
            "WHERE s.user.id IN :userIds " +
            "AND FUNCTION('YEAR', s.startedTime) = FUNCTION('YEAR', :date) " +
            "AND FUNCTION('MONTH', s.startedTime) = FUNCTION('MONTH', :date) " +
            "AND FUNCTION('DAY', s.startedTime) = FUNCTION('DAY', :date) " +
            "ORDER BY s.startedTime")
    List<ScheduleTimeDto> findAllMemberSchedule(@Param("userIds") List<Long> members, @Param("date") LocalDateTime date);

    @Query("SELECT s FROM PrivateSchedule s " +
            "WHERE s.user.id = :userId " + // 해당 사용자의 일정만 확인
            "AND ((s.startedTime < :endTime AND s.endTime > :startTime) " + // 새 일정이 기존 일정과 부분적으로 겹치는 경우
            "OR (s.startedTime <= :startTime AND s.endTime >= :endTime) " + // 새 일정이 기존 일정을 완전히 포함하는 경우
            "OR (s.startedTime >= :startTime AND s.startedTime < :endTime) " + // 새 일정이 기존 일정의 시작 시간과 종료 시간 사이에 있는 경우
            "OR (s.endTime > :startTime AND s.endTime <= :endTime))") // 새 일정이 기존 일정의 시작 시간과 종료 시간 사이에 있는 경우

    List<PrivateSchedule> findDuplicationByUserAndTime(@Param("userId") Long userId, @Param("startTime") LocalDateTime start, @Param("endTime") LocalDateTime end);

    @Query("SELECT p FROM PrivateSchedule p WHERE p.user = :user and p.startedTime = :start and p.endTime = :end")
    Optional<PrivateSchedule> getSchedule(@Param("user") User user, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
