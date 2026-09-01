package com.example.invest_mate_ai.stock.service.impl;

import com.example.invest_mate_ai.common.exception.BusinessException;
import com.example.invest_mate_ai.common.exception.ErrorCode;
import com.example.invest_mate_ai.stock.client.DisclosureClient;
import com.example.invest_mate_ai.stock.client.NewsClient;
import com.example.invest_mate_ai.stock.dto.request.DailyPriceUpsertRequest;
import com.example.invest_mate_ai.stock.dto.response.*;
import com.example.invest_mate_ai.stock.mapper.StockDataMapper;
import com.example.invest_mate_ai.stock.mapper.StockMapper;
import com.example.invest_mate_ai.stock.service.StockDataService;
import com.example.invest_mate_ai.stock.type.DartReportCode;
import com.example.invest_mate_ai.stock.vo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.HexFormat;
import java.util.List;

/** 외부 수집 결과를 DB에 upsert하고 모든 일반 조회는 DB를 기준으로 반환합니다. */
@Service
@RequiredArgsConstructor
@Transactional
public class StockDataServiceImpl implements StockDataService {

    private final NewsClient newsClient;
    private final DisclosureClient disclosureClient;
    private final StockMapper stockMapper;
    private final StockDataMapper stockDataMapper;

    @Override
    @Transactional(readOnly = true)
    public List<StockNewsResponse> getNews(Long stockId, int limit) {
        getStockInfoById(stockId);
        validateLimit(limit);
        return stockDataMapper.findRecentNews(stockId, limit).stream().map(this::toNewsResponse).toList();
    }

    @Override
    public List<StockNewsResponse> refreshNews(Long stockId, int limit) {
        StockVo stock = getStockInfoById(stockId);
        validateLimit(limit);
        List<StockNewsResponse> collected = newsClient.search(stock.getStockName(), limit);
        collected.forEach(item -> stockDataMapper.upsertNews(StockNewsVo.builder()
                .stockId(stockId).title(item.getTitle()).summary(item.getSummary())
                .originalUrl(item.getOriginalUrl()).sourceUrl(item.getSourceUrl()).sourceName("NAVER")
                .publishedAt(item.getPublishedAt()).sentiment(item.getSentiment())
                .contentHash(createContentHash(item)).build()));
        return getNews(stockId, limit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockDisclosureResponse> getDisclosures(Long stockId, LocalDate from, LocalDate to, int limit) {
        StockVo stock = getStockInfoById(stockId);
        validatePeriod(from, to, limit);
        return stockDataMapper.findDisclosures(stock.getId(), from, to, limit).stream()
                .map(item -> toDisclosureResponse(item, stock)).toList();
    }

    @Override
    public List<StockDisclosureResponse> refreshDisclosures(Long stockId, LocalDate from, LocalDate to, int limit) {
        StockVo stock = requireDartStock(stockId);
        validatePeriod(from, to, limit);
        disclosureClient.findDisclosures(stock.getCorpCode(), from, to, limit)
                .forEach(item -> stockDataMapper.upsertDisclosure(StockDisclosureVo.builder()
                        .stockId(stockId).receiptNumber(item.getReceiptNumber()).reportName(item.getReportName())
                        .submitterName(item.getSubmitterName()).eventType(item.getEventType())
                        .detailUrl(item.getDetailUrl()).receivedAt(item.getReceivedAt()).build()));
        return getDisclosures(stockId, from, to, limit);
    }

    @Override
    @Transactional(readOnly = true)
    public StockFinancialResponse getFinancials(Long stockId, int businessYear, DartReportCode reportCode) {
        StockVo stock = getStockInfoById(stockId);
        validateFinancialRequest(businessYear, reportCode);
        StockFinancialVo financial = stockDataMapper.findFinancial(stockId, businessYear, reportCode);
        if (financial == null) throw new BusinessException(ErrorCode.STOCK_DATA_NOT_FOUND);
        return toFinancialResponse(financial, stock);
    }

    @Override
    public StockFinancialResponse refreshFinancials(Long stockId, int businessYear, DartReportCode reportCode) {
        StockVo stock = requireDartStock(stockId);
        validateFinancialRequest(businessYear, reportCode);
        StockFinancialResponse collected = disclosureClient.findFinancials(stock.getCorpCode(), businessYear, reportCode);
        stockDataMapper.upsertFinancial(StockFinancialVo.builder()
                .stockId(stockId).businessYear(businessYear).reportCode(reportCode)
                .statementType(collected.getStatementType()).revenue(collected.getRevenue())
                .operatingProfit(collected.getOperatingProfit()).netIncome(collected.getNetIncome())
                .totalAssets(collected.getTotalAssets()).totalLiabilities(collected.getTotalLiabilities())
                .totalEquity(collected.getTotalEquity()).roe(collected.getRoe())
                .debtRatio(collected.getDebtRatio()).currency(collected.getCurrency())
                .sourceUpdatedAt(OffsetDateTime.now()).build());
        return getFinancials(stockId, businessYear, reportCode);
    }

    @Override
    public DailyPriceResponse upsertDailyPrice(Long stockId, DailyPriceUpsertRequest request) {
        getStockInfoById(stockId);
        validateDailyPrice(request);
        stockDataMapper.upsertDailyPrice(StockDailyPriceVo.builder()
                .stockId(stockId).tradingDate(request.getTradingDate())
                .openPrice(request.getOpenPrice()).highPrice(request.getHighPrice())
                .lowPrice(request.getLowPrice()).closePrice(request.getClosePrice())
                .adjustedClose(request.getAdjustedClose()).volume(request.getVolume())
                .tradingValue(request.getTradingValue()).marketCap(request.getMarketCap()).build());
        return getDailyPrices(stockId, request.getTradingDate(), request.getTradingDate()).get(0);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DailyPriceResponse> getDailyPrices(Long stockId, LocalDate from, LocalDate to) {
        getStockInfoById(stockId);
        if (from == null || to == null || from.isAfter(to)) throw new BusinessException(ErrorCode.INVALID_REQUEST);
        return stockDataMapper.findDailyPrices(stockId, from, to).stream().map(this::toDailyPriceResponse).toList();
    }

    @Override public void deleteDailyPrice(Long stockId, LocalDate tradingDate) {
        getStockInfoById(stockId);
        if (tradingDate == null || stockDataMapper.deleteDailyPrice(stockId, tradingDate) == 0)
            throw new BusinessException(ErrorCode.STOCK_DATA_NOT_FOUND);
    }
    @Override public void deleteNews(Long stockId, Long newsId) { requireDeleted(stockDataMapper.deleteNews(newsId, stockId)); }
    @Override public void deleteDisclosure(Long stockId, Long disclosureId) { requireDeleted(stockDataMapper.deleteDisclosure(disclosureId, stockId)); }
    @Override public void deleteFinancial(Long stockId, Long financialId) { requireDeleted(stockDataMapper.deleteFinancial(financialId, stockId)); }

    private StockVo getStockInfoById(Long stockId) {
        if (stockId == null) throw new BusinessException(ErrorCode.INVALID_REQUEST);
        StockVo stock = stockMapper.findById(stockId);
        if (stock == null) throw new BusinessException(ErrorCode.STOCK_NOT_FOUND);
        return stock;
    }

    private StockVo requireDartStock(Long stockId) {
        StockVo stock = getStockInfoById(stockId);
        if (stock.getCorpCode() == null || stock.getCorpCode().isBlank())
            throw new BusinessException(ErrorCode.DART_CORP_CODE_REQUIRED);
        return stock;
    }

    private void validateLimit(int limit) {
        if (limit < 1 || limit > 100) throw new BusinessException(ErrorCode.INVALID_REQUEST);
    }
    private void validatePeriod(LocalDate from, LocalDate to, int limit) {
        validateLimit(limit);
        if (from == null || to == null || from.isAfter(to)) throw new BusinessException(ErrorCode.INVALID_REQUEST);
    }
    private void validateFinancialRequest(int year, DartReportCode code) {
        if (year < 2000 || year > LocalDate.now().getYear() || code == null)
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
    }
    private void validateDailyPrice(DailyPriceUpsertRequest request) {
        if (request == null || request.getTradingDate() == null || request.getClosePrice() == null
                || request.getClosePrice().signum() < 0) throw new BusinessException(ErrorCode.INVALID_REQUEST);
        if (isNegative(request.getOpenPrice()) || isNegative(request.getHighPrice())
                || isNegative(request.getLowPrice()) || isNegative(request.getAdjustedClose())
                || request.getVolume() != null && request.getVolume() < 0
                || isNegative(request.getTradingValue()) || isNegative(request.getMarketCap())
                || request.getHighPrice() != null && request.getLowPrice() != null
                && request.getHighPrice().compareTo(request.getLowPrice()) < 0) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }
    }
    private boolean isNegative(java.math.BigDecimal value) { return value != null && value.signum() < 0; }
    private void requireDeleted(int count) {
        if (count == 0) throw new BusinessException(ErrorCode.STOCK_DATA_NOT_FOUND);
    }

    private String createContentHash(StockNewsResponse item) {
        String source = String.join("|", nullSafe(item.getTitle()),
                item.getPublishedAt() == null ? "" : item.getPublishedAt().toString(),
                nullSafe(item.getOriginalUrl()));
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(source.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 algorithm is unavailable", exception);
        }
    }
    private String nullSafe(String value) { return value == null ? "" : value; }

    private StockNewsResponse toNewsResponse(StockNewsVo item) {
        return StockNewsResponse.builder().id(item.getId()).stockId(item.getStockId())
                .title(item.getTitle()).summary(item.getSummary()).originalUrl(item.getOriginalUrl())
                .sourceUrl(item.getSourceUrl()).publishedAt(item.getPublishedAt())
                .sentiment(item.getSentiment()).collectedAt(item.getCollectedAt()).build();
    }
    private StockDisclosureResponse toDisclosureResponse(StockDisclosureVo item, StockVo stock) {
        return StockDisclosureResponse.builder().id(item.getId()).stockId(item.getStockId())
                .receiptNumber(item.getReceiptNumber()).reportName(item.getReportName())
                .corporationName(stock.getStockName()).submitterName(item.getSubmitterName())
                .receivedAt(item.getReceivedAt()).detailUrl(item.getDetailUrl()).eventType(item.getEventType()).build();
    }
    private StockFinancialResponse toFinancialResponse(StockFinancialVo item, StockVo stock) {
        return StockFinancialResponse.builder().id(item.getId()).stockId(item.getStockId())
                .corporationCode(stock.getCorpCode()).businessYear(item.getBusinessYear())
                .reportCode(item.getReportCode().name()).statementType(item.getStatementType())
                .revenue(item.getRevenue()).operatingProfit(item.getOperatingProfit()).netIncome(item.getNetIncome())
                .totalAssets(item.getTotalAssets()).totalLiabilities(item.getTotalLiabilities())
                .totalEquity(item.getTotalEquity()).roe(item.getRoe()).debtRatio(item.getDebtRatio())
                .currency(item.getCurrency()).build();
    }
    private DailyPriceResponse toDailyPriceResponse(StockDailyPriceVo item) {
        return DailyPriceResponse.builder().id(item.getId()).stockId(item.getStockId())
                .tradingDate(item.getTradingDate()).openPrice(item.getOpenPrice()).highPrice(item.getHighPrice())
                .lowPrice(item.getLowPrice()).closePrice(item.getClosePrice()).adjustedClose(item.getAdjustedClose())
                .volume(item.getVolume()).tradingValue(item.getTradingValue()).marketCap(item.getMarketCap()).build();
    }
}
