package com.example.invest_mate_ai.stock.service.impl;

import com.example.invest_mate_ai.common.exception.BusinessException;
import com.example.invest_mate_ai.common.exception.ErrorCode;
import com.example.invest_mate_ai.stock.dto.request.StockCreateRequest;
import com.example.invest_mate_ai.stock.dto.request.StockUpdateRequest;
import com.example.invest_mate_ai.stock.dto.response.StockResponse;
import com.example.invest_mate_ai.stock.mapper.StockMapper;
import com.example.invest_mate_ai.stock.service.StockManagementService;
import com.example.invest_mate_ai.stock.type.Market;
import com.example.invest_mate_ai.stock.type.StockStatus;
import com.example.invest_mate_ai.stock.vo.StockVo;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StockManagementServiceImpl implements StockManagementService {

    private final StockMapper stockMapper;

    @Override
    public StockResponse createStock(StockCreateRequest request) {
        validateCreateStock(request);

        if (stockMapper.findByMarketAndCode(request.getMarket(), request.getStockCode()) != null) {
            throw new BusinessException(ErrorCode.STOCK_ALREADY_EXISTS);
        }

        StockVo stock = StockVo.builder()
                .stockCode(request.getStockCode().trim())
                .stockName(request.getStockName().trim())
                .market(request.getMarket())
                .corpCode(blankToNull(request.getCorpCode()))
                .industryCode(blankToNull(request.getIndustryCode()))
                .industry(blankToNull(request.getIndustry()))
                .stockStatus(StockStatus.ACTIVE)
                .listedAt(request.getListedAt())
                .build();
        try {
            stockMapper.insertStock(stock);
        } catch (DataIntegrityViolationException exception) {
            throw new BusinessException(ErrorCode.STOCK_ALREADY_EXISTS);
        }
        return getStock(stock.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public StockResponse getStock(Long stockId) {
        return toResponse(getStockInfoById(stockId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockResponse> searchStock(String keyword, Market market, int limit) {
        if (limit < 1 || limit > 100) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }
        String normalizedKeyword = keyword == null ? null : keyword.trim();
        return stockMapper.searchStock(normalizedKeyword, market, limit).stream().map(this::toResponse).toList();
    }

    @Override
    public StockResponse updateStock(Long stockId, StockUpdateRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }
        validateCorpCode(request.getCorpCode());
        StockVo stock = getStockInfoById(stockId);
        if (request.getStockName() != null) {
            if (request.getStockName().isBlank()) throw new BusinessException(ErrorCode.INVALID_REQUEST);
            stock.setStockName(request.getStockName().trim());
        }
        if (request.getCorpCode() != null) stock.setCorpCode(blankToNull(request.getCorpCode()));
        if (request.getIndustryCode() != null) stock.setIndustryCode(blankToNull(request.getIndustryCode()));
        if (request.getIndustry() != null) stock.setIndustry(blankToNull(request.getIndustry()));
        if (request.getStockStatus() != null) stock.setStockStatus(request.getStockStatus());
        if (request.getListedAt() != null) stock.setListedAt(request.getListedAt());
        if (request.getDelistedAt() != null) stock.setDelistedAt(request.getDelistedAt());

        try {
            stockMapper.updateStock(stock);
        } catch (DataIntegrityViolationException exception) {
            throw new BusinessException(ErrorCode.STOCK_ALREADY_EXISTS);
        }
        return getStock(stockId);
    }

    @Override
    public void deleteStock(Long stockId) {
        getStockInfoById(stockId);
        try {
            stockMapper.deleteStock(stockId);
        } catch (DataIntegrityViolationException exception) {
            // 관심 종목이나 과거 데이터가 있으면 FK가 삭제를 막고 상장 상태 변경을 유도합니다.
            throw new BusinessException(ErrorCode.STOCK_IN_USE);
        }
    }

    private StockVo getStockInfoById(Long stockId) {
        if (stockId == null) throw new BusinessException(ErrorCode.INVALID_REQUEST);
        StockVo stock = stockMapper.findById(stockId);
        if (stock == null) throw new BusinessException(ErrorCode.STOCK_NOT_FOUND);
        return stock;
    }

    private void validateCreateStock(StockCreateRequest request) {
        if (request == null || request.getMarket() == null
                || request.getStockCode() == null || request.getStockCode().isBlank()
                || request.getStockName() == null || request.getStockName().isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }
        validateCorpCode(request.getCorpCode());
    }

    private void validateCorpCode(String corpCode) {
        // 회사코드 8자리 확인
        if (corpCode != null && !corpCode.isBlank() && !corpCode.matches("\\d{8}")) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private StockResponse toResponse(StockVo stock) {
        return StockResponse.builder()
                .id(stock.getId()).stockCode(stock.getStockCode()).stockName(stock.getStockName())
                .market(stock.getMarket()).corpCode(stock.getCorpCode())
                .industryCode(stock.getIndustryCode()).industry(stock.getIndustry())
                .stockStatus(stock.getStockStatus()).listedAt(stock.getListedAt())
                .delistedAt(stock.getDelistedAt()).createdAt(stock.getCreatedAt())
                .updatedAt(stock.getUpdatedAt()).build();
    }
}
