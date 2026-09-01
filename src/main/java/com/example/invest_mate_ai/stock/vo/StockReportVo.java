package com.example.invest_mate_ai.stock.vo;

import com.example.invest_mate_ai.stock.type.ReportStatus;
import com.example.invest_mate_ai.stock.type.ReportType;
import lombok.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;

/** stock_report 테이블의 리포트 스냅샷 영속 모델입니다. JSONB 값은 JSON 문자열로 보관합니다. */
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class StockReportVo {
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
    private String modelName;
    private String promptVersion;
    private OffsetDateTime dataAsOf;
    private OffsetDateTime generatedAt;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
