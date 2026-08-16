package com.example.invest_mate_ai.auth.service;

import com.example.invest_mate_ai.auth.client.GoogleOAuthClient;
import com.example.invest_mate_ai.auth.client.KakaoOAuthClient;
import com.example.invest_mate_ai.auth.client.NaverOAuthClient;
import com.example.invest_mate_ai.auth.client.OAuthClient;
import com.example.invest_mate_ai.auth.dto.response.OAuthTokenResponse;
import com.example.invest_mate_ai.auth.dto.response.OAuthUserInfo;
import com.example.invest_mate_ai.auth.type.OAuthProvider;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class OAuthService {

    private final Map<OAuthProvider, OAuthClient> clients;

    public OAuthService(GoogleOAuthClient googleOAuthClient,
                        NaverOAuthClient naverOAuthClient,
                        KakaoOAuthClient kakaoOAuthClient) {

        this.clients = Map.of(
                OAuthProvider.GOOGLE, googleOAuthClient,
                OAuthProvider.NAVER, naverOAuthClient,
                OAuthProvider.KAKAO, kakaoOAuthClient
        );
    }

    public String createLoginUrl(OAuthProvider provider) {

        OAuthClient client = getClient(provider);

        return client.createAuthorizationUrl();
    }

    public OAuthUserInfo login(OAuthProvider provider, String code) {

        OAuthClient client = getClient(provider);

        OAuthTokenResponse tokenResponse = client.getAccessToken(code);

        return client.getUserInfo(tokenResponse.getAccessToken());
    }

    private OAuthClient getClient(OAuthProvider provider) {

        OAuthClient client = clients.get(provider);

        if (client == null) {
            throw new IllegalArgumentException(
                    "지원하지 않는 OAuth Provider: " + provider
            );
        }

        return client;
    }
}