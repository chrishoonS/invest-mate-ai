package com.example.invest_mate_ai.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 도메인 오류는 ErrorCode에 정의한 HTTP 상태와 응답 형식으로 변환
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusiness(BusinessException exception, HttpServletRequest request) {
        return response(exception.getErrorCode(), request);
    }

    @ExceptionHandler({MissingServletRequestParameterException.class, MethodArgumentTypeMismatchException.class, IllegalArgumentException.class})
    public ResponseEntity<ApiErrorResponse> handleInvalidRequest(Exception exception, HttpServletRequest request) {
        return response(ErrorCode.INVALID_REQUEST, request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleForbidden(AccessDeniedException exception, HttpServletRequest request) {
        return response(ErrorCode.FORBIDDEN, request);
    }

    // OAuth 공급자 HTTP/네트워크 오류는 내부 오류와 분리해 502로 응답
    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<ApiErrorResponse> handleOAuthProviderFailure(RestClientException exception, HttpServletRequest request) {
        log.warn("OAuth provider request failed: {}", exception.getMessage());
        return response(ErrorCode.OAUTH_PROVIDER_FAILURE, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(Exception exception, HttpServletRequest request) {
        log.error("Unexpected server error", exception);
        return response(ErrorCode.INTERNAL_ERROR, request);
    }

    private ResponseEntity<ApiErrorResponse> response(ErrorCode errorCode, HttpServletRequest request) {
        return ResponseEntity.status(errorCode.getStatus()).body(ApiErrorResponse.of(errorCode, request.getRequestURI()));
    }
}
