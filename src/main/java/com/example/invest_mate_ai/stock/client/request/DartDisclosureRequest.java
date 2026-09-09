package com.example.invest_mate_ai.stock.client.request;

import com.example.invest_mate_ai.common.http.QueryParameterSource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public record DartDisclosureRequest(
        String corporationCode, LocalDate from, LocalDate to, int limit
) implements QueryParameterSource {

    @Override
    public MultiValueMap<String, String> queryParameters() {

        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();

        parameters.add("corp_code", corporationCode);
        parameters.add("bgn_de", from.format(DateTimeFormatter.BASIC_ISO_DATE));
        parameters.add("end_de", to.format(DateTimeFormatter.BASIC_ISO_DATE));
        parameters.add("page_count", Integer.toString(Math.max(1, Math.min(limit, 100))));
        parameters.add("sort", "date");
        parameters.add("sort_mth", "desc");

        return parameters;
    }
}
