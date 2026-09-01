package com.example.invest_mate_ai.stock.vo;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class StockDailyPriceVo {
    private Long id;
    private Long stockId;
    private LocalDate tradingDate;
    private BigDecimal openPrice;
    private BigDecimal highPrice;
    private BigDecimal lowPrice;
    private BigDecimal closePrice;
    private BigDecimal adjustedClose;
    private Long volume;
    private BigDecimal tradingValue;
    private BigDecimal marketCap;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
