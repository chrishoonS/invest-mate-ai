package com.example.invest_mate_ai.stock.client;

import com.example.invest_mate_ai.common.exception.BusinessException;
import com.example.invest_mate_ai.common.exception.ErrorCode;
import com.example.invest_mate_ai.stock.client.request.NaverNewsRequest;
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
        NaverNewsRequest request = new NaverNewsRequest(companyName, limit);

        try {
            NaverNewsApiResponse response = restClient.get()
                    .uri(newsUri, uriBuilder -> uriBuilder
                            .queryParams(request.queryParameters())
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
