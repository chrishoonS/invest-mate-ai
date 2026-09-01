package com.example.invest_mate_ai.stock.client;

import com.example.invest_mate_ai.common.exception.BusinessException;
import com.example.invest_mate_ai.common.exception.ErrorCode;
import com.example.invest_mate_ai.stock.calculator.FinancialMetricCalculator;
import com.example.invest_mate_ai.stock.dto.response.StockDisclosureResponse;
import com.example.invest_mate_ai.stock.dto.response.StockFinancialResponse;
import com.example.invest_mate_ai.stock.type.DartReportCode;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/** OpenDART의 공시 목록과 단일회사 전체 재무제표를 내부 모델로 변환합니다. */
@Component
@RequiredArgsConstructor
public class OpenDartDisclosureClient implements DisclosureClient {

    private static final String SUCCESS_STATUS = "000";
    private static final String NO_DATA_STATUS = "013";
    private static final DateTimeFormatter DART_DATE = DateTimeFormatter.BASIC_ISO_DATE;

    private final RestClient restClient;
    private final FinancialMetricCalculator metricCalculator;

    @Value("${stock.api.dart.api-key:}")
    private String apiKey;

    @Value("${stock.api.dart.base-uri}")
    private String baseUri;

    @Override
    public List<StockDisclosureResponse> findDisclosures(
            String corporationCode,
            LocalDate from,
            LocalDate to,
            int limit
    ) {
        validateConfiguration();
        int pageCount = Math.max(1, Math.min(limit, 100));

        try {
            DartDisclosureApiResponse response = restClient.get()
                    .uri(baseUri + "/list.json", uriBuilder -> uriBuilder
                            .queryParam("crtfc_key", apiKey)
                            .queryParam("corp_code", corporationCode)
                            .queryParam("bgn_de", from.format(DART_DATE))
                            .queryParam("end_de", to.format(DART_DATE))
                            .queryParam("page_count", pageCount)
                            .queryParam("sort", "date")
                            .queryParam("sort_mth", "desc")
                            .build())
                    .retrieve()
                    .body(DartDisclosureApiResponse.class);

            if (response == null || NO_DATA_STATUS.equals(response.getStatus())) {
                return Collections.emptyList();
            }
            validateDartStatus(response.getStatus());
            return response.getItems() == null
                    ? Collections.emptyList()
                    : response.getItems().stream().map(this::toDisclosure).toList();
        } catch (BusinessException exception) {
            throw exception;
        } catch (RestClientException exception) {
            throw new BusinessException(ErrorCode.STOCK_DATA_PROVIDER_FAILURE);
        }
    }

    @Override
    public StockFinancialResponse findFinancials(
            String corporationCode,
            int businessYear,
            DartReportCode reportCode
    ) {
        validateConfiguration();

        try {
            DartFinancialApiResponse response = restClient.get()
                    .uri(baseUri + "/fnlttSinglAcntAll.json", uriBuilder -> uriBuilder
                            .queryParam("crtfc_key", apiKey)
                            .queryParam("corp_code", corporationCode)
                            .queryParam("bsns_year", businessYear)
                            .queryParam("reprt_code", reportCode.getCode())
                            // 연결재무제표를 우선 기준으로 삼아 종속기업을 포함한 기업 전체를 평가합니다.
                            .queryParam("fs_div", "CFS")
                            .build())
                    .retrieve()
                    .body(DartFinancialApiResponse.class);

            if (response == null || NO_DATA_STATUS.equals(response.getStatus())) {
                throw new BusinessException(ErrorCode.STOCK_DATA_NOT_FOUND);
            }
            validateDartStatus(response.getStatus());
            return toFinancial(corporationCode, businessYear, reportCode, response.getItems());
        } catch (BusinessException exception) {
            throw exception;
        } catch (RestClientException exception) {
            throw new BusinessException(ErrorCode.STOCK_DATA_PROVIDER_FAILURE);
        }
    }

    private StockDisclosureResponse toDisclosure(DartDisclosureItem item) {
        return StockDisclosureResponse.builder()
                .receiptNumber(item.getReceiptNumber())
                .reportName(item.getReportName())
                .corporationName(item.getCorporationName())
                .submitterName(item.getSubmitterName())
                .receivedAt(LocalDate.parse(item.getReceiptDate(), DART_DATE))
                .detailUrl("https://dart.fss.or.kr/dsaf001/main.do?rcpNo=" + item.getReceiptNumber())
                .eventType(classifyEvent(item.getReportName()))
                .build();
    }

    private StockFinancialResponse toFinancial(
            String corporationCode,
            int businessYear,
            DartReportCode reportCode,
            List<DartFinancialItem> items
    ) {
        if (items == null || items.isEmpty()) {
            throw new BusinessException(ErrorCode.STOCK_DATA_NOT_FOUND);
        }

        BigDecimal revenue = findAmount(items, this::isRevenue);
        BigDecimal operatingProfit = findAmount(items, this::isOperatingProfit);
        BigDecimal netIncome = findAmount(items, this::isNetIncome);
        BigDecimal assets = findAmount(items, item -> accountMatches(item, "ifrs-full_Assets", "자산총계"));
        BigDecimal liabilities = findAmount(items, item -> accountMatches(item, "ifrs-full_Liabilities", "부채총계"));
        BigDecimal equity = findAmount(items, item -> accountMatches(item, "ifrs-full_Equity", "자본총계"));

        return StockFinancialResponse.builder()
                .corporationCode(corporationCode)
                .businessYear(businessYear)
                .reportCode(reportCode.name())
                .statementType("CFS")
                .revenue(revenue)
                .operatingProfit(operatingProfit)
                .netIncome(netIncome)
                .totalAssets(assets)
                .totalLiabilities(liabilities)
                .totalEquity(equity)
                // 분기 순이익을 단순 연환산하면 왜곡될 수 있어 연간 보고서에서만 ROE를 계산합니다.
                .roe(reportCode == DartReportCode.ANNUAL ? metricCalculator.percentage(netIncome, equity) : null)
                .debtRatio(metricCalculator.percentage(liabilities, equity))
                .currency("KRW")
                .build();
    }

    private BigDecimal findAmount(List<DartFinancialItem> items, Predicate<DartFinancialItem> matcher) {
        return items.stream()
                .filter(matcher)
                .map(DartFinancialItem::getCurrentAmount)
                .map(this::parseAmount)
                .filter(value -> value != null)
                .findFirst()
                .orElse(null);
    }

    private boolean isRevenue(DartFinancialItem item) {
        return accountMatches(item, "ifrs-full_Revenue", "매출액", "영업수익", "수익(매출액)");
    }

    private boolean isOperatingProfit(DartFinancialItem item) {
        return accountMatches(item, "dart_OperatingIncomeLoss", "영업이익", "영업이익(손실)");
    }

    private boolean isNetIncome(DartFinancialItem item) {
        return accountMatches(item, "ifrs-full_ProfitLoss", "당기순이익", "당기순이익(손실)");
    }

    private boolean accountMatches(DartFinancialItem item, String accountId, String... accountNames) {
        if (accountId.equals(item.getAccountId())) {
            return true;
        }
        return item.getAccountName() != null && Set.of(accountNames).contains(item.getAccountName().trim());
    }

    /** DART 금액의 쉼표, 공백, 괄호 음수 표기를 BigDecimal로 정규화합니다. */
    private BigDecimal parseAmount(String value) {
        if (value == null || value.isBlank() || "-".equals(value.trim())) {
            return null;
        }
        String normalized = value.replace(",", "").replace(" ", "").trim();
        if (normalized.startsWith("(") && normalized.endsWith(")")) {
            normalized = "-" + normalized.substring(1, normalized.length() - 1);
        }
        try {
            return new BigDecimal(normalized);
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private String classifyEvent(String reportName) {
        if (reportName == null) return "OTHER";
        if (reportName.contains("유상증자") || reportName.contains("무상증자")) return "CAPITAL_INCREASE";
        if (reportName.contains("합병") || reportName.contains("분할")) return "REORGANIZATION";
        if (reportName.contains("배당")) return "DIVIDEND";
        if (reportName.contains("최대주주")) return "OWNERSHIP_CHANGE";
        if (reportName.contains("영업정지") || reportName.contains("회생")) return "RISK_EVENT";
        if (reportName.contains("사업보고서") || reportName.contains("분기보고서") || reportName.contains("반기보고서")) return "PERIODIC_REPORT";
        return "OTHER";
    }

    private void validateDartStatus(String status) {
        if (!SUCCESS_STATUS.equals(status)) {
            throw new BusinessException(ErrorCode.STOCK_DATA_PROVIDER_FAILURE);
        }
    }

    private void validateConfiguration() {
        if (apiKey.isBlank()) {
            throw new BusinessException(ErrorCode.STOCK_API_NOT_CONFIGURED);
        }
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class DartDisclosureApiResponse {
        private String status;

        @JsonProperty("list")
        private List<DartDisclosureItem> items;
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class DartDisclosureItem {
        @JsonProperty("rcept_no") private String receiptNumber;
        @JsonProperty("report_nm") private String reportName;
        @JsonProperty("corp_name") private String corporationName;
        @JsonProperty("flr_nm") private String submitterName;
        @JsonProperty("rcept_dt") private String receiptDate;
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class DartFinancialApiResponse {
        private String status;

        @JsonProperty("list")
        private List<DartFinancialItem> items;
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class DartFinancialItem {
        @JsonProperty("account_id") private String accountId;
        @JsonProperty("account_nm") private String accountName;
        @JsonProperty("thstrm_amount") private String currentAmount;
    }
}
