package com.example.invest_mate_ai.auth.type;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum OAuthProvider {
    GOOGLE,
    NAVER,
    KAKAO;

    @JsonCreator
    public static OAuthProvider converToProvider(String value) {
        return OAuthProvider.valueOf(value.toUpperCase());
    }

}
