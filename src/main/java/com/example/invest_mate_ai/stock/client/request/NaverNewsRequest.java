package com.example.invest_mate_ai.stock.client.request;

import com.example.invest_mate_ai.common.http.QueryParameterSource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public record NaverNewsRequest(String companyName, int limit) implements QueryParameterSource {
    private static final int MAX_DISPLAY = 100;

    @Override
    public MultiValueMap<String, String> queryParameters() {
        // RestClient의 UriBuilder에 바로 전달할 수 있도록 MultiValueMap으로 생성합니다.
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        // 일반 단어와 종목명이 겹치는 오탐을 줄이기 위해 주식 문맥을 함께 검색합니다.
        parameters.add("query", companyName + " 주식");
        parameters.add("display", Integer.toString(Math.max(1, Math.min(limit, MAX_DISPLAY))));
        parameters.add("sort", "date");
        return parameters;
    }
}
