package com.example.invest_mate_ai.auth.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OAuthLoginResponse {

    private boolean registered;

    private String message;

    private Long userId;
}