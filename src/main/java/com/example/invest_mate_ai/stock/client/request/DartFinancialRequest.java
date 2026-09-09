package com.example.invest_mate_ai.stock.client.request;

import com.example.invest_mate_ai.common.http.QueryParameterSource;
import com.example.invest_mate_ai.stock.type.DartReportCode;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public record DartFinancialRequest(
        String corporationCode, int businessYear, DartReportCode reportCode
) implements QueryParameterSource {

    @Override
    public MultiValueMap<String, String> queryParameters() {

        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();

        parameters.add("corp_code", corporationCode);
        parameters.add("bsns_year", Integer.toString(businessYear));
        parameters.add("reprt_code", reportCode.getCode());
        parameters.add("fs_div", "CFS");
        return parameters;
    }
}
