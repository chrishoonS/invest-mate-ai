package com.example.invest_mate_ai.stock.dto.request;

import com.example.invest_mate_ai.stock.type.StockStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/** 종목명, DART 매핑, 산업군 및 상장 상태를 수정하는 요청입니다. */
@Getter
@NoArgsConstructor
public class StockUpdateRequest {
    private String stockName;
    private String corpCode;
    private String industryCode;
    private String industry;
    private StockStatus stockStatus;
    private LocalDate listedAt;
    private LocalDate delistedAt;
}
