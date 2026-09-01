package com.example.invest_mate_ai.stock.dto.response;

import com.example.invest_mate_ai.stock.type.Market;
import com.example.invest_mate_ai.stock.type.StockStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@Builder
public class StockResponse {

    private Long id;

    private String stockCode;

    private String stockName;

    private Market market;

    private String corpCode;

    private String industryCode;

    private String industry;

    private StockStatus stockStatus;

    private LocalDate listedAt;

    private LocalDate delistedAt;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;
}
