package com.example.invest_mate_ai.auth.client;

import com.example.invest_mate_ai.auth.dto.response.OAuthTokenResponse;
import com.example.invest_mate_ai.auth.dto.response.OAuthUserInfo;

public interface OAuthClient {
    String createAuthorizationUrl();

    OAuthTokenResponse getAccessToken(String code);

    OAuthUserInfo getUserInfo(String accessToken);
}
