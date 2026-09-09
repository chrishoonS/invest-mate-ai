package com.example.invest_mate_ai.stock.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

/** DART 재무제표에서 추출한 핵심 계정과 서버가 계산한 비율을 담습니다. */
@Getter
@Builder
public class StockFinancialResponse {
    private Long id;
    private Long stockId;
    private String corporationCode;
    private int businessYear;
    private String reportCode;
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
}
