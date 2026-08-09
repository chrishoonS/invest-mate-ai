package com.example.invest_mate_ai.auth.type;

public enum OAuthProvider {
    GOOGLE,
    NAVER,
    KAKAO;

    public static OAuthProvider converToProvider(String provider) {

        try {
            return valueOf(provider.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "지원하지 않는 OAuth Provider: " + provider
            );
        }
    }
}
