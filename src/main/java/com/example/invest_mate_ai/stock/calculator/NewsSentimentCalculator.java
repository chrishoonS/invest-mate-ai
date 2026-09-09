package com.example.invest_mate_ai.stock.calculator;

import com.example.invest_mate_ai.stock.type.Sentiment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

/**
 * AI 연동 전에도 결과를 재현할 수 있도록 키워드 점수로 뉴스 감성을 계산합니다.
 * 추후 AI 분류기를 붙이더라도 이 클래스의 인터페이스를 유지하면 서비스 변경을 줄일 수 있습니다.
 */
@Component
public class NewsSentimentCalculator {

    private static final List<String> POSITIVE_WORDS = List.of(
            "상승", "호재", "성장", "증가", "흑자", "수주", "돌파", "최대", "개선", "강세"
    );
    private static final List<String> NEGATIVE_WORDS = List.of(
            "하락", "악재", "감소", "적자", "소송", "규제", "리콜", "부진", "약세", "중단"
    );

    public Sentiment calculate(String title, String summary) {
        String text = ((title == null ? "" : title) + " " + (summary == null ? "" : summary))
                .toLowerCase(Locale.ROOT);

        long positiveScore = POSITIVE_WORDS.stream().filter(text::contains).count();
        long negativeScore = NEGATIVE_WORDS.stream().filter(text::contains).count();

        if (positiveScore > negativeScore) {
            return Sentiment.POSITIVE;
        }
        if (negativeScore > positiveScore) {
            return Sentiment.NEGATIVE;
        }
        return Sentiment.NEUTRAL;
    }
}
