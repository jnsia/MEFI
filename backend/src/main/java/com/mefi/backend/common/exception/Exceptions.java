package com.mefi.backend.common.exception;

import lombok.Getter;

@Getter
public class Exceptions extends RuntimeException {

    // 에러 코드
    private ErrorCode errorCode;

    // 생성자
    public Exceptions(ErrorCode errorCode) {
        
        // 인자가 하나인 경우 에러 메시지 적용
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}