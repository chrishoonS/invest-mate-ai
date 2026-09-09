package com.example.invest_mate_ai.stock.client;

import com.example.invest_mate_ai.common.exception.BusinessException;
import com.example.invest_mate_ai.common.exception.ErrorCode;
import com.example.invest_mate_ai.stock.calculator.NewsSentimentCalculator;
import com.example.invest_mate_ai.stock.dto.response.StockNewsResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.HtmlUtils;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class NaverNewsClient implements NewsClient {

    private static final int MAX_DISPLAY = 100;

    private final RestClient restClient;
    private final NewsSentimentCalculator sentimentCalculator;

    @Value("${stock.api.naver.client-id:}")
    private String clientId;

    @Value("${stock.api.naver.client-secret:}")
    private String clientSecret;

    @Value("${stock.api.naver.news-uri}")
    private String newsUri;

    @Override
    public List<StockNewsResponse> search(String companyName, int limit) {
        validateConfiguration();
        int display = Math.max(1, Math.min(limit, MAX_DISPLAY));

        try {
            NaverNewsApiResponse response = restClient.get()
                    .uri(newsUri, uriBuilder -> uriBuilder
                            // 일반 단어와 종목명이 겹치는 오탐을 줄이기 위해 주식 문맥을 함께 검색
                            .queryParam("query", companyName + " 주식")
                            .queryParam("display", display)
                            .queryParam("sort", "date")
                            .build())
                    .header("X-Naver-Client-Id", clientId)
                    .header("X-Naver-Client-Secret", clientSecret)
                    .retrieve()
                    .body(NaverNewsApiResponse.class);

            if (response == null || response.getItems() == null) {
                return Collections.emptyList();
            }
            return response.getItems().stream().map(this::toResponse).toList();
        } catch (RestClientException exception) {
            throw new BusinessException(ErrorCode.STOCK_DATA_PROVIDER_FAILURE);
        }
    }

    private StockNewsResponse toResponse(NaverNewsItem item) {
        String title = cleanHtml(item.getTitle());
        String summary = cleanHtml(item.getDescription());

        return StockNewsResponse.builder()
                .title(title)
                .summary(summary)
                .originalUrl(item.getOriginalLink())
                .sourceUrl(item.getLink())
                .publishedAt(parsePublishedAt(item.getPublishedAt()))
                .sentiment(sentimentCalculator.calculate(title, summary))
                .build();
    }

    /** 네이버 결과의 강조 태그와 HTML 엔티티를 API 소비자가 받지 않도록 제거. */
    private String cleanHtml(String value) {
        if (value == null) {
            return null;
        }
        return HtmlUtils.htmlUnescape(value.replaceAll("<[^>]*>", "")).trim();
    }

    private OffsetDateTime parsePublishedAt(String value) {
        try {
            return value == null ? null : OffsetDateTime.parse(value, DateTimeFormatter.RFC_1123_DATE_TIME);
        } catch (DateTimeParseException exception) {
            // 공급자의 날짜 형식이 일시적으로 달라져도 뉴스 전체 조회를 실패시키지 않습니다.
            return null;
        }
    }

    private void validateConfiguration() {
        if (clientId.isBlank() || clientSecret.isBlank()) {
            throw new BusinessException(ErrorCode.STOCK_API_NOT_CONFIGURED);
        }
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class NaverNewsApiResponse {
        private List<NaverNewsItem> items;
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class NaverNewsItem {
        private String title;
        private String description;
        private String link;
        private String originalLink;

        @JsonProperty("pubDate")
        private String publishedAt;
    }
}
