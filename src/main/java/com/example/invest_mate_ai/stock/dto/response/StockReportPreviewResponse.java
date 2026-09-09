package com.example.invest_mate_ai.stock.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.List;

/** DB 저장 전에도 뉴스·공시·재무 조합 결과를 확인할 수 있는 MVP 리포트입니다. */
@Getter
@Builder
public class StockReportPreviewResponse {
    private Long reportId;
    private Long stockId;
    private String companyName;
    private String corporationCode;
    private OffsetDateTime generatedAt;
    private List<StockNewsResponse> news;
    private List<StockDisclosureResponse> disclosures;
    private StockFinancialResponse financial;
    private String oneLineSummary;
}
