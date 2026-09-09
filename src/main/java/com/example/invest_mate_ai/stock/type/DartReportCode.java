package com.example.invest_mate_ai.stock.type;

import com.example.invest_mate_ai.common.exception.BusinessException;
import com.example.invest_mate_ai.common.exception.ErrorCode;

import java.util.Arrays;

public enum DartReportCode {
    ANNUAL("11011"),
    HALF_YEAR("11012"),
    FIRST_QUARTER("11013"),
    THIRD_QUARTER("11014");

    private final String code;

    DartReportCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    /** 문자열 입력을 enum으로 제한해 잘못된 DART 코드가 외부 API까지 전달되지 않게 합니다. */
    public static DartReportCode from(String value) {
        return Arrays.stream(values())
                .filter(item -> item.name().equalsIgnoreCase(value) || item.code.equals(value))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_REQUEST));
    }
}
