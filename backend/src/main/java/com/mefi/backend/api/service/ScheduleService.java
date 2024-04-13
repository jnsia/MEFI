package com.mefi.backend.api.service;

import com.mefi.backend.api.request.ScheduleReqDto;
import com.mefi.backend.api.response.ScheduleCalResDto;
import com.mefi.backend.api.response.ScheduleDetailResDto;
import com.mefi.backend.api.response.ScheduleResDto;
import com.mefi.backend.api.response.ScheduleTimeDto;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleService {

    // 개인 일정 등록
    void createSchedule(Long userId, ScheduleReqDto scheduleReqDto);

    // 개인 일정 삭제
    ScheduleResDto deleteSchedule(Long userId, Long alarmId);

    List<ScheduleCalResDto> getPrivateSchedule(Long userId, LocalDateTime start, LocalDateTime end);

    // 해당 일자 전체 일정 조회
    List<ScheduleTimeDto> getAllMemberSchedule(Long userId, Long teamId, String day);

    // 개인 일정 수정
    void modifySchedule(Long userId, ScheduleReqDto scheduleReqDto, Long scheduleId);

    // 개인 일정 상세 조회
    ScheduleDetailResDto getPrivateScheduleDetail(Long userId, Long scheduleId);

    // 해당 유저의 일정 중에서 중복되는 일정 체크
    boolean checkDuplicateSchedule(Long id, Long id1, LocalDateTime callStart, LocalDateTime callEnd);
}
