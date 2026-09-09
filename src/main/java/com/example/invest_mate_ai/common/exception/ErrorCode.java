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
    // 외부 주식 데이터의 장애, 데이터 부재, 설정 누락을 서로 다른 HTTP 상태로 전달합니다.
    STOCK_DATA_PROVIDER_FAILURE (HttpStatus.BAD_GATEWAY          , "STOCK_DATA_PROVIDER_FAILURE" , "외부 주식 데이터 서비스와 통신하지 못했습니다."),
    STOCK_DATA_NOT_FOUND        (HttpStatus.NOT_FOUND            , "STOCK_DATA_NOT_FOUND"        , "요청한 종목 데이터를 찾을 수 없습니다."),
    STOCK_API_NOT_CONFIGURED    (HttpStatus.SERVICE_UNAVAILABLE  , "STOCK_API_NOT_CONFIGURED"    , "주식 데이터 API 설정이 완료되지 않았습니다."),
    STOCK_NOT_FOUND             (HttpStatus.NOT_FOUND            , "STOCK_NOT_FOUND"             , "존재하지 않는 종목입니다."),
    STOCK_ALREADY_EXISTS        (HttpStatus.CONFLICT             , "STOCK_ALREADY_EXISTS"        , "이미 등록된 종목입니다."),
    STOCK_IN_USE                (HttpStatus.CONFLICT             , "STOCK_IN_USE"                , "참조 데이터가 존재하여 종목을 삭제할 수 없습니다."),
    DART_CORP_CODE_REQUIRED     (HttpStatus.CONFLICT             , "DART_CORP_CODE_REQUIRED"     , "종목에 DART 기업 고유번호가 등록되지 않았습니다."),
    WATCHLIST_NOT_FOUND         (HttpStatus.NOT_FOUND            , "WATCHLIST_NOT_FOUND"         , "존재하지 않는 관심 종목입니다."),
    WATCHLIST_ALREADY_EXISTS    (HttpStatus.CONFLICT             , "WATCHLIST_ALREADY_EXISTS"    , "이미 등록된 관심 종목입니다."),
    ALERT_SETTING_NOT_FOUND     (HttpStatus.NOT_FOUND            , "ALERT_SETTING_NOT_FOUND"     , "알림 설정이 존재하지 않습니다."),
    NOTIFICATION_NOT_FOUND      (HttpStatus.NOT_FOUND            , "NOTIFICATION_NOT_FOUND"      , "알림이 존재하지 않습니다."),
    NOTIFICATION_ALREADY_EXISTS (HttpStatus.CONFLICT             , "NOTIFICATION_ALREADY_EXISTS" , "이미 생성된 알림입니다."),
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
