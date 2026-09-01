package com.example.invest_mate_ai.stock.dto.response;

import com.example.invest_mate_ai.stock.type.Sentiment;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;

/** 외부 뉴스 공급자의 형식을 서비스 공통 형식으로 정규화한 응답입니다. */
@Getter
@Builder
public class StockNewsResponse {
    private Long id;
    private Long stockId;
    private String title;
    private String summary;
    private String originalUrl;
    private String sourceUrl;
    private OffsetDateTime publishedAt;
    private Sentiment sentiment;
    private OffsetDateTime collectedAt;
}
