package com.mefi.backend.api.controller;

import com.mefi.backend.api.request.TeamModifyReqDto;
import com.mefi.backend.api.request.TeamReqDto;
import com.mefi.backend.api.response.MemberResDto;
import com.mefi.backend.api.response.TeamDetailDto;
import com.mefi.backend.api.response.TeamResDto;
import com.mefi.backend.api.service.TeamService;
import com.mefi.backend.common.auth.CustomUserDetails;
import com.mefi.backend.common.model.BaseResponseBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/team")
@RequiredArgsConstructor
@Slf4j
@Tag(name="2.TEAM", description = "TEAM API")
public class TeamController {

    private final TeamService teamService;

    @PostMapping("")
    @Operation(summary = "팀생성", description = "팀 정보를 받아 팀 생성한다.")
    @ApiResponse(responseCode = "200", description = "성공 \n\n team 식별 아이디 반환")
    public ResponseEntity<? extends BaseResponseBody> createTeam(Authentication authentication, @RequestBody @Valid TeamReqDto teamReqDto){

        // 현재 사용자 식별 ID 불러옴
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

        log.info("UserID :{}", user.getUserId());
        log.info("teamName :{}", teamReqDto.getTeamName());
        log.info("teamDescription :{}", teamReqDto.getTeamDescription());

        // 팀 생성 로직 호출
        teamService.createTeam(user.getUserId(), teamReqDto);

        // 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponseBody.of(0,"Success"));
    }

    @GetMapping("")
    @Operation(summary = "팀 목록 조회", description = "사용자가 속한 팀 목록을 조회한다.")
    @ApiResponse(responseCode = "200", description = "성공 \n\n 사용자가 속한 팀 목록 반환")
    public ResponseEntity<? extends BaseResponseBody> getTeamList(Authentication authentication){

        // 현재 사용자 식별 ID 가져옴
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

        // 파라미터 확인 로그
        log.info("getTeamList UserID :{}", user.getUserId());

        // 사용자 ID로 자신이 속한 팀 목록 조회 로직 호출
        List<TeamResDto> teamList = teamService.getTeamList(user.getUserId());

        // 사용자가 속한 팀 목록 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponseBody.of(0,teamList));
    }

    @GetMapping("/{teamId}")
    @Operation(summary = "팀원 목록 조회", description = "해당 팀의 팀원을 조회한다.")
    @ApiResponse(responseCode = "200", description = "성공 \n\n 팀의 팀원 리스트 반환")
    public ResponseEntity<? extends BaseResponseBody> getMemberList(Authentication authentication, @PathVariable(name = "teamId") Long teamId){

        // 현재 사용자 식별 ID 가져옴
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

        // 파라미터 확인 로그
        log.info("userId : {}", user.getUserId());
        log.info("teamId : {}", teamId);

        // 팀 ID로 자신이 속한 팀원 목록 조회 로직
        List<MemberResDto> memberList = teamService.getMemberList(user.getUserId(), teamId);

        // 현재 팀의 팀원 목록 반환
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponseBody.of(0, memberList));
    }

    @PostMapping("/{teamId}/{memberId}")
    @Operation(summary = "팀원 추가", description = "팀장이 속한 팀의 해당 유저를 추가한다.")
    @ApiResponse(responseCode = "200", description = "성공 \n\n 반환값 없음")
    public ResponseEntity<? extends BaseResponseBody> addMember(Authentication authentication,
                                                                @PathVariable(name = "teamId") Long teamId,
                                                                @PathVariable(name = "memberId") Long memberId){

        // 현재 사용자 식별 ID 가져옴
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

        // 팀원 추가
        teamService.addMember(user.getUserId(), teamId, memberId);

        // 현재 팀의 팀원 목록 반환
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponseBody.of(0, "Success"));
    }

    @DeleteMapping("/{teamId}")
    @Operation(summary = "팀 삭제", description = "팀을 삭제한다")
    @ApiResponse(responseCode = "200", description = "성공 \n\n 팀의 팀원 리스트 반환")
    public ResponseEntity<? extends BaseResponseBody> deleteTeam(Authentication authentication,
                                                                 @PathVariable(name = "teamId") Long teamId){

        // 현재 사용자 식별 ID 가져옴
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

        // 파라미터 확인 로그
        log.info("userId : {}", user.getUserId());
        log.info("teamId : {}", teamId);

        // 팀 삭제
        teamService.deleteTeam(user.getUserId(), teamId);

        // 현재 팀의 팀원 목록 반환
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponseBody.of(0, "Success"));
    }

    @DeleteMapping("/{teamId}/{memberId}")
    @Operation(summary = "팀원 삭제", description = "팀장이 해당 유저를 삭제한다.")
    @ApiResponse(responseCode = "200", description = "성공 \n\n 반환값 없음")
    public ResponseEntity<? extends BaseResponseBody> deleteMember(Authentication authentication,
                                                                   @PathVariable(name = "teamId") Long teamId,
                                                                   @PathVariable(name = "memberId") Long memberId){

        // 현재 사용자 식별 ID 가져옴
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

        log.info("userId : {}", user.getUserId());

        // 팀원 추가
        teamService.deleteMember(user.getUserId(), teamId, memberId);

        // 현재 팀의 팀원 목록 반환
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponseBody.of(0, "Success"));
    }

    @GetMapping("/detail/{teamId}")
    @Operation(summary = "팀 상세 조회", description = "팀원이 속한 팀의 상세 정보를 본다")
    @ApiResponse(responseCode = "200", description = "성공 \n\n 팀 상세 정보 반환")
    public ResponseEntity<? extends BaseResponseBody> getTeamDetail(Authentication authentication,
                                                                    @PathVariable Long teamId){

        // 현재 사용자 식별 ID 가져옴
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

        // 사용자 ID로 자신이 속한 팀 목록 조회 로직 호출
        TeamDetailDto teamDetailDto = teamService.getTeamDetail(teamId);

        // 사용자가 속한 팀 목록 반환
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponseBody.of(0,teamDetailDto));
    }

    @PatchMapping("/{teamId}")
    @Operation(summary = "팀 정보 수정", description = "팀장이 해당 팀의 정보를 수정한다.")
    @ApiResponse(responseCode = "200", description = "성공 \n\n 반환값 없음")
    public ResponseEntity<? extends BaseResponseBody> modifyTeam(Authentication authentication,
                                                                 @PathVariable(name = "teamId") Long teamId,
                                                                 @RequestBody TeamModifyReqDto teamModifyReqDto){

        // 현재 사용자 식별 ID 가져옴
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

        // 파라미터 확인 로그
        log.info("userId : {}", user.getUserId());
        log.info("teamId : {}", teamId);
        log.info("teamName : {}", teamModifyReqDto.getName());
        log.info("teamDescription : {}", teamModifyReqDto.getDescription());

        // 팀 삭제
        teamService.modifyTeam(user.getUserId(), teamId, teamModifyReqDto);

        // 현재 팀의 팀원 목록 반환
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponseBody.of(0, "Success"));
    }

    @PatchMapping("/{teamId}/{memberId}")
    @Operation(summary = "리더 권한 변경", description = "팀장이 다른 팀원에게 리더를 양도한다.")
    @ApiResponse(responseCode = "200", description = "성공 \n\n 반환 없음")
    public ResponseEntity<? extends BaseResponseBody> modifyUserRole(Authentication authentication,
                                                                     @PathVariable Long teamId,
                                                                     @PathVariable Long memberId){

        // 현재 사용자 식별 ID 가져옴
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

        // 리더 권한 변경
        teamService.modifyUserRole(user.getUserId(), teamId, memberId);

        return ResponseEntity.status(HttpStatus.OK).body(BaseResponseBody.of(0,"Success"));
    }
}
