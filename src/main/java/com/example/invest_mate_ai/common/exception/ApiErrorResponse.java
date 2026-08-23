package com.example.invest_mate_ai.common.exception;

import java.time.Instant;

// 모든 실패 응답에서 유지할 공통 API
public record ApiErrorResponse(Instant timestamp, int status, String code, String message, String path) {
    public static ApiErrorResponse of(ErrorCode errorCode, String path) {
        return new ApiErrorResponse(Instant.now(), errorCode.getStatus().value(), errorCode.getCode(), errorCode.getMessage(), path);
    }
}
