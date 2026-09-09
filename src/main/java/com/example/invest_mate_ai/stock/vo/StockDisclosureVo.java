package com.example.invest_mate_ai.stock.vo;

import lombok.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;

/** stock_disclosure 테이블의 DART 공시 영속 모델입니다. */
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class StockDisclosureVo {
    private Long id;
    private Long stockId;
    private String receiptNumber;
    private String reportName;
    private String submitterName;
    private String eventType;
    private String detailUrl;
    private LocalDate receivedAt;
    private String analyzedSummary;
    private OffsetDateTime collectedAt;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
