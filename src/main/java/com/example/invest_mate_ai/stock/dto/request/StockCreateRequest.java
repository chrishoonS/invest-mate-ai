package com.example.invest_mate_ai.stock.dto.request;

import com.example.invest_mate_ai.stock.type.Market;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class StockCreateRequest {
    private String stockCode;
    private String stockName;
    private Market market;
    private String corpCode;
    private String industryCode;
    private String industry;
    private LocalDate listedAt;
}
