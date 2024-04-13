package com.mefi.backend.api.service;

import com.mefi.backend.api.request.ScheduleReqDto;
import com.mefi.backend.api.response.ScheduleCalResDto;
import com.mefi.backend.api.response.ScheduleDetailResDto;
import com.mefi.backend.api.response.ScheduleResDto;
import com.mefi.backend.api.response.ScheduleTimeDto;
import com.mefi.backend.common.exception.ErrorCode;
import com.mefi.backend.common.exception.Exceptions;
import com.mefi.backend.db.entity.*;
import com.mefi.backend.db.repository.ScheduleRepository;
import com.mefi.backend.db.repository.TeamUserRepository;
import com.mefi.backend.db.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ScheduleServiceImpl implements  ScheduleService{

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final TeamUserRepository teamUserRepository;

    @Override
    @Transactional
    public void createSchedule(Long userId, ScheduleReqDto scheduleReqDto) {
        // 유저 조회, 해당 회원이 존재하지 않으면 예외 처리
        User user = userRepository.findById(userId).orElseThrow(()->new Exceptions(ErrorCode.MEMBER_NOT_EXIST));

        // 시간이 겹치는 일정이 존재하면 예외 처리
        checkDuplicateSchedule(userId, -1L, scheduleReqDto.getStartedTime(), scheduleReqDto.getEndTime());

        // 일정 등록
        PrivateSchedule privateSchedule = PrivateSchedule.builder()
                .user(user)
                .startedTime(scheduleReqDto.getStartedTime())
                .endTime(scheduleReqDto.getEndTime())
                .summary(scheduleReqDto.getSummary())
                .description(scheduleReqDto.getDescription())
                .type(scheduleReqDto.getType())
                .build();

        // 일정 등록
        scheduleRepository.save(privateSchedule);
    }

    @Override
    @Transactional
    public ScheduleResDto deleteSchedule(Long userId, Long alarmId) {
        // 삭제하려는 일정 조회, 존재하지 않는다면 예외 발생
        PrivateSchedule schedule = scheduleRepository.findById(alarmId).orElseThrow(()->new Exceptions(ErrorCode.SCHEDULE_NOT_EXIST));

        // 일정을 등록한 유저가 아니라면 예외 발생
        if(userId != schedule.getUser().getId()){
            throw new Exceptions(ErrorCode.SCHEDULE_ACCESS_DENIED);
        }

        // 일정 삭제
        scheduleRepository.delete(schedule);
        return new ScheduleResDto(schedule);
    }

    @Override
    public List<ScheduleCalResDto> getPrivateSchedule(Long userId, LocalDateTime start, LocalDateTime end) {

        User user = userRepository.findById(userId).orElseThrow(() -> new Exceptions(ErrorCode.USER_NOT_EXIST));

        List<PrivateSchedule> result = scheduleRepository.findByUserAndStartedTimeBetweenOrderByStartedTime(user, start, end);

        List<ScheduleCalResDto> list = new ArrayList<>();

        for (PrivateSchedule ps : result) {
            list.add(new ScheduleCalResDto(ps.getId(), ps.getSummary(), ps.getStartedTime() ,ps.getEndTime(), ps.getType()));
        }

        return list;
    }

    @Override
    public List<ScheduleTimeDto> getAllMemberSchedule(Long userId, Long teamId, String day) {

        log.info("=======================ScheduleService-getAllMemberSchedule()=======================");

        // 팀원이 아닌 경우 예외 처리
        teamUserRepository.findByUserIdAndTeamId(userId, teamId).orElseThrow(() -> new Exceptions(ErrorCode.TEAM_ACCESS_DENIED));

        // 팀원들의 PK 값 조회 및 로깅
        List<Long> memberIds = teamUserRepository.findByUserId(teamId);
        log.info("memberIds : {}", memberIds);

        // 조회 일자 파싱 및 로깅
        LocalDateTime date = LocalDateTime.parse(day + "000000.000", DateTimeFormatter.ofPattern("yyyyMMddHHmmss.SSS"));
        log.info("date {}", date);

        // 팀원들 일정 조회
        return scheduleRepository.findAllMemberSchedule(memberIds, date);
    }

    @Override
    @Transactional
    public void modifySchedule(Long userId, ScheduleReqDto scheduleReqDto, Long scheduleId) {
        // 삭제하려는 일정 조회, 존재하지 않는다면 예외 발생
        PrivateSchedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(()->new Exceptions(ErrorCode.SCHEDULE_NOT_EXIST));

        // 일정을 등록한 유저가 아니라면 예외 발생
        if(userId != schedule.getUser().getId()){
            throw new Exceptions(ErrorCode.SCHEDULE_ACCESS_DENIED);
        }

        // 시간이 겹치는 일정이 존재하면 예외 처리
        checkDuplicateSchedule(userId, schedule.getId(), scheduleReqDto.getStartedTime(), scheduleReqDto.getEndTime());

        // 만약 수정하려는 일정 타입이 회의라면 예외 발생
        if(schedule.getType() == ScheduleType.CONFERENCE){
            throw new Exceptions(ErrorCode.CONFERENCE_NOT_MODIFY);
        }

        schedule.changeDetail(scheduleReqDto.getSummary(), scheduleReqDto.getDescription(), scheduleReqDto.getStartedTime(), scheduleReqDto.getEndTime());
    }

    // 개인 일정 상세 조회
    @Override
    public ScheduleDetailResDto getPrivateScheduleDetail(Long userId, Long scheduleId) {

        log.info("=======================ScheduleService-getPrivateScheduleDetail()=======================");

        // 해당 일정이 존재하지 않는다면 예외 처리
        PrivateSchedule ps = scheduleRepository.findById(scheduleId).orElseThrow(() -> new Exceptions(ErrorCode.SCHEDULE_NOT_EXIST));

        // 해당 일정의 현재 사용자의 일정이 아니라면 예외 처리
        if(ps.getUser().getId() == userId) new Exceptions(ErrorCode.SCHEDULE_ACCESS_DENIED);

        // 일정 상세 DTO 반환
        return new ScheduleDetailResDto(ps.getId(), ps.getSummary(), ps.getDescription(), ps.getStartedTime(), ps.getEndTime(), ps.getType());
    }

    // 해당 유저의 일정 중에서 중복되는 일정 체크
    public boolean checkDuplicateSchedule(Long userId, Long scheduleId, LocalDateTime start, LocalDateTime end){
        // 유저 스케줄 조회
        List<PrivateSchedule> schedules = scheduleRepository.findDuplicationByUserAndTime(userId, start, end);

        // 중복되는 일정이 있을 경우, 확인 후 예외 처리
        int count = schedules.size();
        if(count>1) {
            throw new Exceptions(ErrorCode.SCHEDULE_DUPLICATED); // 2개 이상의 일정과 겹치는 경우
        }
        if(count==1) {
            if(schedules.get(0).getId()!=scheduleId){
                throw new Exceptions(ErrorCode.SCHEDULE_DUPLICATED); // 다른 일정과 겹치는 경우
            }
        }
        return true;
    }
}
