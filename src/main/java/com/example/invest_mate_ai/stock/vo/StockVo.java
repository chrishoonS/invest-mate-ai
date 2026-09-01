package com.example.invest_mate_ai.stock.vo;

import com.example.invest_mate_ai.stock.type.Market;
import com.example.invest_mate_ai.stock.type.StockStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;

/** stock 테이블과 1:1로 대응하는 종목 마스터 영속 모델입니다. */
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class StockVo {
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
