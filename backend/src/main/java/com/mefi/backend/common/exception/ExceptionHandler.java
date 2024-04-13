package com.mefi.backend.common.exception;

import com.mefi.backend.common.model.BaseResponseBody;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// RestController 모든 예외 처리 관리
@RestControllerAdvice
@Slf4j
public class ExceptionHandler {

    // Exceptions 예외 처리 응답
    @org.springframework.web.bind.annotation.ExceptionHandler(Exceptions.class)
    public ResponseEntity<? extends BaseResponseBody> exceptions(Exceptions e) {

        // 정의한 에러 코드 및 메시지 적용
        BaseResponseBody errorResponse = BaseResponseBody.error(e.getErrorCode().getErrorCode(), e.getMessage());
        return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(errorResponse);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(ConstraintViolationException.class)
    public Object handleConstraintViolationException(ConstraintViolationException e) {

        log.info("handleMethodArgumentNotValidException 핸들러 입장");
        log.info(e.toString());
        log.info(e.getMessage());

        // 정의한 에러 코드 및 메시지 적용
        BaseResponseBody errorResponse = BaseResponseBody.error(ErrorCode.NOT_VALID_REQUEST.getErrorCode(), ErrorCode.NOT_VALID_REQUEST.getMessage());
        return ResponseEntity.status(ErrorCode.NOT_VALID_REQUEST.getHttpStatus()).body(errorResponse);
    }


    @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {

        log.info("handleMethodArgumentNotValidException 핸들러 입장");
        log.info(e.toString());
        log.info(e.getMessage());

        // 정의한 에러 코드 및 메시지 적용
        BaseResponseBody errorResponse = BaseResponseBody.error(ErrorCode.NOT_VALID_REQUEST.getErrorCode(), ErrorCode.NOT_VALID_REQUEST.getMessage());
        return ResponseEntity.status(ErrorCode.NOT_VALID_REQUEST.getHttpStatus()).body(errorResponse);
    }
}