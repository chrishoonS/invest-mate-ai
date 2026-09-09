package com.example.invest_mate_ai.stock.client;

import com.example.invest_mate_ai.stock.dto.response.StockDisclosureResponse;
import com.example.invest_mate_ai.stock.dto.response.StockFinancialResponse;
import com.example.invest_mate_ai.stock.type.DartReportCode;

import java.time.LocalDate;
import java.util.List;

/** 공시 목록과 정기보고서 재무정보를 제공하는 외부 시스템 포트입니다. */
public interface DisclosureClient {
    List<StockDisclosureResponse> findDisclosures(String corporationCode, LocalDate from, LocalDate to, int limit);

    StockFinancialResponse findFinancials(String corporationCode, int businessYear, DartReportCode reportCode);
}
