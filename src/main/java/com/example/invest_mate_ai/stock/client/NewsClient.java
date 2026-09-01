package com.example.invest_mate_ai.stock.client;

import com.example.invest_mate_ai.stock.dto.response.StockNewsResponse;

import java.util.List;

/** 뉴스 공급자가 바뀌어도 서비스 계층을 수정하지 않기 위한 포트입니다. */
public interface NewsClient {
    List<StockNewsResponse> search(String companyName, int limit);
}
