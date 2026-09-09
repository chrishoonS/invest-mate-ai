package com.example.invest_mate_ai.stock.mapper;

import com.example.invest_mate_ai.stock.type.Market;
import com.example.invest_mate_ai.stock.vo.StockVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StockMapper {

    StockVo findById(Long id);

    StockVo findByMarketAndCode(@Param("market") Market market, @Param("stockCode") String stockCode);

    List<StockVo> searchStock(@Param("keyword") String keyword, @Param("market") Market market, @Param("limit") int limit);

    int insertStock(StockVo stock);

    int updateStock(StockVo stock);

    int deleteStock(Long id);
}
