package com.example.invest_mate_ai.stock.calculator;

import com.example.invest_mate_ai.stock.type.Sentiment;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/** 규칙 기반 뉴스 감성이 동일 입력에 항상 같은 결과를 내는지 검증합니다. */
class NewsSentimentCalculatorTest {

    private final NewsSentimentCalculator calculator = new NewsSentimentCalculator();

    @Test
    void positiveWordsProducePositiveSentiment() {
        assertThat(calculator.calculate("실적 성장과 흑자 전환", "수주 증가"))
                .isEqualTo(Sentiment.POSITIVE);
    }

    @Test
    void tiedScoresProduceNeutralSentiment() {
        assertThat(calculator.calculate("실적 증가와 부진", null))
                .isEqualTo(Sentiment.NEUTRAL);
    }
}
