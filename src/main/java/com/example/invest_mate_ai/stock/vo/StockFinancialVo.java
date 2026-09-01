package com.example.invest_mate_ai.stock.vo;

import com.example.invest_mate_ai.stock.type.DartReportCode;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/** stock_financial 테이블의 핵심 재무계정 및 비율 영속 모델입니다. */
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class StockFinancialVo {
    private Long id;
    private Long stockId;
    private Integer businessYear;
    private DartReportCode reportCode;
    private String statementType;
    private BigDecimal revenue;
    private BigDecimal operatingProfit;
    private BigDecimal netIncome;
    private BigDecimal totalAssets;
    private BigDecimal totalLiabilities;
    private BigDecimal totalEquity;
    private BigDecimal roe;
    private BigDecimal debtRatio;
    private String currency;
    private OffsetDateTime sourceUpdatedAt;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
