package com.mefi.backend.db.repository;

import com.mefi.backend.api.response.MemberResDto;
import com.mefi.backend.api.response.TeamResDto;

import java.util.List;

// 사용자 정의 인터페이스 작성
public interface TeamUserRepositoryCustom {

    // 유저 식별 ID로 사용자가 속한 팀 목록 조회
    List<TeamResDto> findTeamsByUserId(Long userId);

    // 사용자가 해당 팀의 구성원인지 조회
    Long isMember(Long userId, Long teamId);

    // 해당 팀원 목록 조회
    List<MemberResDto> getMemberList(Long teamId);
}
