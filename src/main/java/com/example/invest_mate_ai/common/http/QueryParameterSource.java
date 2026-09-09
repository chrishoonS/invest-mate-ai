package com.example.invest_mate_ai.common.http;

import org.springframework.util.MultiValueMap;

/** 외부 API 요청이 UriBuilder용 쿼리 파라미터를 직접 제공하도록 정의합니다. */
public interface QueryParameterSource {
    /** UriBuilder에 별도 변환 없이 전달할 수 있는 쿼리 파라미터를 반환합니다. */
    MultiValueMap<String, String> queryParameters();
}
