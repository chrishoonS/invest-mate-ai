package com.example.invest_mate_ai.stock.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

/** 종목의 일별 가격·거래량·시가총액 조회 응답입니다. */
@Getter
@Builder
public class DailyPriceResponse {
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
}
