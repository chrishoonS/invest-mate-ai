package com.example.invest_mate_ai.stock.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

/** OpenDART 공시 목록 중 리포트 생성에 필요한 필드만 노출합니다. */
@Getter
@Builder
public class StockDisclosureResponse {
    private Long id;
    private Long stockId;
    private String receiptNumber;
    private String reportName;
    private String corporationName;
    private String submitterName;
    private LocalDate receivedAt;
    private String detailUrl;
    private String eventType;
}
