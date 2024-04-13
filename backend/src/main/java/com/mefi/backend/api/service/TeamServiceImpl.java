package com.mefi.backend.api.service;

import com.mefi.backend.api.request.TeamModifyReqDto;
import com.mefi.backend.api.request.TeamReqDto;
import com.mefi.backend.api.response.MemberResDto;
import com.mefi.backend.api.response.TeamDetailDto;
import com.mefi.backend.api.response.TeamResDto;
import com.mefi.backend.common.exception.ErrorCode;
import com.mefi.backend.common.exception.Exceptions;
import com.mefi.backend.db.entity.Team;
import com.mefi.backend.db.entity.User;
import com.mefi.backend.db.entity.UserRole;
import com.mefi.backend.db.entity.UserTeam;
import com.mefi.backend.db.repository.TeamRepository;
import com.mefi.backend.db.repository.TeamUserRepository;
import com.mefi.backend.db.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService{

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final TeamUserRepository teamUserRepository;
    private final NotiService notiService;

    @Override
    @Transactional
    public void createTeam(Long leaderId, TeamReqDto teamReqDto){
        // 팀 생성
        Team team = Team.builder()
                .name(teamReqDto.getTeamName())
                .description(teamReqDto.getTeamDescription())
                .time(LocalDateTime.now())
                .build();

        // 리더 추가
        User leader = userRepository.findById(leaderId).orElseThrow(() -> new Exceptions(ErrorCode.USER_NOT_EXIST));
        UserTeam teamLeader = UserTeam.builder()
                .user(leader)
                .team(team)
                .role(UserRole.LEADER)
                .build();
        team.addUserTeam(teamLeader);

        // 멤버 추가
        List<Long> memberIds = teamReqDto.getMembers();
        for (Long memberId : memberIds) {
            User member = userRepository.findById(memberId).orElseThrow(() -> new Exceptions(ErrorCode.USER_NOT_EXIST));
            UserTeam teamMember = UserTeam.builder()
                    .user(member)
                    .team(team)
                    .role(UserRole.MEMBER)
                    .build();
            team.addUserTeam(teamMember);
        }

        // 팀 저장
        Team newTeam = teamRepository.save(team);

        // 팀원 모두에게 알림 전송
        String sender = makeSender(newTeam.getName(), leader.getName());
        String message =  makeMessage(leader.getName(), newTeam.getName(),1);
        notiService.sendNotiForTeam(newTeam.getId(), sender,message);
        log.info("알림 전송 완료 : {}", message);
    }

    // 팀 목록 조회 서비스
    @Override
    public List<TeamResDto> getTeamList(Long userId) {

        return teamUserRepository.findTeamsByUserId(userId);
    }

    // 팀원 목록 조회
    @Override
    public List<MemberResDto> getMemberList(Long userId, Long teamId) {
        // 해당 팀에 현재 로그인한 유저가 속해있는지 확인 -> 만약 0으로 반환되면 속해있지 않음
        Long count = teamUserRepository.isMember(userId, teamId);

        // 예외처리 : 만약 해당 팀의 멤버가 아니라면... 예외 발생!!
        if(count == 0L){
            log.info("현재 로그인된 사용자는 해당 팀의 멤버가 아닙니다.");
            return null;
        }

        // 팀 구성원 목록 반환
        return teamUserRepository.getMemberList(teamId);
    }

    /**
     * @return 리더가 아니면 예외 처리
     */
    @Override
    public Boolean checkRole(Long userId, Long teamId) {
        log.info("checkRole()");

        UserTeam userTeam  = teamUserRepository.findByUserIdAndTeamId(userId, teamId).orElseThrow(() -> new Exceptions(ErrorCode.TEAM_ACCESS_DENIED));

        log.info("해당 유저의 권한 : {}", userTeam.getRole());

        if(userTeam.getRole() != UserRole.LEADER){
            throw new Exceptions(ErrorCode.NOT_TEAM_LEADER);
        }

        return true;
    }

    @Override
    @Transactional
    public void addMember(Long userId, Long teamId, Long memberId) {

        if(checkRole(userId, teamId)){
            User member = userRepository.findById(memberId).orElseThrow(() -> new Exceptions(ErrorCode.USER_NOT_EXIST));
            Team team = teamRepository.findById(teamId).orElseThrow(() -> new Exceptions(ErrorCode.TEAM_NOT_EXIST));

            UserTeam teamMember = UserTeam.builder()
                    .user(member)
                    .team(team)
                    .role(UserRole.MEMBER)
                    .build();

            teamUserRepository.save(teamMember);

            // 팀원에게 알림 전송
            User leader = userRepository.findById(userId).orElseThrow(() -> new Exceptions(ErrorCode.MEMBER_NOT_EXIST));
            String sender = makeSender(team.getName(), leader.getName());
            String message =  makeMessage(leader.getName(), team.getName(), 1);
            notiService.sendNotiForUser(memberId, sender, message);

        }else{
            throw new Exceptions(ErrorCode.NOT_TEAM_LEADER);
        }
    }

    @Override
    @Transactional
    public void deleteTeam(Long userId, Long teamId) {
        Optional<Team> optionalTeam = teamRepository.findById(teamId);
        Team team = optionalTeam.orElseThrow(() -> new Exceptions(ErrorCode.TEAM_NOT_EXIST));

        // 팀장 권환 확인
        if(!checkRole(userId, teamId)) throw new Exceptions(ErrorCode.NOT_TEAM_LEADER);

        // 팀 멤버 전원 삭제
        teamUserRepository.deleteByTeamId(teamId);

        // 팀 삭제
        teamRepository.delete(team);


    }

    @Override
    @Transactional
    public void deleteMember(Long userId, Long teamId, Long memberId) {
        log.info("deleteMember()");
        // 해당 팀의 리더가 아니라면 예외 발생
        checkRole(userId, teamId);

        // 만약 리더를 삭제하려 한다면 예외 발생
        UserTeam userTeam  = teamUserRepository.findByUserIdAndTeamId(memberId, teamId).orElseThrow(() -> new Exceptions(ErrorCode.MEMBER_NOT_EXIST));
        if(userTeam.getRole() == UserRole.LEADER){throw new Exceptions(ErrorCode.LEADER_NOT_DELETEABLE);}

        // 삭제하려는 멤버가 해당 팀에 없다면 예외 발생
        teamUserRepository.findByUserIdAndTeamId(memberId, teamId).orElseThrow(() -> new Exceptions(ErrorCode.TEAM_ACCESS_DENIED));

        // 멤버 삭제
        teamUserRepository.deleteByUserIdAndTeamId(memberId, teamId);

        // 삭제된 팀원에게 알림 전송
        User leader = userRepository.findById(userId).orElseThrow(() -> new Exceptions(ErrorCode.MEMBER_NOT_EXIST));
        String sender = makeSender(userTeam.getTeam().getName(), leader.getName());
        String message = makeMessage(leader.getName(), userTeam.getTeam().getName(), -1);
        notiService.sendNotiForUser(memberId, sender, message);
    }

    @Override
    public TeamDetailDto getTeamDetail(Long teamId) {
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new Exceptions(ErrorCode.TEAM_NOT_EXIST));

        return new TeamDetailDto(team.getId(), team.getName(), team.getDescription(), team.getCreatedTime());
    }

    @Override
    @Transactional
    public void modifyTeam(Long userId, Long teamId, TeamModifyReqDto teamModifyReqDto) {

        // 팀 엔티티 DB에 불러옴 -> 해당 팀이 없다면 예외 처리
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new Exceptions(ErrorCode.TEAM_NOT_EXIST));

        // 해당 팀의 리더가 아니라면 예외 발생
        checkRole(userId, teamId);

        // 엔티티 수정
        team.changeTeamName(teamModifyReqDto.getName());
        team.changeTeamDescription(teamModifyReqDto.getDescription());
    }

    @Override
    @Transactional
    public void modifyUserRole(Long userId, Long teamId, Long memberId) {
        // 리더가 아니면 예외 처리
        checkRole(userId, teamId);

        // 받은 유저가 팀의 멤버가 아니면 예외 처리
        UserTeam leader = teamUserRepository.findByUserIdAndTeamId(userId, teamId).orElseThrow(() -> new Exceptions(ErrorCode.TEAM_ACCESS_DENIED));
        UserTeam member = teamUserRepository.findByUserIdAndTeamId(memberId,teamId).orElseThrow(() -> new Exceptions(ErrorCode.TEAM_ACCESS_DENIED));

        leader.changeRole(UserRole.MEMBER);
        member.changeRole(UserRole.LEADER);
    }

    public String makeSender(String teamName, String leaderName){
        return teamName + " " + leaderName;
    }

    public String makeMessage(String sender, String teamName, int type){
        if(type==1)
            return String.format("%s님이 당신을 팀 %s에 초대하였습니다.", sender, teamName);
        return String.format("%s님이 당신을 팀 %s에서 제외하였습니다.", sender, teamName);
    }
}
