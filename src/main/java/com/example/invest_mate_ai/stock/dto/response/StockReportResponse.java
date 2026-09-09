package com.example.invest_mate_ai.stock.dto.response;

import com.example.invest_mate_ai.stock.type.ReportStatus;
import com.example.invest_mate_ai.stock.type.ReportType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.OffsetDateTime;

/** DB에 저장된 리포트 스냅샷을 JSON 문자열과 메타데이터로 반환합니다. */
@Getter
@Builder
public class StockReportResponse {
    private Long id;
    private Long stockId;
    private LocalDate reportDate;
    private ReportType reportType;
    private String reportKey;
    private ReportStatus reportStatus;
    private String overviewJson;
    private String newsJson;
    private String disclosureJson;
    private String financialJson;
    private String riskJson;
    private String technicalJson;
    private String oneLineSummary;
    private String failureMessage;
    private OffsetDateTime dataAsOf;
    private OffsetDateTime generatedAt;
}
