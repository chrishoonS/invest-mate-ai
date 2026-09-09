package com.example.invest_mate_ai.stock.service;

import com.example.invest_mate_ai.stock.dto.request.StockCreateRequest;
import com.example.invest_mate_ai.stock.dto.request.StockUpdateRequest;
import com.example.invest_mate_ai.stock.dto.response.StockResponse;
import com.example.invest_mate_ai.stock.type.Market;

import java.util.List;

public interface StockManagementService {

    StockResponse createStock(StockCreateRequest request);

    StockResponse getStock(Long stockId);

    List<StockResponse> searchStock(String keyword, Market market, int limit);

    StockResponse updateStock(Long stockId, StockUpdateRequest request);

    void deleteStock(Long stockId);

}
