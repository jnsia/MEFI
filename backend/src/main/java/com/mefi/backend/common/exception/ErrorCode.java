package com.mefi.backend.common.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    
    // 에러 열거

    // 회원
    USER_NOT_EXIST(HttpStatus.BAD_REQUEST, "U-001", "존재하지 않는 회원입니다."),
    EMAIL_NOT_EXIST(HttpStatus.BAD_REQUEST, "U-002", "존재하지 않는 이메일입니다."),
    EMAIL_EXIST(HttpStatus.BAD_REQUEST, "U-003", "이미 사용 중인 이메일입니다."),
    CODE_TIME_EXPIRED(HttpStatus.BAD_REQUEST, "U-004", "인증 시간이 초과하였습니다."),
    CODE_NOT_MATCH(HttpStatus.BAD_REQUEST, "U-005", "인증 코드가 일치하지 않습니다."),
    CODE_NOT_EXIST(HttpStatus.BAD_REQUEST, "U-006", "인증 코드가 유효하지 않습니다."),
    SAME_AS_BEFORE(HttpStatus.BAD_REQUEST, "U-007", "이전 정보와 동일합니다."),
    CATEGORY_NOT_EXIST(HttpStatus.BAD_REQUEST, "U-008", "존재하지 않는 항목입니다."),
    DIFFERENT_FROM_BEFORE(HttpStatus.BAD_REQUEST, "U-009", "이전 정보와 다릅니다."),
    CORRECT_NOT_PASSWORD(HttpStatus.BAD_REQUEST, "U-010", "비밀번호가 올바르지 않습니다."),

    // 토큰
    UNEXPECTED_TOKEN(HttpStatus.BAD_REQUEST, "T-001", "토큰이 만료되었습니다."),
    TOKEN_NOT_EXIST(HttpStatus.BAD_REQUEST, "T-002", "토큰이 존재하지 않습니다."),

    // 팀
    TEAM_NOT_EXIST(HttpStatus.BAD_REQUEST, "G-001", "존재하지 않는 팀입니다."),
    TEAM_ACCESS_DENIED(HttpStatus.BAD_REQUEST, "G-002", "해당 팀에 권한이 없습니다."),
    NOT_TEAM_LEADER(HttpStatus.BAD_REQUEST, "G-003", "팀장만 수정 권한이 있습니다."),
    LEADER_NOT_DELETEABLE(HttpStatus.BAD_REQUEST, "G-004", "리더는 삭제할 수 없습니다."),
    MEMBER_NOT_EXIST(HttpStatus.BAD_REQUEST, "G-005", "해당 팀원은 존재하지 않습니다."),
    NOT_TEAM_MEMBER(HttpStatus.BAD_REQUEST, "G-006", "해당 팀의 멤버가 아닙니다."),
    // 파일
    FILE_NOT_EXIST(HttpStatus.BAD_REQUEST, "F-001", "파일이 존재하지 않습니다."),

    // 일정
    SCHEDULE_NOT_EXIST(HttpStatus.BAD_REQUEST, "S-001", "존재하지 않는 일정입니다."),
    SCHEDULE_DUPLICATED(HttpStatus.BAD_REQUEST, "S-002", "해당 시간에 다른 일정이 있습니다."),
    SCHEDULE_ACCESS_DENIED(HttpStatus.BAD_REQUEST, "S-002", "개인 일정은 본인에게만 관리 권한이 있습니다."),
    CONFERENCE_NOT_MODIFY(HttpStatus.BAD_REQUEST, "S-003", "회의 일정은 수정 불가능 합니다."),

    // 회의
    CONFERENCE_NOT_EXIST(HttpStatus.BAD_REQUEST, "C-001", "존재하지 않는 회의입니다."),

    // Validation
    NOT_VALID_REQUEST(HttpStatus.BAD_REQUEST, "I-001", "요청변수가 유효하지 않습니다.");
    
    // 상태, 에러 코드, 메시지
    private HttpStatus httpStatus;
    private String errorCode;
    private String message;

    // 생성자
    ErrorCode(HttpStatus httpStatus, String errorCode, String message) {
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.message = message;
    }
}