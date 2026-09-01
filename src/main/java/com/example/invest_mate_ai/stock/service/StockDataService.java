package com.example.invest_mate_ai.stock.service;

import com.example.invest_mate_ai.stock.dto.response.StockDisclosureResponse;
import com.example.invest_mate_ai.stock.dto.response.StockFinancialResponse;
import com.example.invest_mate_ai.stock.dto.response.StockNewsResponse;
import com.example.invest_mate_ai.stock.dto.request.DailyPriceUpsertRequest;
import com.example.invest_mate_ai.stock.dto.response.DailyPriceResponse;
import com.example.invest_mate_ai.stock.type.DartReportCode;

import java.time.LocalDate;
import java.util.List;

/** Controller와 외부 API 클라이언트 사이에서 입력 검증과 데이터 조회 흐름을 담당합니다. */
public interface StockDataService {
    List<StockNewsResponse> getNews(Long stockId, int limit);
    List<StockNewsResponse> refreshNews(Long stockId, int limit);

    List<StockDisclosureResponse> getDisclosures(Long stockId, LocalDate from, LocalDate to, int limit);
    List<StockDisclosureResponse> refreshDisclosures(Long stockId, LocalDate from, LocalDate to, int limit);

    StockFinancialResponse getFinancials(Long stockId, int businessYear, DartReportCode reportCode);
    StockFinancialResponse refreshFinancials(Long stockId, int businessYear, DartReportCode reportCode);

    DailyPriceResponse upsertDailyPrice(Long stockId, DailyPriceUpsertRequest request);
    List<DailyPriceResponse> getDailyPrices(Long stockId, LocalDate from, LocalDate to);
    void deleteDailyPrice(Long stockId, LocalDate tradingDate);

    void deleteNews(Long stockId, Long newsId);
    void deleteDisclosure(Long stockId, Long disclosureId);
    void deleteFinancial(Long stockId, Long financialId);
}
