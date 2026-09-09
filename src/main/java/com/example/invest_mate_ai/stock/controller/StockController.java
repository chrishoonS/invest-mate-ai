package com.example.invest_mate_ai.stock.controller;

import com.example.invest_mate_ai.stock.dto.request.*;
import com.example.invest_mate_ai.stock.dto.response.*;
import com.example.invest_mate_ai.stock.service.StockDataService;
import com.example.invest_mate_ai.stock.service.StockManagementService;
import com.example.invest_mate_ai.stock.service.StockReportService;
import com.example.invest_mate_ai.stock.type.DartReportCode;
import com.example.invest_mate_ai.stock.type.Market;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/** 종목 마스터와 종목별 가격·뉴스·공시·재무·리포트 CRUD API를 제공합니다. */
@RestController
@RequestMapping("/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockManagementService stockManagementService;
    private final StockDataService stockDataService;
    private final StockReportService stockReportService;

    @PostMapping
    public ResponseEntity<StockResponse> createStock(@RequestBody StockCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(stockManagementService.createStock(request));
    }

    @GetMapping("/{stockId}")
    public ResponseEntity<StockResponse> getStock(@PathVariable Long stockId) {
        return ResponseEntity.ok(stockManagementService.getStock(stockId));
    }

    @GetMapping
    public ResponseEntity<List<StockResponse>> searchStock(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Market market,
            @RequestParam(defaultValue = "20") int limit
    ) {
        return ResponseEntity.ok(stockManagementService.searchStock(keyword, market, limit));
    }

    @PatchMapping("/{stockId}")
    public ResponseEntity<StockResponse> updateStock(@PathVariable Long stockId, @RequestBody StockUpdateRequest request) {
        return ResponseEntity.ok(stockManagementService.updateStock(stockId, request));
    }

    @DeleteMapping("/{stockId}")
    public ResponseEntity<Void> deleteStock(@PathVariable Long stockId) {
        stockManagementService.deleteStock(stockId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{stockId}/news")
    public ResponseEntity<List<StockNewsResponse>> getNews(
            @PathVariable Long stockId, @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(stockDataService.getNews(stockId, limit));
    }

    @PostMapping("/{stockId}/news/refresh")
    public ResponseEntity<List<StockNewsResponse>> refreshNews(
            @PathVariable Long stockId, @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(stockDataService.refreshNews(stockId, limit));
    }

    @DeleteMapping("/{stockId}/news/{newsId}")
    public ResponseEntity<Void> deleteNews(@PathVariable Long stockId, @PathVariable Long newsId) {
        stockDataService.deleteNews(stockId, newsId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{stockId}/disclosures")
    public ResponseEntity<List<StockDisclosureResponse>> getDisclosures(
            @PathVariable Long stockId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "10") int limit
    ) {
        LocalDate end = to == null ? LocalDate.now() : to;
        return ResponseEntity.ok(stockDataService.getDisclosures(
                stockId, from == null ? end.minusDays(30) : from, end, limit));
    }

    @PostMapping("/{stockId}/disclosures/refresh")
    public ResponseEntity<List<StockDisclosureResponse>> refreshDisclosures(
            @PathVariable Long stockId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "10") int limit
    ) {
        LocalDate end = to == null ? LocalDate.now() : to;
        return ResponseEntity.ok(stockDataService.refreshDisclosures(
                stockId, from == null ? end.minusDays(30) : from, end, limit));
    }

    @DeleteMapping("/{stockId}/disclosures/{disclosureId}")
    public ResponseEntity<Void> deleteDisclosure(@PathVariable Long stockId, @PathVariable Long disclosureId) {
        stockDataService.deleteDisclosure(stockId, disclosureId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{stockId}/financials")
    public ResponseEntity<StockFinancialResponse> getFinancials(
            @PathVariable Long stockId, @RequestParam int businessYear,
            @RequestParam(defaultValue = "ANNUAL") String reportCode) {
        return ResponseEntity.ok(stockDataService.getFinancials(
                stockId, businessYear, DartReportCode.from(reportCode)));
    }

    @PostMapping("/{stockId}/financials/refresh")
    public ResponseEntity<StockFinancialResponse> refreshFinancials(
            @PathVariable Long stockId, @RequestParam int businessYear,
            @RequestParam(defaultValue = "ANNUAL") String reportCode) {
        return ResponseEntity.ok(stockDataService.refreshFinancials(
                stockId, businessYear, DartReportCode.from(reportCode)));
    }

    @DeleteMapping("/{stockId}/financials/{financialId}")
    public ResponseEntity<Void> deleteFinancial(@PathVariable Long stockId, @PathVariable Long financialId) {
        stockDataService.deleteFinancial(stockId, financialId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{stockId}/prices")
    public ResponseEntity<DailyPriceResponse> upsertPrice(
            @PathVariable Long stockId, @RequestBody DailyPriceUpsertRequest request) {
        return ResponseEntity.ok(stockDataService.upsertDailyPrice(stockId, request));
    }

    @GetMapping("/{stockId}/prices")
    public ResponseEntity<List<DailyPriceResponse>> getPrices(
            @PathVariable Long stockId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(stockDataService.getDailyPrices(stockId, from, to));
    }

    @DeleteMapping("/{stockId}/prices/{tradingDate}")
    public ResponseEntity<Void> deletePrice(
            @PathVariable Long stockId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tradingDate) {
        stockDataService.deleteDailyPrice(stockId, tradingDate);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{stockId}/reports")
    public ResponseEntity<StockReportPreviewResponse> generateReport(
            @PathVariable Long stockId, @RequestParam int businessYear,
            @RequestParam(defaultValue = "ANNUAL") String reportCode) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                stockReportService.generate(stockId, businessYear, DartReportCode.from(reportCode)));
    }

    @GetMapping("/{stockId}/reports")
    public ResponseEntity<List<StockReportResponse>> getReports(
            @PathVariable Long stockId, @RequestParam(defaultValue = "20") int limit) {
        return ResponseEntity.ok(stockReportService.getReports(stockId, limit));
    }

    @DeleteMapping("/{stockId}/reports/{reportId}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long stockId, @PathVariable Long reportId) {
        stockReportService.deleteReport(stockId, reportId);
        return ResponseEntity.noContent().build();
    }
}
