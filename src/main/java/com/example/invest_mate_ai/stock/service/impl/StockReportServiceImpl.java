package com.example.invest_mate_ai.stock.service.impl;

import com.example.invest_mate_ai.common.exception.BusinessException;
import com.example.invest_mate_ai.common.exception.ErrorCode;
import com.example.invest_mate_ai.stock.dto.response.*;
import com.example.invest_mate_ai.stock.mapper.StockDataMapper;
import com.example.invest_mate_ai.stock.mapper.StockMapper;
import com.example.invest_mate_ai.stock.service.StockDataService;
import com.example.invest_mate_ai.stock.service.StockReportService;
import com.example.invest_mate_ai.stock.type.*;
import com.example.invest_mate_ai.stock.vo.StockReportVo;
import com.example.invest_mate_ai.stock.vo.StockVo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class StockReportServiceImpl implements StockReportService {

    private static final int DATA_LIMIT = 10;
    private static final int DISCLOSURE_LOOKBACK_DAYS = 30;

    private final StockDataService stockDataService;
    private final StockMapper stockMapper;
    private final StockDataMapper stockDataMapper;
    private final ObjectMapper objectMapper;

    @Override
    public StockReportPreviewResponse generate(Long stockId, int businessYear, DartReportCode reportCode) {
        StockVo stock = getStockInfoById(stockId);
        LocalDate today = LocalDate.now();

        // 생성 시점에 외부 원천을 갱신하고, 응답 및 리포트는 저장된 DB 데이터를 기준으로 만듭니다.
        List<StockNewsResponse> news = stockDataService.refreshNews(stockId, DATA_LIMIT);
        List<StockDisclosureResponse> disclosures = stockDataService.refreshDisclosures(
                stockId, today.minusDays(DISCLOSURE_LOOKBACK_DAYS), today, DATA_LIMIT);
        StockFinancialResponse financial = stockDataService.refreshFinancials(stockId, businessYear, reportCode);
        String summary = createRuleBasedSummary(news, disclosures);
        OffsetDateTime generatedAt = OffsetDateTime.now();

        StockReportVo report = StockReportVo.builder()
                .stockId(stockId).reportDate(today).reportType(ReportType.DAILY)
                .reportKey(today.toString()).reportStatus(ReportStatus.COMPLETED)
                .overviewJson(toJson(Map.of(
                        "stockId", stockId,
                        "stockCode", stock.getStockCode(),
                        "stockName", stock.getStockName(),
                        "market", stock.getMarket().name(),
                        "industry", stock.getIndustry() == null ? "" : stock.getIndustry())))
                .newsJson(toJson(news)).disclosureJson(toJson(disclosures)).financialJson(toJson(financial))
                .riskJson("{}").technicalJson("{}").oneLineSummary(summary)
                .promptVersion("RULE_V1").dataAsOf(generatedAt).generatedAt(generatedAt).build();
        stockDataMapper.upsertReport(report);
        StockReportVo saved = stockDataMapper.findReportByKey(stockId, ReportType.DAILY, today.toString());

        return StockReportPreviewResponse.builder()
                .reportId(saved.getId()).stockId(stockId).companyName(stock.getStockName())
                .corporationCode(stock.getCorpCode()).generatedAt(generatedAt)
                .news(news).disclosures(disclosures).financial(financial).oneLineSummary(summary).build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockReportResponse> getReports(Long stockId, int limit) {
        getStockInfoById(stockId);
        if (limit < 1 || limit > 100) throw new BusinessException(ErrorCode.INVALID_REQUEST);
        return stockDataMapper.findReports(stockId, limit).stream().map(this::toResponse).toList();
    }

    @Override
    public void deleteReport(Long stockId, Long reportId) {
        getStockInfoById(stockId);
        if (reportId == null || stockDataMapper.deleteReport(reportId, stockId) == 0)
            throw new BusinessException(ErrorCode.STOCK_DATA_NOT_FOUND);
    }

    private StockVo getStockInfoById(Long stockId) {
        if (stockId == null) throw new BusinessException(ErrorCode.INVALID_REQUEST);
        StockVo stock = stockMapper.findById(stockId);
        if (stock == null) throw new BusinessException(ErrorCode.STOCK_NOT_FOUND);
        if (stock.getCorpCode() == null || stock.getCorpCode().isBlank())
            throw new BusinessException(ErrorCode.DART_CORP_CODE_REQUIRED);
        return stock;
    }

    private String createRuleBasedSummary(List<StockNewsResponse> news, List<StockDisclosureResponse> disclosures) {
        long positive = news.stream().filter(item -> item.getSentiment() == Sentiment.POSITIVE).count();
        long negative = news.stream().filter(item -> item.getSentiment() == Sentiment.NEGATIVE).count();
        String direction = positive > negative ? "긍정" : negative > positive ? "부정" : "중립";
        return "최근 뉴스 흐름은 %s이며, 최근 30일 공시는 %d건입니다."
                .formatted(direction, disclosures.size());
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR);
        }
    }

    private StockReportResponse toResponse(StockReportVo report) {
        return StockReportResponse.builder()
                .id(report.getId()).stockId(report.getStockId()).reportDate(report.getReportDate())
                .reportType(report.getReportType()).reportKey(report.getReportKey())
                .reportStatus(report.getReportStatus()).overviewJson(report.getOverviewJson())
                .newsJson(report.getNewsJson()).disclosureJson(report.getDisclosureJson())
                .financialJson(report.getFinancialJson()).riskJson(report.getRiskJson())
                .technicalJson(report.getTechnicalJson()).oneLineSummary(report.getOneLineSummary())
                .failureMessage(report.getFailureMessage()).dataAsOf(report.getDataAsOf())
                .generatedAt(report.getGeneratedAt()).build();
    }
}
