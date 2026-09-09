package com.example.invest_mate_ai.stock.vo;

import com.example.invest_mate_ai.stock.type.Sentiment;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/** stock_news 테이블의 뉴스 및 감성 분석 영속 모델입니다. */
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class StockNewsVo {
    private Long id;
    private Long stockId;
    private String title;
    private String summary;
    private String originalUrl;
    private String sourceUrl;
    private String sourceName;
    private OffsetDateTime publishedAt;
    private Sentiment sentiment;
    private BigDecimal sentimentScore;
    private String contentHash;
    private OffsetDateTime collectedAt;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
