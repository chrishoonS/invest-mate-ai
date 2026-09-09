package com.example.invest_mate_ai.stock.calculator;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/** ROE와 부채비율에 공통 사용되는 백분율 계산 경계값을 검증합니다. */
class FinancialMetricCalculatorTest {

    private final FinancialMetricCalculator calculator = new FinancialMetricCalculator();

    @Test
    void calculatesPercentageWithTwoDecimalPlaces() {
        assertThat(calculator.percentage(new BigDecimal("28"), new BigDecimal("380")))
                .isEqualByComparingTo("7.37");
    }

    @Test
    void zeroDenominatorReturnsNull() {
        assertThat(calculator.percentage(BigDecimal.TEN, BigDecimal.ZERO)).isNull();
    }
}
