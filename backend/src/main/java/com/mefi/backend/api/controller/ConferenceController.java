package com.mefi.backend.api.controller;

import com.mefi.backend.api.request.ConferenceCreateReqDto;
import com.mefi.backend.api.request.ConferenceModifyAllReqDto;
import com.mefi.backend.api.response.ConferenceResDto;
import com.mefi.backend.api.response.ConferenceDetailResDto;
import com.mefi.backend.api.service.ConferenceService;
import com.mefi.backend.common.auth.CustomUserDetails;
import com.mefi.backend.common.model.BaseResponseBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/meeting")
@Validated
@Tag(name="3.CONFERENCE", description="CONFERENCE API")
public class ConferenceController {

    private final ConferenceService conferenceService;

    @Operation(summary = "회의 생성", description = "api/meeting/\n\n 리더는 회의를 생성 가능하다.")
    @PostMapping("")
    @ApiResponse(responseCode = "201", description = "성공 \n\n Success 반환")
    public ResponseEntity<? extends BaseResponseBody> createMeeting(Authentication authentication,
                                                                    @Valid @RequestBody ConferenceCreateReqDto conferenceCreateReqDto) {
        // 로그인된 유저 정보 조회
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        log.info("\n회의 생성 URL 맵핑: {}", conferenceCreateReqDto.getTitle());
        log.info("\n회의 생성자 : {}", userDetails.getUserId());

        // 회의 생성
        Long conferenceId = conferenceService.createMeeting(userDetails.getUserId(), conferenceCreateReqDto);

        log.info("회의 생성 반환 값 확인 : {}", conferenceId);

        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponseBody.of(0, conferenceId));
    }

    @GetMapping("/{teamId}")
    @Operation(summary = "팀 회의 이력 조회 API", description = "주어진 기간동안의 팀 회의 이력을 반환한다")
    @ApiResponse(responseCode = "200", description = "성공 시 상태 코드 200와 회의 이력 리스트 반환")
    public ResponseEntity<? extends BaseResponseBody> getConferenceHistory(
            Authentication authentication,
            @PathVariable("teamId") Long teamId,
            @RequestParam("start") String start,
            @RequestParam("end") String end
    ){

        // 로그인된 유저 정보 조회
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

        // 팀 회의 이력 조회
        List<ConferenceResDto> histories = conferenceService.getConferenceHistory(user.getUserId(), teamId, start, end);
        log.info("조회된 회의 이력 개수 : {}", histories.size());
        
        // 반환
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponseBody.of(0, histories));
    }

    @Operation(summary = "회의 상세 조회", description = "api/meeting/detail/{conferenceId}\n\n 사용자는 자신이 포홤된 회의에 대한 정보를 상세하게 조회 할 수 있다.")
    @GetMapping("/detail/{conferenceId}")
    @ApiResponse(responseCode = "200", description = "성공 \n\n 회의 상세 조회 내용 반환")
    public ResponseEntity<? extends BaseResponseBody> detailMeeting(Authentication authentication,
                                                                    @Parameter(name = "conferenceId", description = "회의 번호")
                                                                    @PathVariable("conferenceId") Long conferenceId) {
        // 로그인된 유저 정보 조회
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        log.info("\n회의 상세 조회 URL 맵핑: {}", conferenceId);
        log.info("\n회의 상세 조회자 : {}", userDetails.getUserId());

        // 회의 상세 조회
        ConferenceDetailResDto conferenceDetailResDto = conferenceService.detailMeeting(userDetails.getUserId(), conferenceId);
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponseBody.of(0,conferenceDetailResDto));
    }

    @Operation(summary = "회의 취소", description = "api/meeting/cancel/{conferenceId}\n\n 리더는 회의 취소가 가능하다.")
    @PatchMapping("/cancel/{conferenceId}")
    @ApiResponse(responseCode = "200", description = "성공 \n\n Success 반환")
    public ResponseEntity<? extends BaseResponseBody> cancelMeeting(Authentication authentication,
                                                                    @Parameter(name = "conferenceId", description = "회의 번호")
                                                                    @PathVariable("conferenceId") Long conferenceId) {
        // 로그인된 유저 정보 조회
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        log.info("\n회의 취소 URL 맵핑: {}", conferenceId);
        log.info("\n회의 취소자 : {}", userDetails.getUserId());

        // 회의 취소
        conferenceService.cancelMeeting(userDetails.getUserId(), conferenceId);
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponseBody.of(0,"Success"));
    }

    @Operation(summary = "회의 종료", description = "api/meeting/done/{conferenceId}\n\n 리더는 회의를 종료 할 수 있다.")
    @PatchMapping("/done/{conferenceId}")
    @ApiResponse(responseCode = "200", description = "성공 \n\n Success 반환")
    public ResponseEntity<? extends BaseResponseBody> doneMeeting(Authentication authentication,
                                                                    @Parameter(name = "conferenceId", description = "회의 번호")
                                                                    @PathVariable("conferenceId") Long conferenceId) {
        // 로그인된 유저 정보 조회
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        log.info("\n회의 종료 URL 맵핑: {}", conferenceId);
        log.info("\n회의 종료자 : {}", userDetails.getUserId());

        // 회의 종료
        conferenceService.doneMeeting(userDetails.getUserId(), conferenceId);
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponseBody.of(0,"Success"));
    }

    @Operation(summary = "회의 정보 전체 수정", description = "api/meeting/modify/{conferenceId}\n\n 리더는 회의 정보를 전체 수정 할 수 있다.")
    @PutMapping("/modify/{conferenceId}")
    @ApiResponse(responseCode = "200", description = "성공 \n\n Success 반환")
    public ResponseEntity<? extends BaseResponseBody> modifyAllMeeting(Authentication authentication,
                                                                  @Parameter(name = "conferenceId", description = "회의 번호")
                                                                  @PathVariable("conferenceId") Long conferenceId,
                                                                    @Valid @RequestBody ConferenceModifyAllReqDto conferenceModifyAllReqDto) {
        // 로그인된 유저 정보 조회
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        log.info("\n회의 전체 수정 URL 맵핑: {}", conferenceId);
        log.info("\n회의 수정자 : {}", userDetails.getUserId());

        // 회의 전체 수정
        conferenceService.modifyAllMeeting(userDetails.getUserId(), conferenceId, conferenceModifyAllReqDto);
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponseBody.of(0,"Success"));
    }
}