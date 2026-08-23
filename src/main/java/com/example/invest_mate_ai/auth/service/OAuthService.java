package com.example.invest_mate_ai.auth.service;

import com.example.invest_mate_ai.auth.dto.response.OAuthLoginResponse;
import com.example.invest_mate_ai.auth.type.OAuthProvider;
import com.example.invest_mate_ai.identity.dto.request.IdentityVerifyRequest;

public interface OAuthService {

    String createLoginUrl(OAuthProvider provider);

    OAuthLoginResponse login(OAuthProvider provider, String code);

    OAuthLoginResponse verifyIdentity(String pendingLoginId, IdentityVerifyRequest identityVerifyRequest);
}
