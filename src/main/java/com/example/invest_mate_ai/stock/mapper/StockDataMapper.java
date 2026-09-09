package com.example.invest_mate_ai.stock.mapper;

import com.example.invest_mate_ai.stock.type.DartReportCode;
import com.example.invest_mate_ai.stock.type.ReportType;
import com.example.invest_mate_ai.stock.vo.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/** 가격·뉴스·공시·재무·리포트 테이블의 저장과 조회를 한 수집 데이터 경계로 묶습니다. */
@Mapper
public interface StockDataMapper {
    int upsertDailyPrice(StockDailyPriceVo price);
    List<StockDailyPriceVo> findDailyPrices(@Param("stockId") Long stockId, @Param("from") LocalDate from, @Param("to") LocalDate to);
    int deleteDailyPrice(@Param("stockId") Long stockId, @Param("tradingDate") LocalDate tradingDate);

    int upsertNews(StockNewsVo news);
    List<StockNewsVo> findRecentNews(@Param("stockId") Long stockId, @Param("limit") int limit);
    int deleteNews(@Param("id") Long id, @Param("stockId") Long stockId);

    int upsertDisclosure(StockDisclosureVo disclosure);
    List<StockDisclosureVo> findDisclosures(@Param("stockId") Long stockId, @Param("from") LocalDate from, @Param("to") LocalDate to, @Param("limit") int limit);
    int deleteDisclosure(@Param("id") Long id, @Param("stockId") Long stockId);

    int upsertFinancial(StockFinancialVo financial);
    StockFinancialVo findFinancial(@Param("stockId") Long stockId, @Param("businessYear") int businessYear, @Param("reportCode") DartReportCode reportCode);
    int deleteFinancial(@Param("id") Long id, @Param("stockId") Long stockId);

    StockReportVo findReportByKey(@Param("stockId") Long stockId, @Param("reportType") ReportType reportType, @Param("reportKey") String reportKey);
    List<StockReportVo> findReports(@Param("stockId") Long stockId, @Param("limit") int limit);
    int upsertReport(StockReportVo report);
    int deleteReport(@Param("id") Long id, @Param("stockId") Long stockId);
}
