package com.example.invest_mate_ai.auth.service.impl;

import com.example.invest_mate_ai.auth.client.OAuthClient;
import com.example.invest_mate_ai.auth.client.OAuthClientRegistry;
import com.example.invest_mate_ai.auth.dto.response.OAuthLoginResponse;
import com.example.invest_mate_ai.auth.dto.response.OAuthTokenResponse;
import com.example.invest_mate_ai.auth.dto.response.OAuthUserInfo;
import com.example.invest_mate_ai.auth.service.OAuthService;
import com.example.invest_mate_ai.auth.type.OAuthProvider;
import com.example.invest_mate_ai.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuthServiceImpl implements OAuthService {

    private final OAuthClientRegistry clientRegistry;
    private final UserService userService;

    @Override
    public String createLoginUrl(OAuthProvider provider) {
        return getClient(provider).createAuthorizationUrl();
    }

    @Override
    public OAuthLoginResponse login(OAuthProvider provider, String code) {
        OAuthClient client = getClient(provider);
        OAuthTokenResponse tokenResponse = client.getAccessToken(code);
        OAuthUserInfo userInfo = client.getUserInfo(tokenResponse.getAccessToken());
        return userService.loginOrRegister(userInfo);
    }

    private OAuthClient getClient(OAuthProvider provider) {
        return clientRegistry.get(provider);
    }
}
