package com.example.invest_mate_ai.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    INVALID_REQUEST             (HttpStatus.BAD_REQUEST          , "INVALID_REQUEST"             , "요청 값이 올바르지 않습니다."),
    UNSUPPORTED_OAUTH_PROVIDER  (HttpStatus.BAD_REQUEST          , "UNSUPPORTED_OAUTH_PROVIDER"  , "지원하지 않는 OAuth Provider입니다."),
    USER_NOT_FOUND              (HttpStatus.NOT_FOUND            , "USER_NOT_FOUND"              , "존재하지 않는 회원입니다."),
    USER_ALREADY_WITHDRAWN      (HttpStatus.CONFLICT             , "USER_ALREADY_WITHDRAWN"      , "이미 탈퇴한 회원입니다."),
    FORBIDDEN                   (HttpStatus.FORBIDDEN            , "FORBIDDEN"                   , "접근 권한이 없습니다."),
    OAUTH_PROVIDER_FAILURE      (HttpStatus.BAD_GATEWAY          , "OAUTH_PROVIDER_FAILURE"      , "외부 OAuth 서비스와 통신하지 못했습니다."),
    IDENTITY_VERIFICATION_FAILED(HttpStatus.BAD_REQUEST          , "IDENTITY_VERIFICATION_FAILED", "본인 인증에 실패했거나 취소되었습니다."),
    INTERNAL_ERROR              (HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR"              , "서버 내부 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
