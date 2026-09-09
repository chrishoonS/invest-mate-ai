package com.example.invest_mate_ai.stock.service;

import com.example.invest_mate_ai.stock.dto.response.StockReportPreviewResponse;
import com.example.invest_mate_ai.stock.dto.response.StockReportResponse;
import com.example.invest_mate_ai.stock.type.DartReportCode;

import java.util.List;

/** 수집한 뉴스·공시·재무 데이터를 하나의 리포트 읽기 모델로 조합합니다. */
public interface StockReportService {
    StockReportPreviewResponse generate(
            Long stockId,
            int businessYear,
            DartReportCode reportCode
    );

    List<StockReportResponse> getReports(Long stockId, int limit);
    void deleteReport(Long stockId, Long reportId);
}
