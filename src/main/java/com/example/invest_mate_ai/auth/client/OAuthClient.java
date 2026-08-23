package com.example.invest_mate_ai.auth.client;

import com.example.invest_mate_ai.auth.dto.response.OAuthTokenResponse;
import com.example.invest_mate_ai.auth.dto.response.OAuthUserInfo;
import com.example.invest_mate_ai.auth.type.OAuthProvider;

public interface OAuthClient {

    OAuthProvider provider();

    String createAuthorizationUrl();

    OAuthTokenResponse getAccessToken(String code);

    OAuthUserInfo getUserInfo(String accessToken);
}
