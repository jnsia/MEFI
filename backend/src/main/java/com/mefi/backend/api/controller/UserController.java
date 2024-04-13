package com.mefi.backend.api.controller;

import com.mefi.backend.api.request.*;
import com.mefi.backend.api.response.MemberResDto;
import com.mefi.backend.api.response.UserModifyAllResDto;
import com.mefi.backend.api.service.MailServiceImpl;
import com.mefi.backend.api.service.TokenService;
import com.mefi.backend.api.service.UserService;
import com.mefi.backend.common.auth.CustomUserDetails;
import com.mefi.backend.common.model.BaseResponseBody;
import com.mefi.backend.db.entity.Token;
import com.mefi.backend.db.entity.User;
import com.mefi.backend.db.repository.NotiRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
@Tag(name="1.USER", description="USER API")
public class UserController {

    private final UserService userService;
    private final TokenService tokenService;
    private final MailServiceImpl mailService;
    private final NotiRepository notiRepository;

    @Operation(summary = "회원가입", description = "/users\n\n 사용자의 정보를 통해 회원가입 한다.")
    @PostMapping("")
    @ApiResponse(responseCode = "201", description = "성공 \n\n Success 반환")
    public ResponseEntity<? extends BaseResponseBody> join(@Valid @RequestBody JoinReqDto joinReqDto) {

        // 회원가입
        userService.join(joinReqDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponseBody.of(0, "Success"));
    }

    @Operation(summary = "회원탈퇴", description = "/users\n\n 사용자는 회원탈퇴를 한다.")
    @DeleteMapping("")
    @ApiResponse(responseCode = "200", description = "성공 \n\n Success 반환")
    public ResponseEntity<? extends BaseResponseBody> withdraw(Authentication authentication,
                                                               @Valid @RequestBody UserWithdrawReqDto userWithdrawReqDto) {

        // 로그인된 유저 정보 조회
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // 유저 식별 ID로 유저 정보 조회
        User user = userService.findById(userDetails.getUserId());

        // 유저 제거
        userService.withdraw(user, userWithdrawReqDto);
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponseBody.of(0, "Success"));
    }

    @Operation(summary = "로그아웃", description = "/users/logout\n\n 사용자는 로그아웃을 한다.")
    @PostMapping("/logout")
    @ApiResponse(responseCode = "200", description = "성공 \n\n Success 반환")
    public ResponseEntity<? extends BaseResponseBody> logout(Authentication authentication) {

        // 로그인된 유저 정보 조회
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // 식별 ID로 리프레시 토큰 제거
        Token token = tokenService.findByUserId(userDetails.getUserId());
        tokenService.deleteRefreshToken(token);

        // 해당 유저의 SseEmitter 모두 제거
        notiRepository.deleteEmittersByUserId(String.valueOf(userDetails.getUserId()));
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponseBody.of(0, "Success"));
    }

    @Operation(summary = "회원가입을 위한 이메일 인증", description = "/users/join/auth\n\n 사용자는 회원가입을 위해 이메일 인증을 한다.")
    @PostMapping("/join/auth")
    @ApiResponse(responseCode = "200", description = "성공 \n\n Success 반환")
    public ResponseEntity<? extends BaseResponseBody> verifyJoinEmail(@Valid @RequestBody VerifyEmailReqDto verifyEmailReqDto) throws Exception {

        // 메일 전송 후 코드 받기
        mailService.sendJoinMessage(verifyEmailReqDto.getEmail());
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponseBody.of(0, "Success"));
    }

    @Operation(summary = "비밀번호 찾기를 위한 이메일 인증", description = "/users/pwd/auth\n\n 사용자는 비밀번호 찾기를 위해 이메일 인증을 한다.")
    @PostMapping("/pwd/auth")
    @ApiResponse(responseCode = "200", description = "성공 \n\n Success 반환")
    public ResponseEntity<? extends BaseResponseBody> verifyPasswordRecoveryEmail(@Valid @RequestBody VerifyEmailReqDto verifyEmailReqDto) throws Exception {

        // 메일 전송 후 코드 받기
        mailService.sendPasswordRecoveryMessage(verifyEmailReqDto.getEmail());
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponseBody.of(0, "Success"));
    }

    @Operation(summary = "이메일 인증 확인", description = "/users/auth/check\n\n 사용자는 이메일 인증 확인을 한다.")
    @PostMapping("/auth/check")
    @ApiResponse(responseCode = "200", description = "성공 \n\n Success 반환")
    public ResponseEntity<? extends BaseResponseBody> verifyEmailCode(@Valid @RequestBody VerifyCodeReqDto verifyCodeReqDto) {

        // 인증 코드 확인 후 토큰 반환 (주석 이유 : 추가 로직 필요!)
        // String token = mailService.validateAuthCode(verifyCodeReqDto);
        mailService.validateAuthCode(verifyCodeReqDto);
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponseBody.of(0, "Success"));
    }

    @Operation(summary = "회원 검색", description = "/users/search/{keyword} \n\n 사용자는 회원을 검색한다.")
    @GetMapping("/search/{keyword}")
    @ApiResponse(responseCode = "200", description = "성공 \n\n 검색 결과 유저 리스트 반환")
    public ResponseEntity<? extends BaseResponseBody> searchUsers(
            Authentication authentication,
            @Parameter(name = "keyword", description = "검색 키워드")
            @Valid @NotBlank @PathVariable("keyword") String keyword) {

        // 로그인된 유저 정보 조회
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // 검색어로 회원 조회
        List<MemberResDto> searchResultList = userService.getSearchUsers(userDetails.getUserId(),keyword);
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponseBody.of(0, searchResultList));
    }

    @Operation(summary = "회원 정보 전체 수정", description = "/users/info \n\n 사용자는 자신의 정보를 전체 수정한다.")
    @PutMapping("/info")
    @ApiResponse(responseCode = "200", description = "성공 \n\n Success 반환")
    public ResponseEntity<? extends BaseResponseBody> modifyUserInfoAll(Authentication authentication,
                        @RequestPart(value="profileImg", required = false) MultipartFile profileImg,
                        @RequestPart(value="userModifyAllReqDto") @Valid UserModifyAllReqDto userModifyAllReqDto) throws IOException {

        // 로그인된 유저 정보 조회
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // 회원 정보 전체 수정
        UserModifyAllResDto userModifyAllResDto = userService.modifyUserInfoAll(userDetails.getUserId(),userModifyAllReqDto, profileImg);
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponseBody.of(0, userModifyAllResDto));
    }
    
    @Operation(summary = "회원 정보 부분 수정", description = "/users/info \n\n 사용자는 자신의 정보를 부분적으로 수정한다.")
    @PatchMapping("/info")
    @ApiResponse(responseCode = "200", description = "성공 \n\n Success 반환")
    public ResponseEntity<? extends BaseResponseBody> modifyUserInfo(Authentication authentication,
                                                                     @Valid @RequestBody UserModifyReqDto userModifyReqDto) {

        // 로그인된 유저 정보 조회
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // 회원 정보 부분 수정
        userService.modifyUserInfo(userDetails.getUserId(),userModifyReqDto);
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponseBody.of(0, "Success"));
    }

    @Operation(summary = "기본 비밀번호 수정", description = "/users/pwd \n\n 사용자는 마이페이지에서 비밀번호를 수정한다.")
    @PatchMapping("/pwd")
    @ApiResponse(responseCode = "200", description = "성공 \n\n Success 반환")
    ResponseEntity<? extends BaseResponseBody> modifyUserPassword(Authentication authentication,
                                                                  @Valid @RequestBody UserModifyPasswordReqDto userModifyPasswordReqDto) {
        // 로그인된 유저 정보 조회
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // 회원 비밀번호 수정
        userService.modifyUserPassword(userDetails.getUserId(),userModifyPasswordReqDto);
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponseBody.of(0, "Success"));
    }

    @Operation(summary = "찾기 인증 후 비밀번호 수정", description = "/users/pwd/recovery \n\n 사용자는 비밀번호 찾기 인증 후 비밀번호를 수정한다.")
    @PatchMapping("/pwd/recovery")
    @ApiResponse(responseCode = "200", description = "성공 \n\n Success 반환")
    ResponseEntity<? extends BaseResponseBody> recoveryUserPassword(@Valid @RequestBody UserPasswordRecoveryReqDto userPasswordRecoveryReqDto) {

        // 회원 비밀번호 수정
        userService.recoveryUserPassword(userPasswordRecoveryReqDto);
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponseBody.of(0, "Success"));
    }
}